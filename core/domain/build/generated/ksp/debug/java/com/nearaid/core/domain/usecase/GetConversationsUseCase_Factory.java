package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.ChatRepository;
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
public final class GetConversationsUseCase_Factory implements Factory<GetConversationsUseCase> {
  private final Provider<ChatRepository> repositoryProvider;

  public GetConversationsUseCase_Factory(Provider<ChatRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetConversationsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetConversationsUseCase_Factory create(
      Provider<ChatRepository> repositoryProvider) {
    return new GetConversationsUseCase_Factory(repositoryProvider);
  }

  public static GetConversationsUseCase newInstance(ChatRepository repository) {
    return new GetConversationsUseCase(repository);
  }
}
