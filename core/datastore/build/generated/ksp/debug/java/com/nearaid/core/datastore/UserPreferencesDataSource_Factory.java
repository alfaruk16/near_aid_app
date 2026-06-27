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
public final class UserPreferencesDataSource_Factory implements Factory<UserPreferencesDataSource> {
  private final Provider<DataStore<Preferences>> dataStoreProvider;

  public UserPreferencesDataSource_Factory(Provider<DataStore<Preferences>> dataStoreProvider) {
    this.dataStoreProvider = dataStoreProvider;
  }

  @Override
  public UserPreferencesDataSource get() {
    return newInstance(dataStoreProvider.get());
  }

  public static UserPreferencesDataSource_Factory create(
      Provider<DataStore<Preferences>> dataStoreProvider) {
    return new UserPreferencesDataSource_Factory(dataStoreProvider);
  }

  public static UserPreferencesDataSource newInstance(DataStore<Preferences> dataStore) {
    return new UserPreferencesDataSource(dataStore);
  }
}
