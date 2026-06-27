package com.nearaid.feature.post.create;

import com.nearaid.core.domain.usecase.CreateListingUseCase;
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase;
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
public final class CreateListingViewModel_Factory implements Factory<CreateListingViewModel> {
  private final Provider<ObserveCategoriesUseCase> observeCategoriesProvider;

  private final Provider<RefreshCategoriesUseCase> refreshCategoriesProvider;

  private final Provider<CreateListingUseCase> createListingProvider;

  public CreateListingViewModel_Factory(
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<RefreshCategoriesUseCase> refreshCategoriesProvider,
      Provider<CreateListingUseCase> createListingProvider) {
    this.observeCategoriesProvider = observeCategoriesProvider;
    this.refreshCategoriesProvider = refreshCategoriesProvider;
    this.createListingProvider = createListingProvider;
  }

  @Override
  public CreateListingViewModel get() {
    return newInstance(observeCategoriesProvider.get(), refreshCategoriesProvider.get(), createListingProvider.get());
  }

  public static CreateListingViewModel_Factory create(
      Provider<ObserveCategoriesUseCase> observeCategoriesProvider,
      Provider<RefreshCategoriesUseCase> refreshCategoriesProvider,
      Provider<CreateListingUseCase> createListingProvider) {
    return new CreateListingViewModel_Factory(observeCategoriesProvider, refreshCategoriesProvider, createListingProvider);
  }

  public static CreateListingViewModel newInstance(ObserveCategoriesUseCase observeCategories,
      RefreshCategoriesUseCase refreshCategories, CreateListingUseCase createListing) {
    return new CreateListingViewModel(observeCategories, refreshCategories, createListing);
  }
}
