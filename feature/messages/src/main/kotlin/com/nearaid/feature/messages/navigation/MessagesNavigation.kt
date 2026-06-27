package com.nearaid.feature.messages.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.nearaid.core.navigation.ChatRoute
import com.nearaid.core.navigation.MessagesRoute
import com.nearaid.feature.messages.chat.ChatScreen
import com.nearaid.feature.messages.conversations.MessagesScreen

fun NavGraphBuilder.messagesGraph(navController: NavController) {
    composable<MessagesRoute> {
        MessagesScreen(
            onOpenChat = { claimId, threadId, title ->
                navController.navigate(ChatRoute(claimId, threadId, title))
            },
        )
    }
    composable<ChatRoute> { entry ->
        val r = entry.toRoute<ChatRoute>()
        ChatScreen(
            claimId = r.claimId,
            threadId = r.threadId,
            title = r.title,
            onBack = { navController.popBackStack() },
        )
    }
}
