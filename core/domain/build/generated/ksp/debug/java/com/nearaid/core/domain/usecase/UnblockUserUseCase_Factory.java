package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.SafetyRepository;
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
public final class UnblockUserUseCase_Factory implements Factory<UnblockUserUseCase> {
  private final Provider<SafetyRepository> repositoryProvider;

  public UnblockUserUseCase_Factory(Provider<SafetyRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UnblockUserUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UnblockUserUseCase_Factory create(Provider<SafetyRepository> repositoryProvider) {
    return new UnblockUserUseCase_Factory(repositoryProvider);
  }

  public static UnblockUserUseCase newInstance(SafetyRepository repository) {
    return new UnblockUserUseCase(repository);
  }
}
