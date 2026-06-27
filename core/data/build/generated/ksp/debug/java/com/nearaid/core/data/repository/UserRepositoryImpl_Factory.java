package com.nearaid.core.data.repository;

import com.nearaid.core.network.api.UserApi;
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
public final class UserRepositoryImpl_Factory implements Factory<UserRepositoryImpl> {
  private final Provider<UserApi> userApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public UserRepositoryImpl_Factory(Provider<UserApi> userApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.userApiProvider = userApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public UserRepositoryImpl get() {
    return newInstance(userApiProvider.get(), ioDispatcherProvider.get());
  }

  public static UserRepositoryImpl_Factory create(Provider<UserApi> userApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new UserRepositoryImpl_Factory(userApiProvider, ioDispatcherProvider);
  }

  public static UserRepositoryImpl newInstance(UserApi userApi, CoroutineDispatcher ioDispatcher) {
    return new UserRepositoryImpl(userApi, ioDispatcher);
  }
}
