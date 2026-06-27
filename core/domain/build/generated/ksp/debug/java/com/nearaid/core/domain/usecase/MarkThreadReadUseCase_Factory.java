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
public final class MarkThreadReadUseCase_Factory implements Factory<MarkThreadReadUseCase> {
  private final Provider<ChatRepository> repositoryProvider;

  public MarkThreadReadUseCase_Factory(Provider<ChatRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public MarkThreadReadUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static MarkThreadReadUseCase_Factory create(Provider<ChatRepository> repositoryProvider) {
    return new MarkThreadReadUseCase_Factory(repositoryProvider);
  }

  public static MarkThreadReadUseCase newInstance(ChatRepository repository) {
    return new MarkThreadReadUseCase(repository);
  }
}
