package com.nearaid.core.network.socket

import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Realtime chat transport (§10). Connects to wss://.../ws?token=…, subscribes to a
 * claim thread, and emits incoming `message.new` events as domain [ChatMessage]s.
 */
class ChatSocket(
    private val client: HttpClient,
    private val json: Json,
    private val authPrefs: AuthPreferencesDataSource,
    private val wsUrl: String,
) {
    fun observe(threadId: String): Flow<ChatMessage> = callbackFlow {
        val token = authPrefs.currentTokens()?.accessToken
        // Realtime transport is best-effort: message history loads over REST, so a
        // failed upgrade (server 404, unreachable host, expired token) must degrade
        // gracefully rather than crash the collector. Complete the flow instead.
        val session = try {
            client.webSocketSession("$wsUrl?token=$token").also {
                it.send(Frame.Text("""{"event":"subscribe","thread_id":"$threadId"}"""))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            close()
            return@callbackFlow
        }

        val reader = launch {
            try {
                for (frame in session.incoming) {
                    val text = (frame as? Frame.Text)?.readText() ?: continue
                    val event = runCatching { json.parseToJsonElement(text).jsonObject }.getOrNull() ?: continue
                    if (event.string("event") != "message.new") continue
                    if (event.string("thread_id") != threadId) continue
                    val msg = event["message"]?.jsonObject ?: continue
                    trySend(msg.toChatMessage())
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Socket dropped/errored mid-stream — stop emitting, don't propagate.
            } finally {
                close()
            }
        }

        awaitClose {
            reader.cancel()
            (session as CoroutineScope).cancel()
        }
    }

    private fun JsonObject.string(key: String): String? = this[key]?.jsonPrimitive?.contentOrNull

    private fun JsonObject.toChatMessage(): ChatMessage = ChatMessage(
        id = string("id").orEmpty(),
        senderId = string("sender_id").orEmpty(),
        type = if (string("type") == "image") MessageType.IMAGE else MessageType.TEXT,
        body = string("body"),
        imageUrl = string("image_url"),
        createdAt = string("created_at").orEmpty(),
        readAt = string("read_at"),
    )
}
