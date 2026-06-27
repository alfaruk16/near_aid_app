package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.AuthRepository;
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
public final class ObserveLoginStateUseCase_Factory implements Factory<ObserveLoginStateUseCase> {
  private final Provider<AuthRepository> repositoryProvider;

  public ObserveLoginStateUseCase_Factory(Provider<AuthRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveLoginStateUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveLoginStateUseCase_Factory create(
      Provider<AuthRepository> repositoryProvider) {
    return new ObserveLoginStateUseCase_Factory(repositoryProvider);
  }

  public static ObserveLoginStateUseCase newInstance(AuthRepository repository) {
    return new ObserveLoginStateUseCase(repository);
  }
}
