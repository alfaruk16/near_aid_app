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
public final class ObserveThreadUseCase_Factory implements Factory<ObserveThreadUseCase> {
  private final Provider<ChatRepository> repositoryProvider;

  public ObserveThreadUseCase_Factory(Provider<ChatRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveThreadUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveThreadUseCase_Factory create(Provider<ChatRepository> repositoryProvider) {
    return new ObserveThreadUseCase_Factory(repositoryProvider);
  }

  public static ObserveThreadUseCase newInstance(ChatRepository repository) {
    return new ObserveThreadUseCase(repository);
  }
}
