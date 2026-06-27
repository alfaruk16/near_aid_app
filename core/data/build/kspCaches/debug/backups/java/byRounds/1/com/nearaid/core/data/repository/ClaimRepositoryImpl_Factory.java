package com.nearaid.core.data.repository;

import com.nearaid.core.network.api.ClaimApi;
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
public final class ClaimRepositoryImpl_Factory implements Factory<ClaimRepositoryImpl> {
  private final Provider<ListingApi> listingApiProvider;

  private final Provider<ClaimApi> claimApiProvider;

  private final Provider<CoroutineDispatcher> ioDispatcherProvider;

  public ClaimRepositoryImpl_Factory(Provider<ListingApi> listingApiProvider,
      Provider<ClaimApi> claimApiProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    this.listingApiProvider = listingApiProvider;
    this.claimApiProvider = claimApiProvider;
    this.ioDispatcherProvider = ioDispatcherProvider;
  }

  @Override
  public ClaimRepositoryImpl get() {
    return newInstance(listingApiProvider.get(), claimApiProvider.get(), ioDispatcherProvider.get());
  }

  public static ClaimRepositoryImpl_Factory create(Provider<ListingApi> listingApiProvider,
      Provider<ClaimApi> claimApiProvider, Provider<CoroutineDispatcher> ioDispatcherProvider) {
    return new ClaimRepositoryImpl_Factory(listingApiProvider, claimApiProvider, ioDispatcherProvider);
  }

  public static ClaimRepositoryImpl newInstance(ListingApi listingApi, ClaimApi claimApi,
      CoroutineDispatcher ioDispatcher) {
    return new ClaimRepositoryImpl(listingApi, claimApi, ioDispatcher);
  }
}
