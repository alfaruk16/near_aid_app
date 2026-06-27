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
public final class UpdateProfileUseCase_Factory implements Factory<UpdateProfileUseCase> {
  private final Provider<UserRepository> repositoryProvider;

  public UpdateProfileUseCase_Factory(Provider<UserRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public UpdateProfileUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static UpdateProfileUseCase_Factory create(Provider<UserRepository> repositoryProvider) {
    return new UpdateProfileUseCase_Factory(repositoryProvider);
  }

  public static UpdateProfileUseCase newInstance(UserRepository repository) {
    return new UpdateProfileUseCase(repository);
  }
}
