package com.nearaid.feature.activity;

import com.nearaid.core.domain.usecase.ConfirmReceiptUseCase;
import com.nearaid.core.domain.usecase.GetMyClaimsUseCase;
import com.nearaid.core.domain.usecase.GetMyListingsUseCase;
import com.nearaid.core.domain.usecase.MarkDeliveredUseCase;
import com.nearaid.core.domain.usecase.WithdrawClaimUseCase;
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
public final class ActivityViewModel_Factory implements Factory<ActivityViewModel> {
  private final Provider<GetMyClaimsUseCase> getMyClaimsProvider;

  private final Provider<GetMyListingsUseCase> getMyListingsProvider;

  private final Provider<MarkDeliveredUseCase> markDeliveredProvider;

  private final Provider<ConfirmReceiptUseCase> confirmReceiptProvider;

  private final Provider<WithdrawClaimUseCase> withdrawClaimProvider;

  public ActivityViewModel_Factory(Provider<GetMyClaimsUseCase> getMyClaimsProvider,
      Provider<GetMyListingsUseCase> getMyListingsProvider,
      Provider<MarkDeliveredUseCase> markDeliveredProvider,
      Provider<ConfirmReceiptUseCase> confirmReceiptProvider,
      Provider<WithdrawClaimUseCase> withdrawClaimProvider) {
    this.getMyClaimsProvider = getMyClaimsProvider;
    this.getMyListingsProvider = getMyListingsProvider;
    this.markDeliveredProvider = markDeliveredProvider;
    this.confirmReceiptProvider = confirmReceiptProvider;
    this.withdrawClaimProvider = withdrawClaimProvider;
  }

  @Override
  public ActivityViewModel get() {
    return newInstance(getMyClaimsProvider.get(), getMyListingsProvider.get(), markDeliveredProvider.get(), confirmReceiptProvider.get(), withdrawClaimProvider.get());
  }

  public static ActivityViewModel_Factory create(Provider<GetMyClaimsUseCase> getMyClaimsProvider,
      Provider<GetMyListingsUseCase> getMyListingsProvider,
      Provider<MarkDeliveredUseCase> markDeliveredProvider,
      Provider<ConfirmReceiptUseCase> confirmReceiptProvider,
      Provider<WithdrawClaimUseCase> withdrawClaimProvider) {
    return new ActivityViewModel_Factory(getMyClaimsProvider, getMyListingsProvider, markDeliveredProvider, confirmReceiptProvider, withdrawClaimProvider);
  }

  public static ActivityViewModel newInstance(GetMyClaimsUseCase getMyClaims,
      GetMyListingsUseCase getMyListings, MarkDeliveredUseCase markDelivered,
      ConfirmReceiptUseCase confirmReceipt, WithdrawClaimUseCase withdrawClaim) {
    return new ActivityViewModel(getMyClaims, getMyListings, markDelivered, confirmReceipt, withdrawClaim);
  }
}
