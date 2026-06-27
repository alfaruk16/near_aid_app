package com.nearaid.feature.profile.settings;

import com.nearaid.core.domain.usecase.LogoutUseCase;
import com.nearaid.core.domain.usecase.ObserveLanguageUseCase;
import com.nearaid.core.domain.usecase.SetLanguageUseCase;
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
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<LogoutUseCase> logoutProvider;

  private final Provider<ObserveLanguageUseCase> observeLanguageProvider;

  private final Provider<SetLanguageUseCase> setLanguageProvider;

  private final Provider<UpdateProfileUseCase> updateProfileProvider;

  public SettingsViewModel_Factory(Provider<LogoutUseCase> logoutProvider,
      Provider<ObserveLanguageUseCase> observeLanguageProvider,
      Provider<SetLanguageUseCase> setLanguageProvider,
      Provider<UpdateProfileUseCase> updateProfileProvider) {
    this.logoutProvider = logoutProvider;
    this.observeLanguageProvider = observeLanguageProvider;
    this.setLanguageProvider = setLanguageProvider;
    this.updateProfileProvider = updateProfileProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(logoutProvider.get(), observeLanguageProvider.get(), setLanguageProvider.get(), updateProfileProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<LogoutUseCase> logoutProvider,
      Provider<ObserveLanguageUseCase> observeLanguageProvider,
      Provider<SetLanguageUseCase> setLanguageProvider,
      Provider<UpdateProfileUseCase> updateProfileProvider) {
    return new SettingsViewModel_Factory(logoutProvider, observeLanguageProvider, setLanguageProvider, updateProfileProvider);
  }

  public static SettingsViewModel newInstance(LogoutUseCase logout,
      ObserveLanguageUseCase observeLanguage, SetLanguageUseCase setLanguage,
      UpdateProfileUseCase updateProfile) {
    return new SettingsViewModel(logout, observeLanguage, setLanguage, updateProfile);
  }
}
