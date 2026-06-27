package com.nearaid.core.data.repository;

import com.nearaid.core.datastore.UserPreferencesDataSource;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class PreferencesRepositoryImpl_Factory implements Factory<PreferencesRepositoryImpl> {
  private final Provider<UserPreferencesDataSource> userPrefsProvider;

  public PreferencesRepositoryImpl_Factory(Provider<UserPreferencesDataSource> userPrefsProvider) {
    this.userPrefsProvider = userPrefsProvider;
  }

  @Override
  public PreferencesRepositoryImpl get() {
    return newInstance(userPrefsProvider.get());
  }

  public static PreferencesRepositoryImpl_Factory create(
      Provider<UserPreferencesDataSource> userPrefsProvider) {
    return new PreferencesRepositoryImpl_Factory(userPrefsProvider);
  }

  public static PreferencesRepositoryImpl newInstance(UserPreferencesDataSource userPrefs) {
    return new PreferencesRepositoryImpl(userPrefs);
  }
}
