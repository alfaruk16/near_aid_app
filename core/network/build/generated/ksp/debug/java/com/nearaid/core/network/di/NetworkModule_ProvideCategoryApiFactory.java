package com.nearaid.core.network.di;

import com.nearaid.core.network.api.CategoryApi;
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
public final class NetworkModule_ProvideCategoryApiFactory implements Factory<CategoryApi> {
  private final Provider<Retrofit> retrofitProvider;

  public NetworkModule_ProvideCategoryApiFactory(Provider<Retrofit> retrofitProvider) {
    this.retrofitProvider = retrofitProvider;
  }

  @Override
  public CategoryApi get() {
    return provideCategoryApi(retrofitProvider.get());
  }

  public static NetworkModule_ProvideCategoryApiFactory create(
      Provider<Retrofit> retrofitProvider) {
    return new NetworkModule_ProvideCategoryApiFactory(retrofitProvider);
  }

  public static CategoryApi provideCategoryApi(Retrofit retrofit) {
    return Preconditions.checkNotNullFromProvides(NetworkModule.INSTANCE.provideCategoryApi(retrofit));
  }
}
