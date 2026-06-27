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
public final class CreateListingUseCase_Factory implements Factory<CreateListingUseCase> {
  private final Provider<ListingRepository> repositoryProvider;

  public CreateListingUseCase_Factory(Provider<ListingRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public CreateListingUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static CreateListingUseCase_Factory create(
      Provider<ListingRepository> repositoryProvider) {
    return new CreateListingUseCase_Factory(repositoryProvider);
  }

  public static CreateListingUseCase newInstance(ListingRepository repository) {
    return new CreateListingUseCase(repository);
  }
}
