package com.nearaid.core.network.interceptor;

import com.nearaid.core.datastore.AuthPreferencesDataSource;
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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<AuthPreferencesDataSource> authPrefsProvider;

  public AuthInterceptor_Factory(Provider<AuthPreferencesDataSource> authPrefsProvider) {
    this.authPrefsProvider = authPrefsProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(authPrefsProvider.get());
  }

  public static AuthInterceptor_Factory create(
      Provider<AuthPreferencesDataSource> authPrefsProvider) {
    return new AuthInterceptor_Factory(authPrefsProvider);
  }

  public static AuthInterceptor newInstance(AuthPreferencesDataSource authPrefs) {
    return new AuthInterceptor(authPrefs);
  }
}
