package com.nearaid.core.data.repository;

import com.nearaid.core.database.dao.ListingCacheDao;
import com.nearaid.core.network.api.ListingApi;
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
public final class ListingRepositoryImpl_Factory implements Factory<ListingRepositoryImpl> {
  private final Provider<ListingApi> listingApiProvider;

  private final Provider<ListingCacheDao> cacheDaoProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public ListingRepositoryImpl_Factory(Provider<ListingApi> listingApiProvider,
      Provider<ListingCacheDao> cacheDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.listingApiProvider = listingApiProvider;
    this.cacheDaoProvider = cacheDaoProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public ListingRepositoryImpl get() {
    return newInstance(listingApiProvider.get(), cacheDaoProvider.get(), ioDispatcherProvider.get());
  }

  public static ListingRepositoryImpl_Factory create(Provider<ListingApi> listingApiProvider,
      Provider<ListingCacheDao> cacheDaoProvider,
      Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new ListingRepositoryImpl_Factory(listingApiProvider, cacheDaoProvider, ioDispatcherProvider);
  }

  public static ListingRepositoryImpl newInstance(ListingApi listingApi, ListingCacheDao cacheDao,
      CoroutineDispatcher ioDispatcher) {
    return new ListingRepositoryImpl(listingApi, cacheDao, ioDispatcher);
  }
}
