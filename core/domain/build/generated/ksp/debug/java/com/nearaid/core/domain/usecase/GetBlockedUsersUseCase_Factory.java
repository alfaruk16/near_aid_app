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
public final class GetBlockedUsersUseCase_Factory implements Factory<GetBlockedUsersUseCase> {
  private final Provider<SafetyRepository> repositoryProvider;

  public GetBlockedUsersUseCase_Factory(Provider<SafetyRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetBlockedUsersUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetBlockedUsersUseCase_Factory create(
      Provider<SafetyRepository> repositoryProvider) {
    return new GetBlockedUsersUseCase_Factory(repositoryProvider);
  }

  public static GetBlockedUsersUseCase newInstance(SafetyRepository repository) {
    return new GetBlockedUsersUseCase(repository);
  }
}
