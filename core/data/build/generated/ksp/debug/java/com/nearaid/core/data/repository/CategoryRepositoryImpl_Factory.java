package com.nearaid.core.data.repository;

import com.nearaid.core.network.api.CategoryApi;
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
public final class CategoryRepositoryImpl_Factory implements Factory<CategoryRepositoryImpl> {
  private final Provider<CategoryApi> categoryApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public CategoryRepositoryImpl_Factory(Provider<CategoryApi> categoryApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.categoryApiProvider = categoryApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public CategoryRepositoryImpl get() {
    return newInstance(categoryApiProvider.get(), ioDispatcherProvider.get());
  }

  public static CategoryRepositoryImpl_Factory create(Provider<CategoryApi> categoryApiProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new CategoryRepositoryImpl_Factory(categoryApiProvider, ioDispatcherProvider);
  }

  public static CategoryRepositoryImpl newInstance(CategoryApi categoryApi,
      CoroutineDispatcher ioDispatcher) {
    return new CategoryRepositoryImpl(categoryApi, ioDispatcher);
  }
}
