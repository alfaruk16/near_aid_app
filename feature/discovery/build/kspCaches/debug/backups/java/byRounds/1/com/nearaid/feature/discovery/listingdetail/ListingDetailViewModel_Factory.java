package com.nearaid.feature.discovery.listingdetail;

import com.nearaid.core.domain.usecase.BlockUserUseCase;
import com.nearaid.core.domain.usecase.ClaimListingUseCase;
import com.nearaid.core.domain.usecase.GetListingUseCase;
import com.nearaid.core.domain.usecase.ReportUseCase;
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
public final class ListingDetailViewModel_Factory implements Factory<ListingDetailViewModel> {
  private final Provider<GetListingUseCase> getListingProvider;

  private final Provider<ClaimListingUseCase> claimListingProvider;

  private final Provider<ReportUseCase> reportProvider;

  private final Provider<BlockUserUseCase> blockUserProvider;

  public ListingDetailViewModel_Factory(Provider<GetListingUseCase> getListingProvider,
      Provider<ClaimListingUseCase> claimListingProvider, Provider<ReportUseCase> reportProvider,
      Provider<BlockUserUseCase> blockUserProvider) {
    this.getListingProvider = getListingProvider;
    this.claimListingProvider = claimListingProvider;
    this.reportProvider = reportProvider;
    this.blockUserProvider = blockUserProvider;
  }

  @Override
  public ListingDetailViewModel get() {
    return newInstance(getListingProvider.get(), claimListingProvider.get(), reportProvider.get(), blockUserProvider.get());
  }

  public static ListingDetailViewModel_Factory create(
      Provider<GetListingUseCase> getListingProvider,
      Provider<ClaimListingUseCase> claimListingProvider, Provider<ReportUseCase> reportProvider,
      Provider<BlockUserUseCase> blockUserProvider) {
    return new ListingDetailViewModel_Factory(getListingProvider, claimListingProvider, reportProvider, blockUserProvider);
  }

  public static ListingDetailViewModel newInstance(GetListingUseCase getListing,
      ClaimListingUseCase claimListing, ReportUseCase report, BlockUserUseCase blockUser) {
    return new ListingDetailViewModel(getListing, claimListing, report, blockUser);
  }
}
