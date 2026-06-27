package com.nearaid.feature.auth.profilesetup;

import com.nearaid.core.domain.usecase.UpdateProfileUseCase;
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
public final class ProfileSetupViewModel_Factory implements Factory<ProfileSetupViewModel> {
  private final Provider<UpdateProfileUseCase> updateProfileProvider;

  public ProfileSetupViewModel_Factory(Provider<UpdateProfileUseCase> updateProfileProvider) {
    this.updateProfileProvider = updateProfileProvider;
  }

  @Override
  public ProfileSetupViewModel get() {
    return newInstance(updateProfileProvider.get());
  }

  public static ProfileSetupViewModel_Factory create(
      Provider<UpdateProfileUseCase> updateProfileProvider) {
    return new ProfileSetupViewModel_Factory(updateProfileProvider);
  }

  public static ProfileSetupViewModel newInstance(UpdateProfileUseCase updateProfile) {
    return new ProfileSetupViewModel(updateProfile);
  }
}
