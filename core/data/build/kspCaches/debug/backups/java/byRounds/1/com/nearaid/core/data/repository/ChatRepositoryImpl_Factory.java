package com.nearaid.core.data.repository;

import com.nearaid.core.database.dao.ConversationCacheDao;
import com.nearaid.core.network.api.ChatApi;
import com.nearaid.core.network.api.ClaimApi;
import com.nearaid.core.network.socket.ChatSocket;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.nearaid.core.common.dispatcher.Dispatcher")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class ChatRepositoryImpl_Factory implements Factory<ChatRepositoryImpl> {
  private final Provider<ChatApi> chatApiProvider;

  private final Provider<ClaimApi> claimApiProvider;

  private final Provider<ChatSocket> chatSocketProvider;

  private final Provider<ConversationCacheDao> cacheDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public ChatRepositoryImpl_Factory(Provider<ChatApi> chatApiProvider,
      Provider<ClaimApi> claimApiProvider, Provider<ChatSocket> chatSocketProvider,
      Provider<ConversationCacheDao> cacheDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.chatApiProvider = chatApiProvider;
    this.claimApiProvider = claimApiProvider;
    this.chatSocketProvider = chatSocketProvider;
    this.cacheDaoProvider = cacheDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public ChatRepositoryImpl get() {
    return newInstance(chatApiProvider.get(), claimApiProvider.get(), chatSocketProvider.get(), cacheDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static ChatRepositoryImpl_Factory create(Provider<ChatApi> chatApiProvider,
      Provider<ClaimApi> claimApiProvider, Provider<ChatSocket> chatSocketProvider,
      Provider<ConversationCacheDao> cacheDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new ChatRepositoryImpl_Factory(chatApiProvider, claimApiProvider, chatSocketProvider, cacheDaoProvider, ioDispatcherProvider);
  }

  public static ChatRepositoryImpl newInstance(ChatApi chatApi, ClaimApi claimApi,
      ChatSocket chatSocket, ConversationCacheDao cacheDao, CoroutineDispatcher ioDispatcher) {
    return new ChatRepositoryImpl(chatApi, claimApi, chatSocket, cacheDao, ioDispatcher);
  }
}
