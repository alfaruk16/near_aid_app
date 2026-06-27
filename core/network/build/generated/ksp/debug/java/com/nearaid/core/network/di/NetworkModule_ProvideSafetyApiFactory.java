package com.nearaid.core.network.di;

import com.nearaid.core.network.api.SafetyApi;
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
public final class NetworkModule_ProvideSafetyApiFactory implements Factory<SafetyApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideSafetyApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public SafetyApi get() {
    return provideSafetyApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideSafetyApiFactory create(Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideSafetyApiFactory(retrofitProvider);
  }

  public static SafetyApi provideSafetyApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideSafetyApi(retrofit));
  }
}
