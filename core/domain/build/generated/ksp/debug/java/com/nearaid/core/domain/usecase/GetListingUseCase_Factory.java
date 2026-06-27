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
public final class GetListingUseCase_Factory implements Factory<GetListingUseCase> {
  private final Provider<ListingRepository> repositoryProvider;

  public GetListingUseCase_Factory(Provider<ListingRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetListingUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetListingUseCase_Factory create(Provider<ListingRepository> repositoryProvider) {
    return new GetListingUseCase_Factory(repositoryProvider);
  }

  public static GetListingUseCase newInstance(ListingRepository repository) {
    return new GetListingUseCase(repository);
  }
}
