package com.nearaid.core.data.repository;

import com.nearaid.core.datastore.AuthPreferencesDataSource;
import com.nearaid.core.network.api.AuthApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import kotlinx.coroutines.CoroutineDispatcher;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("com.nearaid.core.common.dispatcher.Dispatcher")
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<AuthApi> authApiProvider;

  private final Provider<AuthPreferencesDataSource> authPrefsProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public AuthRepositoryImpl_Factory(Provider<AuthApi> authApiProvider,
      Provider<AuthPreferencesDataSource> authPrefsProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.authApiProvider = authApiProvider;
    this.authPrefsProvider = authPrefsProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(authApiProvider.get(), authPrefsProvider.get(), ioDispatcherProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<AuthApi> authApiProvider,
      Provider<AuthPreferencesDataSource> authPrefsProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new AuthRepositoryImpl_Factory(authApiProvider, authPrefsProvider, ioDispatcherProvider);
  }

  public static AuthRepositoryImpl newInstance(AuthApi authApi, AuthPreferencesDataSource authPrefs,
      CoroutineDispatcher ioDispatcher) {
    return new AuthRepositoryImpl(authApi, authPrefs, ioDispatcher);
  }
}
