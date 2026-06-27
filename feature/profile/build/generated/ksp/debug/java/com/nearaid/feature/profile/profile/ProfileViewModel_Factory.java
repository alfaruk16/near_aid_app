package com.nearaid.feature.profile.profile;

import com.nearaid.core.domain.usecase.ObserveCurrentUserUseCase;
import com.nearaid.core.domain.usecase.RefreshCurrentUserUseCase;
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
public final class ProfileViewModel_Factory implements Factory<ProfileViewModel> {
  private final Provider<ObserveCurrentUserUseCase> observeCurrentUserProvider;

  private final Provider<RefreshCurrentUserUseCase> refreshCurrentUserProvider;

  public ProfileViewModel_Factory(Provider<ObserveCurrentUserUseCase> observeCurrentUserProvider,
      Provider<RefreshCurrentUserUseCase> refreshCurrentUserProvider) {
    this.observeCurrentUserProvider = observeCurrentUserProvider;
    this.refreshCurrentUserProvider = refreshCurrentUserProvider;
  }

  @Override
  public ProfileViewModel get() {
    return newInstance(observeCurrentUserProvider.get(), refreshCurrentUserProvider.get());
  }

  public static ProfileViewModel_Factory create(
      Provider<ObserveCurrentUserUseCase> observeCurrentUserProvider,
      Provider<RefreshCurrentUserUseCase> refreshCurrentUserProvider) {
    return new ProfileViewModel_Factory(observeCurrentUserProvider, refreshCurrentUserProvider);
  }

  public static ProfileViewModel newInstance(ObserveCurrentUserUseCase observeCurrentUser,
      RefreshCurrentUserUseCase refreshCurrentUser) {
    return new ProfileViewModel(observeCurrentUser, refreshCurrentUser);
  }
}
