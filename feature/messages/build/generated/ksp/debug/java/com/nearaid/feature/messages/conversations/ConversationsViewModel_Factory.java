package com.nearaid.feature.messages.conversations;

import com.nearaid.core.domain.usecase.GetConversationsUseCase;
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
public final class ConversationsViewModel_Factory implements Factory<ConversationsViewModel> {
  private final Provider<GetConversationsUseCase> getConversationsProvider;

  public ConversationsViewModel_Factory(
      Provider<GetConversationsUseCase> getConversationsProvider) {
    this.getConversationsProvider = getConversationsProvider;
  }

  @Override
  public ConversationsViewModel get() {
    return newInstance(getConversationsProvider.get());
  }

  public static ConversationsViewModel_Factory create(
      Provider<GetConversationsUseCase> getConversationsProvider) {
    return new ConversationsViewModel_Factory(getConversationsProvider);
  }

  public static ConversationsViewModel newInstance(GetConversationsUseCase getConversations) {
    return new ConversationsViewModel(getConversations);
  }
}
