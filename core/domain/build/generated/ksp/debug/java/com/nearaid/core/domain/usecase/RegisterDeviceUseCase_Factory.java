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
public final class RegisterDeviceUseCase_Factory implements Factory<RegisterDeviceUseCase> {
  private final Provider<UserRepository> repositoryProvider;

  public RegisterDeviceUseCase_Factory(Provider<UserRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RegisterDeviceUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RegisterDeviceUseCase_Factory create(Provider<UserRepository> repositoryProvider) {
    return new RegisterDeviceUseCase_Factory(repositoryProvider);
  }

  public static RegisterDeviceUseCase newInstance(UserRepository repository) {
    return new RegisterDeviceUseCase(repository);
  }
}
