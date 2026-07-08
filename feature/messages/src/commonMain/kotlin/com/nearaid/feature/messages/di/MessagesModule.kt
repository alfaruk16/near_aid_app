package com.nearaid.feature.messages.di

import com.nearaid.feature.messages.chat.ChatViewModel
import com.nearaid.feature.messages.conversations.ConversationsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val messagesModule = module {
    viewModelOf(::ChatViewModel)
    viewModelOf(::ConversationsViewModel)
}
