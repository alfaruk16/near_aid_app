package com.nearaid.core.network.di;

import com.nearaid.core.network.api.ListingApi;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import retrofit2.Retrofit;

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
public final class NetworkModule_ProvideListingApiFactory implements Factory<ListingApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideListingApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public ListingApi get() {
    return provideListingApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideListingApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideListingApiFactory(retrofitProvider);
  }

  public static ListingApi provideListingApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideListingApi(retrofit));
  }
}
