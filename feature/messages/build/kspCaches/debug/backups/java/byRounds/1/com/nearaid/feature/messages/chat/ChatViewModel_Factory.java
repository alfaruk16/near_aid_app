package com.nearaid.feature.messages.chat;

import com.nearaid.core.domain.usecase.GetMessagesUseCase;
import com.nearaid.core.domain.usecase.MarkThreadReadUseCase;
import com.nearaid.core.domain.usecase.ObserveCurrentUserUseCase;
import com.nearaid.core.domain.usecase.ObserveThreadUseCase;
import com.nearaid.core.domain.usecase.SendMessageUseCase;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata
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
public final class ChatViewModel_Factory implements Factory<ChatViewModel> {
  private final Provider<GetMessagesUseCase> getMessagesProvider;

  private final Provider<SendMessageUseCase> sendMessageProvider;

  private final Provider<MarkThreadReadUseCase> markThreadReadProvider;

  private final Provider<ObserveThreadUseCase> observeThreadProvider;

  private final Provider<ObserveCurrentUserUseCase> observeCurrentUserProvider;

  public ChatViewModel_Factory(Provider<GetMessagesUseCase> getMessagesProvider,
      Provider<SendMessageUseCase> sendMessageProvider,
      Provider<MarkThreadReadUseCase> markThreadReadProvider,
      Provider<ObserveThreadUseCase> observeThreadProvider,
      Provider<ObserveCurrentUserUseCase> observeCurrentUserProvider) {
    this.getMessagesProvider = getMessagesProvider;
    this.sendMessageProvider = sendMessageProvider;
    this.markThreadReadProvider = markThreadReadProvider;
    this.observeThreadProvider = observeThreadProvider;
    this.observeCurrentUserProvider = observeCurrentUserProvider;
  }

  @Override
  public ChatViewModel get() {
    return newInstance(getMessagesProvider.get(), sendMessageProvider.get(), markThreadReadProvider.get(), observeThreadProvider.get(), observeCurrentUserProvider.get());
  }

  public static ChatViewModel_Factory create(Provider<GetMessagesUseCase> getMessagesProvider,
      Provider<SendMessageUseCase> sendMessageProvider,
      Provider<MarkThreadReadUseCase> markThreadReadProvider,
      Provider<ObserveThreadUseCase> observeThreadProvider,
      Provider<ObserveCurrentUserUseCase> observeCurrentUserProvider) {
    return new ChatViewModel_Factory(getMessagesProvider, sendMessageProvider, markThreadReadProvider, observeThreadProvider, observeCurrentUserProvider);
  }

  public static ChatViewModel newInstance(GetMessagesUseCase getMessages,
      SendMessageUseCase sendMessage, MarkThreadReadUseCase markThreadRead,
      ObserveThreadUseCase observeThread, ObserveCurrentUserUseCase observeCurrentUser) {
    return new ChatViewModel(getMessages, sendMessage, markThreadRead, observeThread, observeCurrentUser);
  }
}
