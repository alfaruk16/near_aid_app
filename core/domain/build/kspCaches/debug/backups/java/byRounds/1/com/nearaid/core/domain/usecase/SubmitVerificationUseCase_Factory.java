package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.UserRepository;
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
public final class SubmitVerificationUseCase_Factory implements Factory<SubmitVerificationUseCase> {
  private final Provider<UserRepository> repositoryProvider;

  public SubmitVerificationUseCase_Factory(Provider<UserRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public SubmitVerificationUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static SubmitVerificationUseCase_Factory create(
      Provider<UserRepository> repositoryProvider) {
    return new SubmitVerificationUseCase_Factory(repositoryProvider);
  }

  public static SubmitVerificationUseCase newInstance(UserRepository repository) {
    return new SubmitVerificationUseCase(repository);
  }
}
