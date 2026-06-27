package com.nearaid.core.domain.usecase;

import com.nearaid.core.domain.repository.ListingRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class GetNearbyListingsUseCase_Factory implements Factory<GetNearbyListingsUseCase> {
  private final Provider<ListingRepository> repositoryProvider;

  public GetNearbyListingsUseCase_Factory(Provider<ListingRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetNearbyListingsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetNearbyListingsUseCase_Factory create(
      Provider<ListingRepository> repositoryProvider) {
    return new GetNearbyListingsUseCase_Factory(repositoryProvider);
  }

  public static GetNearbyListingsUseCase newInstance(ListingRepository repository) {
    return new GetNearbyListingsUseCase(repository);
  }
}
