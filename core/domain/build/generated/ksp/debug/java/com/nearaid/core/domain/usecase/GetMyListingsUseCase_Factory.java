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
public final class GetMyListingsUseCase_Factory implements Factory<GetMyListingsUseCase> {
  private final Provider<ListingRepository> repositoryProvider;

  public GetMyListingsUseCase_Factory(Provider<ListingRepository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
  }

  @Override
  public GetMyListingsUseCase get() {
    return newInstance(repositoryProvider.get());
  }

  public static GetMyListingsUseCase_Factory create(
      Provider<ListingRepository> repositoryProvider) {
    return new GetMyListingsUseCase_Factory(repositoryProvider);
  }

  public static GetMyListingsUseCase newInstance(ListingRepository repository) {
    return new GetMyListingsUseCase(repository);
  }
}
