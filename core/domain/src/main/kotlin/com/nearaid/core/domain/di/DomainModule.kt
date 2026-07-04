package com.nearaid.core.domain.di

import com.nearaid.core.domain.usecase.BlockUserUseCase
import com.nearaid.core.domain.usecase.CancelListingUseCase
import com.nearaid.core.domain.usecase.ClaimListingUseCase
import com.nearaid.core.domain.usecase.ConfirmReceiptUseCase
import com.nearaid.core.domain.usecase.CreateListingUseCase
import com.nearaid.core.domain.usecase.GetBlockedUsersUseCase
import com.nearaid.core.domain.usecase.GetConversationsUseCase
import com.nearaid.core.domain.usecase.GetListingUseCase
import com.nearaid.core.domain.usecase.GetMessagesUseCase
import com.nearaid.core.domain.usecase.GetMyClaimsUseCase
import com.nearaid.core.domain.usecase.GetMyListingsUseCase
import com.nearaid.core.domain.usecase.GetNearbyListingsUseCase
import com.nearaid.core.domain.usecase.GetNotificationsUseCase
import com.nearaid.core.domain.usecase.GetPublicUserUseCase
import com.nearaid.core.domain.usecase.GetUserRatingsUseCase
import com.nearaid.core.domain.usecase.LogoutUseCase
import com.nearaid.core.domain.usecase.MarkDeliveredUseCase
import com.nearaid.core.domain.usecase.MarkNotificationsReadUseCase
import com.nearaid.core.domain.usecase.MarkThreadReadUseCase
import com.nearaid.core.domain.usecase.ObserveCategoriesUseCase
import com.nearaid.core.domain.usecase.ObserveCurrentUserUseCase
import com.nearaid.core.domain.usecase.ObserveLanguageUseCase
import com.nearaid.core.domain.usecase.ObserveLoginStateUseCase
import com.nearaid.core.domain.usecase.ObserveSearchRadiusUseCase
import com.nearaid.core.domain.usecase.ObserveSessionUseCase
import com.nearaid.core.domain.usecase.ObserveThreadUseCase
import com.nearaid.core.domain.usecase.RateClaimUseCase
import com.nearaid.core.domain.usecase.RefreshCategoriesUseCase
import com.nearaid.core.domain.usecase.RefreshCurrentUserUseCase
import com.nearaid.core.domain.usecase.RegisterDeviceUseCase
import com.nearaid.core.domain.usecase.ReportUseCase
import com.nearaid.core.domain.usecase.RequestOtpUseCase
import com.nearaid.core.domain.usecase.SendMessageUseCase
import com.nearaid.core.domain.usecase.SetLanguageUseCase
import com.nearaid.core.domain.usecase.SetSearchRadiusUseCase
import com.nearaid.core.domain.usecase.SubmitVerificationUseCase
import com.nearaid.core.domain.usecase.UnblockUserUseCase
import com.nearaid.core.domain.usecase.UpdateProfileUseCase
import com.nearaid.core.domain.usecase.VerifyOtpUseCase
import com.nearaid.core.domain.usecase.WithdrawClaimUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

/** Use cases are stateless wrappers over repositories → registered as factories. */
val domainModule = module {
    // Auth
    factoryOf(::RequestOtpUseCase)
    factoryOf(::VerifyOtpUseCase)
    factoryOf(::LogoutUseCase)
    factoryOf(::ObserveLoginStateUseCase)
    factoryOf(::ObserveSessionUseCase)
    // Category
    factoryOf(::ObserveCategoriesUseCase)
    factoryOf(::RefreshCategoriesUseCase)
    // Chat
    factoryOf(::GetConversationsUseCase)
    factoryOf(::GetMessagesUseCase)
    factoryOf(::SendMessageUseCase)
    factoryOf(::MarkThreadReadUseCase)
    factoryOf(::ObserveThreadUseCase)
    // Claim
    factoryOf(::ClaimListingUseCase)
    factoryOf(::WithdrawClaimUseCase)
    factoryOf(::MarkDeliveredUseCase)
    factoryOf(::ConfirmReceiptUseCase)
    factoryOf(::RateClaimUseCase)
    factoryOf(::GetMyClaimsUseCase)
    // Listing
    factoryOf(::GetNearbyListingsUseCase)
    factoryOf(::GetListingUseCase)
    factoryOf(::CreateListingUseCase)
    factoryOf(::CancelListingUseCase)
    factoryOf(::GetMyListingsUseCase)
    // Notification
    factoryOf(::GetNotificationsUseCase)
    factoryOf(::MarkNotificationsReadUseCase)
    // Preferences
    factoryOf(::ObserveLanguageUseCase)
    factoryOf(::SetLanguageUseCase)
    factoryOf(::ObserveSearchRadiusUseCase)
    factoryOf(::SetSearchRadiusUseCase)
    // Safety
    factoryOf(::ReportUseCase)
    factoryOf(::BlockUserUseCase)
    factoryOf(::UnblockUserUseCase)
    factoryOf(::GetBlockedUsersUseCase)
    // User
    factoryOf(::ObserveCurrentUserUseCase)
    factoryOf(::RefreshCurrentUserUseCase)
    factoryOf(::UpdateProfileUseCase)
    factoryOf(::GetPublicUserUseCase)
    factoryOf(::GetUserRatingsUseCase)
    factoryOf(::SubmitVerificationUseCase)
    factoryOf(::RegisterDeviceUseCase)
}
