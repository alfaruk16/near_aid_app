package com.nearaid.feature.profile.publicprofile;

import com.nearaid.core.domain.usecase.GetPublicUserUseCase;
import com.nearaid.core.domain.usecase.GetUserRatingsUseCase;
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
public final class PublicProfileViewModel_Factory implements Factory<PublicProfileViewModel> {
  private final Provider<GetPublicUserUseCase> getPublicUserProvider;

  private final Provider<GetUserRatingsUseCase> getUserRatingsProvider;

  public PublicProfileViewModel_Factory(Provider<GetPublicUserUseCase> getPublicUserProvider,
      Provider<GetUserRatingsUseCase> getUserRatingsProvider) {
    this.getPublicUserProvider = getPublicUserProvider;
    this.getUserRatingsProvider = getUserRatingsProvider;
  }

  @Override
  public PublicProfileViewModel get() {
    return newInstance(getPublicUserProvider.get(), getUserRatingsProvider.get());
  }

  public static PublicProfileViewModel_Factory create(
      Provider<GetPublicUserUseCase> getPublicUserProvider,
      Provider<GetUserRatingsUseCase> getUserRatingsProvider) {
    return new PublicProfileViewModel_Factory(getPublicUserProvider, getUserRatingsProvider);
  }

  public static PublicProfileViewModel newInstance(GetPublicUserUseCase getPublicUser,
      GetUserRatingsUseCase getUserRatings) {
    return new PublicProfileViewModel(getPublicUser, getUserRatings);
  }
}
