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
public final class RateClaimUseCase_Factory implements Factory<RateClaimUseCase> {
  private final Provider<ClaimRepository> repositoryProvider;

  public RateClaimUseCase_Factory(Provider<ClaimRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public RateClaimUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static RateClaimUseCase_Factory create(Provider<ClaimRepository> repositoryProvider) {
    return new RateClaimUseCase_Factory(repositoryProvider);
  }

  public static RateClaimUseCase newInstance(ClaimRepository repository) {
    return new RateClaimUseCase(repository);
  }
}
