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
public final class ConfirmReceiptUseCase_Factory implements Factory<ConfirmReceiptUseCase> {
  private final Provider<ClaimRepository> repositoryProvider;

  public ConfirmReceiptUseCase_Factory(Provider<ClaimRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public ConfirmReceiptUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static ConfirmReceiptUseCase_Factory create(Provider<ClaimRepository> repositoryProvider) {
    return new ConfirmReceiptUseCase_Factory(repositoryProvider);
  }

  public static ConfirmReceiptUseCase newInstance(ClaimRepository repository) {
    return new ConfirmReceiptUseCase(repository);
  }
}
