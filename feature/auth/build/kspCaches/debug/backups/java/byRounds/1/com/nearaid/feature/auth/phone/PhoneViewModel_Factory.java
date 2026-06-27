package com.nearaid.feature.auth.phone;

import com.nearaid.core.domain.usecase.RequestOtpUseCase;
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
public final class PhoneViewModel_Factory implements Factory<PhoneViewModel> {
  private final Provider<RequestOtpUseCase> requestOtpProvider;

  public PhoneViewModel_Factory(Provider<RequestOtpUseCase> requestOtpProvider) {
    this.requestOtpProvider = requestOtpProvider;
  }

  @Override
  public PhoneViewModel get() {
    return newInstance(requestOtpProvider.get());
  }

  public static PhoneViewModel_Factory create(Provider<RequestOtpUseCase> requestOtpProvider) {
    return new PhoneViewModel_Factory(requestOtpProvider);
  }

  public static PhoneViewModel newInstance(RequestOtpUseCase requestOtp) {
    return new PhoneViewModel(requestOtp);
  }
}
