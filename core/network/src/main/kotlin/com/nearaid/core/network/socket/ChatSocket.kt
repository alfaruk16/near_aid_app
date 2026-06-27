package com.nearaid.core.network.socket

import com.nearaid.core.datastore.AuthPreferencesDataSource
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.MessageType
import com.nearaid.core.network.di.WsUrl
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Realtime chat transport (§10). Connects to wss://.../ws?token=…, subscribes to a
 * claim thread, and emits incoming `message.new` events as domain [ChatMessage]s.
 */
@Singleton
class ChatSocket @Inject constructor(
    private val client: OkHttpClient,
    private val json: Json,
    private val authPrefs: AuthPreferencesDataSource,
    @WsUrl private val wsUrl: String,
) {
    fun observe(threadId: String): Flow<ChatMessage> = callbackFlow {
        val token = runBlocking { authPrefs.currentTokens()?.accessToken }
        val request = Request.Builder().url("$wsUrl?token=$token").build()

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                webSocket.send("""{"event":"subscribe","thread_id":"$threadId"}""")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                val event = runCatching { json.parseToJsonElement(text).jsonObject }.getOrNull() ?: return
                if (event.string("event") != "message.new") return
                if (event.string("thread_id") != threadId) return
                val msg = event["message"]?.jsonObject ?: return
                trySend(msg.toChatMessage())
            }
        }

        val socket = client.newWebSocket(request, listener)
        awaitClose { socket.close(1000, null) }
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
