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
public final class WithdrawClaimUseCase_Factory implements Factory<WithdrawClaimUseCase> {
  private final Provider<ClaimRepository> repositoryProvider;

  public WithdrawClaimUseCase_Factory(Provider<ClaimRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public WithdrawClaimUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static WithdrawClaimUseCase_Factory create(Provider<ClaimRepository> repositoryProvider) {
    return new WithdrawClaimUseCase_Factory(repositoryProvider);
  }

  public static WithdrawClaimUseCase newInstance(ClaimRepository repository) {
    return new WithdrawClaimUseCase(repository);
  }
}
