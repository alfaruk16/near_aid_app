package com.nearaid.core.data.repository;

import com.nearaid.core.network.api.NotificationApi;
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
public final class NotificationRepositoryImpl_Factory implements Factory<NotificationRepositoryImpl> {
  private final Provider<NotificationApi> notificationApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public NotificationRepositoryImpl_Factory(Provider<NotificationApi> notificationApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.notificationApiProvider = notificationApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public NotificationRepositoryImpl get() {
    return newInstance(notificationApiProvider.get(), ioDispatcherProvider.get());
  }

  public static NotificationRepositoryImpl_Factory create(
      Provider<NotificationApi> notificationApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new NotificationRepositoryImpl_Factory(notificationApiProvider, ioDispatcherProvider);
  }

  public static NotificationRepositoryImpl newInstance(NotificationApi notificationApi,
      CoroutineDispatcher ioDispatcher) {
    return new NotificationRepositoryImpl(notificationApi, ioDispatcher);
  }
}
