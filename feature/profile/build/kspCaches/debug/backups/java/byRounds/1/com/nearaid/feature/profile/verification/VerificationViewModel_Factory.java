package com.nearaid.feature.profile.verification;

import com.nearaid.core.domain.usecase.SubmitVerificationUseCase;
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
public final class VerificationViewModel_Factory implements Factory<VerificationViewModel> {
  private final Provider<SubmitVerificationUseCase> submitVerificationProvider;

  public VerificationViewModel_Factory(
      Provider<SubmitVerificationUseCase> submitVerificationProvider) {
    this.submitVerificationProvider = submitVerificationProvider;
  }

  @Override
  public VerificationViewModel get() {
    return newInstance(submitVerificationProvider.get());
  }

  public static VerificationViewModel_Factory create(
      Provider<SubmitVerificationUseCase> submitVerificationProvider) {
    return new VerificationViewModel_Factory(submitVerificationProvider);
  }

  public static VerificationViewModel newInstance(SubmitVerificationUseCase submitVerification) {
    return new VerificationViewModel(submitVerification);
  }
}
