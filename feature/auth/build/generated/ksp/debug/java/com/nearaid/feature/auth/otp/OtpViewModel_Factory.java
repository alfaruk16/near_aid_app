package com.nearaid.feature.auth.otp;

import com.nearaid.core.domain.usecase.RefreshCurrentUserUseCase;
import com.nearaid.core.domain.usecase.VerifyOtpUseCase;
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
public final class OtpViewModel_Factory implements Factory<OtpViewModel> {
  private final Provider<VerifyOtpUseCase> verifyOtpProvider;

  private final Provider<RefreshCurrentUserUseCase> refreshCurrentUserProvider;

  public OtpViewModel_Factory(Provider<VerifyOtpUseCase> verifyOtpProvider,
      Provider<RefreshCurrentUserUseCase> refreshCurrentUserProvider) {
    this.verifyOtpProvider = verifyOtpProvider;
    this.refreshCurrentUserProvider = refreshCurrentUserProvider;
  }

  @Override
  public OtpViewModel get() {
    return newInstance(verifyOtpProvider.get(), refreshCurrentUserProvider.get());
  }

  public static OtpViewModel_Factory create(Provider<VerifyOtpUseCase> verifyOtpProvider,
      Provider<RefreshCurrentUserUseCase> refreshCurrentUserProvider) {
    return new OtpViewModel_Factory(verifyOtpProvider, refreshCurrentUserProvider);
  }

  public static OtpViewModel newInstance(VerifyOtpUseCase verifyOtp,
      RefreshCurrentUserUseCase refreshCurrentUser) {
    return new OtpViewModel(verifyOtp, refreshCurrentUser);
  }
}
