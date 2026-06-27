package com.nearaid.core.data.repository;

import com.nearaid.core.network.api.SafetyApi;
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
public final class SafetyRepositoryImpl_Factory implements Factory<SafetyRepositoryImpl> {
  private final Provider<SafetyApi> safetyApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public SafetyRepositoryImpl_Factory(Provider<SafetyApi> safetyApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.safetyApiProvider = safetyApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public SafetyRepositoryImpl get() {
    return newInstance(safetyApiProvider.get(), ioDispatcherProvider.get());
  }

  public static SafetyRepositoryImpl_Factory create(Provider<SafetyApi> safetyApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new SafetyRepositoryImpl_Factory(safetyApiProvider, ioDispatcherProvider);
  }

  public static SafetyRepositoryImpl newInstance(SafetyApi safetyApi,
      CoroutineDispatcher ioDispatcher) {
    return new SafetyRepositoryImpl(safetyApi, ioDispatcher);
  }
}
