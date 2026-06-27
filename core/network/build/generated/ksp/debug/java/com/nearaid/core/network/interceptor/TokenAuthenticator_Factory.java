package com.nearaid.core.network.interceptor;

import com.nearaid.core.datastore.AuthPreferencesDataSource;
import com.nearaid.core.network.api.AuthApi;
import dagger.Lazy;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
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
public final class TokenAuthenticator_Factory implements Factory<TokenAuthenticator> {
  private final Provider<AuthPreferencesDataSource> authPrefsProvider;

  private final Provider<AuthApi> authApiProvider;

  public TokenAuthenticator_Factory(Provider<AuthPreferencesDataSource> authPrefsProvider,
      Provider<AuthApi> authApiProvider) {
    this.authPrefsProvider = authPrefsProvider;
    this.authApiProvider = authApiProvider;
  }

  @Override
  public TokenAuthenticator get() {
    return newInstance(authPrefsProvider.get(), DoubleCheck.lazy(authApiProvider));
  }

  public static TokenAuthenticator_Factory create(
      Provider<AuthPreferencesDataSource> authPrefsProvider, Provider<AuthApi> authApiProvider) {
    return new TokenAuthenticator_Factory(authPrefsProvider, authApiProvider);
  }

  public static TokenAuthenticator newInstance(AuthPreferencesDataSource authPrefs,
      Lazy<AuthApi> authApi) {
    return new TokenAuthenticator(authPrefs, authApi);
  }
}
