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
public final class ClaimListingUseCase_Factory implements Factory<ClaimListingUseCase> {
  private final Provider<ClaimRepository> repositoryProvider;

  public ClaimListingUseCase_Factory(Provider<ClaimRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ClaimListingUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ClaimListingUseCase_Factory create(Provider<ClaimRepository> repositoryProvider) {
    return new ClaimListingUseCase_Factory(repositoryProvider);
  }

  public static ClaimListingUseCase newInstance(ClaimRepository repository) {
    return new ClaimListingUseCase(repository);
  }
}
