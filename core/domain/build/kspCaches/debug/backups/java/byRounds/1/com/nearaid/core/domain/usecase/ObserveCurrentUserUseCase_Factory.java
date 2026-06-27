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
public final class ObserveCurrentUserUseCase_Factory implements Factory<ObserveCurrentUserUseCase> {
  private final Provider<UserRepository> repositoryProvider;

  public ObserveCurrentUserUseCase_Factory(Provider<UserRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ObserveCurrentUserUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ObserveCurrentUserUseCase_Factory create(
      Provider<UserRepository> repositoryProvider) {
    return new ObserveCurrentUserUseCase_Factory(repositoryProvider);
  }

  public static ObserveCurrentUserUseCase newInstance(UserRepository repository) {
    return new ObserveCurrentUserUseCase(repository);
  }
}
