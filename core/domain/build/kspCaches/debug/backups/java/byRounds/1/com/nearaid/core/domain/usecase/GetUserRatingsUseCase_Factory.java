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
public final class GetUserRatingsUseCase_Factory implements Factory<GetUserRatingsUseCase> {
  private final Provider<UserRepository> repositoryProvider;

  public GetUserRatingsUseCase_Factory(Provider<UserRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetUserRatingsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetUserRatingsUseCase_Factory create(Provider<UserRepository> repositoryProvider) {
    return new GetUserRatingsUseCase_Factory(repositoryProvider);
  }

  public static GetUserRatingsUseCase newInstance(UserRepository repository) {
    return new GetUserRatingsUseCase(repository);
  }
}
