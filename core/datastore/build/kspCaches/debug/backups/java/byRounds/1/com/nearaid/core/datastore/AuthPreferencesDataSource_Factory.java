package com.nearaid.core.datastore;

import androidx.datastore.core.DataStore;
import androidx.datastore.preferences.core.Preferences;
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
public final class AuthPreferencesDataSource_Factory implements Factory<AuthPreferencesDataSource> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public AuthPreferencesDataSource_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public AuthPreferencesDataSource get() {
    return newInstance(dataStoreProvider.get());
  }

  public static AuthPreferencesDataSource_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new AuthPreferencesDataSource_Factory(dataStoreProvider);
  }

  public static AuthPreferencesDataSource newInstance(DataStore<Preferences> dataStore) {
    return new AuthPreferencesDataSource(dataStore);
  }
}
