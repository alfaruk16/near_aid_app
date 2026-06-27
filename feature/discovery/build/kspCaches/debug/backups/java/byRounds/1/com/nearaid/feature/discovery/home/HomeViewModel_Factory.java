package com.nearaid.feature.discovery.home;

import com.nearaid.core.domain.usecase.GetNearbyListingsUseCase;
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase;
import com.nearaid.core.domain.usecase.ObserveSearchRadiusUseCase;
import com.nearaid.core.domain.usecase.RefreshCategoriesUseCase;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<GetNearbyListingsUseCase> getNearbyListingsProvider;

  private final Provider<ObserveCategoriesUseCase> observeCategoriesProvider;

  private final Provider<RefreshCategoriesUseCase> refreshCategoriesProvider;

  private final Provider<ObserveSearchRadiusUseCase> observeSearchRadiusProvider;

  public HomeViewModel_Factory(Provider<GetNearbyListingsUseCase> getNearbyListingsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<RefreshCategoriesUseCase> refreshCategoriesProvider,
      Provider<ObserveSearchRadiusUseCase> observeSearchRadiusProvider) {
    this.getNearbyListingsProvider = getNearbyListingsProvider;
    this.observeCategoriesProvider = observeCategoriesProvider;
    this.refreshCategoriesProvider = refreshCategoriesProvider;
    this.observeSearchRadiusProvider = observeSearchRadiusProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(getNearbyListingsProvider.get(), observeCategoriesProvider.get(), refreshCategoriesProvider.get(), observeSearchRadiusProvider.get());
  }

  public static HomeViewModel_Factory create(
      Provider<GetNearbyListingsUseCase> getNearbyListingsProvider,
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<RefreshCategoriesUseCase> refreshCategoriesProvider,
      Provider<ObserveSearchRadiusUseCase> observeSearchRadiusProvider) {
    return new HomeViewModel_Factory(getNearbyListingsProvider, observeCategoriesProvider, refreshCategoriesProvider, observeSearchRadiusProvider);
  }

  public static HomeViewModel newInstance(GetNearbyListingsUseCase getNearbyListings,
      ObserveCategoriesUseCase observeCategories, RefreshCategoriesUseCase refreshCategories,
      ObserveSearchRadiusUseCase observeSearchRadius) {
    return new HomeViewModel(getNearbyListings, observeCategories, refreshCategories, observeSearchRadius);
  }
}
