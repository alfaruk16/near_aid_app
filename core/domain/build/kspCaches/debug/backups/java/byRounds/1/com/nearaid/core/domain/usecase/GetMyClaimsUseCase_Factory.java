package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.ClaimRepository;
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
public final class GetMyClaimsUseCase_Factory implements Factory<GetMyClaimsUseCase> {
  private final Provider<ClaimRepository> repositoryProvider;

  public GetMyClaimsUseCase_Factory(Provider<ClaimRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetMyClaimsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetMyClaimsUseCase_Factory create(Provider<ClaimRepository> repositoryProvider) {
    return new GetMyClaimsUseCase_Factory(repositoryProvider);
  }

  public static GetMyClaimsUseCase newInstance(ClaimRepository repository) {
    return new GetMyClaimsUseCase(repository);
  }
}
