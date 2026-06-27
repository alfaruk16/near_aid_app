package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.AuthRepository;
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
public final class ObserveSessionUseCase_Factory implements Factory<ObserveSessionUseCase> {
  private final Provider<AuthRepository> authRepositoryProvider;

  private final Provider<UserRepository> userRepositoryProvider;

  public ObserveSessionUseCase_Factory(Provider<AuthRepository> authRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    this.authRepositoryProvider = authRepositoryProvider;
    this.userRepositoryProvider = userRepositoryProvider;
  }

  @Override
  public ObserveSessionUseCase get() {
    return newInstance(authRepositoryProvider.get(), userRepositoryProvider.get());
  }

  public static ObserveSessionUseCase_Factory create(
      Provider<AuthRepository> authRepositoryProvider,
      Provider<UserRepository> userRepositoryProvider) {
    return new ObserveSessionUseCase_Factory(authRepositoryProvider, userRepositoryProvider);
  }

  public static ObserveSessionUseCase newInstance(AuthRepository authRepository,
      UserRepository userRepository) {
    return new ObserveSessionUseCase(authRepository, userRepository);
  }
}
