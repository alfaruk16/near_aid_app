package com.nearaid.core.network.di;

import com.nearaid.core.network.api.ClaimApi;
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
public final class NetworkModule_ProvideClaimApiFactory implements Factory<ClaimApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideClaimApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public ClaimApi get() {
    return provideClaimApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideClaimApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideClaimApiFactory(retrofitProvider);
  }

  public static ClaimApi provideClaimApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideClaimApi(retrofit));
  }
}
