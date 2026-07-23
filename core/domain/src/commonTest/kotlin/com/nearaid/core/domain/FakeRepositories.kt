package com.nearaid.core.domain

import com.nearaid.core.common.result.AppError
import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.AuthRepository
import com.nearaid.core.domain.repository.CategoryRepository
import com.nearaid.core.domain.repository.ChatRepository
import com.nearaid.core.domain.repository.ClaimRepository
import com.nearaid.core.domain.repository.ListingRepository
import com.nearaid.core.domain.repository.NotificationRepository
import com.nearaid.core.domain.repository.PreferencesRepository
import com.nearaid.core.domain.repository.SafetyRepository
import com.nearaid.core.domain.repository.UserRepository
import com.nearaid.core.model.AppLanguage
import com.nearaid.core.model.AuthSession
import com.nearaid.core.model.Category
import com.nearaid.core.model.ChatMessage
import com.nearaid.core.model.Claim
import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.Conversation
import com.nearaid.core.model.DiscoveryQuery
import com.nearaid.core.model.ListingCard
import com.nearaid.core.model.ListingDetail
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Me
import com.nearaid.core.model.NewListing
import com.nearaid.core.model.NotificationItem
import com.nearaid.core.model.OtpChallenge
import com.nearaid.core.model.Page
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.Rating
import com.nearaid.core.model.ReportTargetType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/** Shared canned failure so tests can assert failures propagate unchanged. */
val testError: AppError = AppError.NotFound("nope")

class FakeAuthRepository : AuthRepository {
    override val isLoggedIn = MutableStateFlow(false)

    var requestOtpResult: DataResult<OtpChallenge> = DataResult.Success(OtpChallenge("req-1", 60))
    var verifyOtpResult: DataResult<AuthSession> =
        DataResult.Success(AuthSession("access", "refresh", isNewUser = false, userId = "u1"))
    var lastPhone: String? = null
    var lastRequestId: String? = null
    var lastCode: String? = null
    var logoutCalls = 0

    override suspend fun requestOtp(phone: String): DataResult<OtpChallenge> {
        lastPhone = phone
        return requestOtpResult
    }

    override suspend fun verifyOtp(requestId: String, code: String): DataResult<AuthSession> {
        lastRequestId = requestId
        lastCode = code
        return verifyOtpResult
    }

    override suspend fun logout() {
        logoutCalls++
    }
}

class FakeUserRepository : UserRepository {
    val me = MutableStateFlow<Me?>(null)
    var clearCalls = 0
    var refreshResult: DataResult<Me> = DataResult.Success(sampleMe())
    var updateResult: DataResult<Me> = DataResult.Success(sampleMe())
    var publicUserResult: DataResult<PublicUser> = DataResult.Success(samplePublicUser())
    var ratingsResult: DataResult<Page<Rating>> = DataResult.Success(Page(emptyList(), null, false))
    var verificationResult: DataResult<Unit> = DataResult.Success(Unit)
    var registerResult: DataResult<Unit> = DataResult.Success(Unit)

    // Captured args
    var lastDisplayName: String? = null
    var lastLanguage: AppLanguage? = null
    var lastPhotoUrl: String? = null
    var lastDefaultArea: String? = null
    var lastEmail: String? = null
    var lastPublicUserId: String? = null
    var lastRatingsId: String? = null
    var lastRatingsCursor: String? = null
    var lastDocumentPath: String? = null
    var lastFcmToken: String? = null

    override fun observeMe(): Flow<Me?> = me

    override fun clear() {
        clearCalls++
    }

    override suspend fun refreshMe(): DataResult<Me> = refreshResult

    override suspend fun updateProfile(
        displayName: String?,
        language: AppLanguage?,
        photoUrl: String?,
        defaultArea: String?,
        email: String?,
    ): DataResult<Me> {
        lastDisplayName = displayName
        lastLanguage = language
        lastPhotoUrl = photoUrl
        lastDefaultArea = defaultArea
        lastEmail = email
        return updateResult
    }

    override suspend fun getPublicUser(id: String): DataResult<PublicUser> {
        lastPublicUserId = id
        return publicUserResult
    }

    override suspend fun getUserRatings(id: String, cursor: String?): DataResult<Page<Rating>> {
        lastRatingsId = id
        lastRatingsCursor = cursor
        return ratingsResult
    }

    override suspend fun submitVerification(documentPath: String): DataResult<Unit> {
        lastDocumentPath = documentPath
        return verificationResult
    }

    override suspend fun registerDevice(fcmToken: String): DataResult<Unit> {
        lastFcmToken = fcmToken
        return registerResult
    }
}

class FakeCategoryRepository : CategoryRepository {
    val categories = MutableStateFlow<List<Category>>(emptyList())
    var refreshResult: DataResult<List<Category>> = DataResult.Success(emptyList())

    override fun observeCategories(): Flow<List<Category>> = categories
    override suspend fun refreshCategories(): DataResult<List<Category>> = refreshResult
}

class FakeChatRepository : ChatRepository {
    var conversationsResult: DataResult<Page<Conversation>> = DataResult.Success(Page(emptyList(), null, false))
    var messagesResult: DataResult<Page<ChatMessage>> = DataResult.Success(Page(emptyList(), null, false))
    var sendResult: DataResult<ChatMessage> = DataResult.Success(sampleMessage())
    var markReadResult: DataResult<Unit> = DataResult.Success(Unit)
    val thread = MutableStateFlow(sampleMessage())

    var lastConversationsCursor: String? = null
    var lastMessagesClaimId: String? = null
    var lastMessagesCursor: String? = null
    var lastSendClaimId: String? = null
    var lastSendBody: String? = null
    var lastMarkReadClaimId: String? = null
    var lastObserveThreadId: String? = null

    override suspend fun getConversations(cursor: String?): DataResult<Page<Conversation>> {
        lastConversationsCursor = cursor
        return conversationsResult
    }

    override suspend fun getMessages(claimId: String, cursor: String?): DataResult<Page<ChatMessage>> {
        lastMessagesClaimId = claimId
        lastMessagesCursor = cursor
        return messagesResult
    }

    override suspend fun sendMessage(claimId: String, body: String): DataResult<ChatMessage> {
        lastSendClaimId = claimId
        lastSendBody = body
        return sendResult
    }

    override suspend fun markRead(claimId: String): DataResult<Unit> {
        lastMarkReadClaimId = claimId
        return markReadResult
    }

    override fun observeThread(threadId: String): Flow<ChatMessage> {
        lastObserveThreadId = threadId
        return thread
    }
}

class FakeClaimRepository : ClaimRepository {
    var claimResult: DataResult<Claim> = DataResult.Success(sampleClaim())
    var withdrawResult: DataResult<Unit> = DataResult.Success(Unit)
    var markDeliveredResult: DataResult<Unit> = DataResult.Success(Unit)
    var confirmReceiptResult: DataResult<Unit> = DataResult.Success(Unit)
    var rateResult: DataResult<Unit> = DataResult.Success(Unit)
    var myClaimsResult: DataResult<List<Claim>> = DataResult.Success(emptyList())

    var lastClaimListingId: String? = null
    var lastWithdrawId: String? = null
    var lastMarkDeliveredId: String? = null
    var lastConfirmReceiptId: String? = null
    var lastRateClaimId: String? = null
    var lastRateScore: Int? = null
    var lastRateComment: String? = null
    var lastMyClaimsStatus: ClaimStatus? = null

    override suspend fun claim(listingId: String): DataResult<Claim> {
        lastClaimListingId = listingId
        return claimResult
    }

    override suspend fun withdraw(claimId: String): DataResult<Unit> {
        lastWithdrawId = claimId
        return withdrawResult
    }

    override suspend fun markDelivered(claimId: String): DataResult<Unit> {
        lastMarkDeliveredId = claimId
        return markDeliveredResult
    }

    override suspend fun confirmReceipt(claimId: String): DataResult<Unit> {
        lastConfirmReceiptId = claimId
        return confirmReceiptResult
    }

    override suspend fun rate(claimId: String, score: Int, comment: String?): DataResult<Unit> {
        lastRateClaimId = claimId
        lastRateScore = score
        lastRateComment = comment
        return rateResult
    }

    override suspend fun getMyClaims(status: ClaimStatus?): DataResult<List<Claim>> {
        lastMyClaimsStatus = status
        return myClaimsResult
    }
}

class FakeListingRepository : ListingRepository {
    var nearbyResult: DataResult<Page<ListingCard>> = DataResult.Success(Page(emptyList(), null, false))
    var listingResult: DataResult<ListingDetail> = DataResult.Success(sampleListingDetail())
    var createResult: DataResult<ListingDetail> = DataResult.Success(sampleListingDetail())
    var cancelResult: DataResult<Unit> = DataResult.Success(Unit)
    var myListingsResult: DataResult<List<ListingCard>> = DataResult.Success(emptyList())

    var lastNearbyQuery: DiscoveryQuery? = null
    var lastNearbyCursor: String? = null
    var lastGetListingId: String? = null
    var lastCreateInput: NewListing? = null
    var lastCancelId: String? = null
    var lastCancelReason: String? = null
    var lastMyListingsType: ListingType? = null
    var lastMyListingsStatus: ListingStatus? = null

    override suspend fun getNearby(query: DiscoveryQuery, cursor: String?): DataResult<Page<ListingCard>> {
        lastNearbyQuery = query
        lastNearbyCursor = cursor
        return nearbyResult
    }

    override suspend fun getListing(id: String): DataResult<ListingDetail> {
        lastGetListingId = id
        return listingResult
    }

    override suspend fun createListing(input: NewListing): DataResult<ListingDetail> {
        lastCreateInput = input
        return createResult
    }

    override suspend fun cancelListing(id: String, reason: String): DataResult<Unit> {
        lastCancelId = id
        lastCancelReason = reason
        return cancelResult
    }

    override suspend fun getMyListings(type: ListingType, status: ListingStatus?): DataResult<List<ListingCard>> {
        lastMyListingsType = type
        lastMyListingsStatus = status
        return myListingsResult
    }
}

class FakeNotificationRepository : NotificationRepository {
    var notificationsResult: DataResult<List<NotificationItem>> = DataResult.Success(emptyList())
    var markAllReadResult: DataResult<Unit> = DataResult.Success(Unit)
    var markAllReadCalls = 0

    override suspend fun getNotifications(): DataResult<List<NotificationItem>> = notificationsResult

    override suspend fun markAllRead(): DataResult<Unit> {
        markAllReadCalls++
        return markAllReadResult
    }
}

class FakePreferencesRepository : PreferencesRepository {
    override val language = MutableStateFlow(AppLanguage.BN)
    override val searchRadiusKm = MutableStateFlow(5.0)
    var lastSetLanguage: AppLanguage? = null
    var lastSetRadius: Double? = null

    override suspend fun setLanguage(language: AppLanguage) {
        lastSetLanguage = language
        this.language.value = language
    }

    override suspend fun setSearchRadiusKm(radius: Double) {
        lastSetRadius = radius
        this.searchRadiusKm.value = radius
    }
}

class FakeSafetyRepository : SafetyRepository {
    var reportResult: DataResult<Unit> = DataResult.Success(Unit)
    var blockResult: DataResult<Unit> = DataResult.Success(Unit)
    var unblockResult: DataResult<Unit> = DataResult.Success(Unit)
    var blockedUsersResult: DataResult<List<PublicUser>> = DataResult.Success(emptyList())

    var lastReportTargetType: ReportTargetType? = null
    var lastReportTargetId: String? = null
    var lastReportReason: String? = null
    var lastBlockUserId: String? = null
    var lastUnblockUserId: String? = null

    override suspend fun report(targetType: ReportTargetType, targetId: String, reason: String): DataResult<Unit> {
        lastReportTargetType = targetType
        lastReportTargetId = targetId
        lastReportReason = reason
        return reportResult
    }

    override suspend fun block(userId: String): DataResult<Unit> {
        lastBlockUserId = userId
        return blockResult
    }

    override suspend fun unblock(userId: String): DataResult<Unit> {
        lastUnblockUserId = userId
        return unblockResult
    }

    override suspend fun getBlockedUsers(): DataResult<List<PublicUser>> = blockedUsersResult
}

// --- Sample model factories -------------------------------------------------

fun sampleMe(displayName: String? = "Rahim"): Me = Me(
    id = "u1",
    phone = "+8801712345678",
    email = null,
    displayName = displayName,
    photoUrl = null,
    language = AppLanguage.BN,
    defaultArea = null,
    isPhoneVerified = true,
    isIdVerified = false,
    trustScore = 4.5,
    status = com.nearaid.core.model.AccountStatus.ACTIVE,
)

fun samplePublicUser(): PublicUser = PublicUser(
    id = "u2",
    displayName = "Karim",
    photoUrl = null,
    trustScore = 4.0,
    isIdVerified = true,
    aggregateRating = 4.2,
    completedHelpCount = 3,
)

fun sampleClaim(): Claim = Claim(
    id = "c1",
    listingId = "l1",
    listingType = ListingType.OFFER,
    status = ClaimStatus.ACTIVE,
    chatThreadId = "t1",
    claimedAt = "2026-01-01T00:00:00Z",
)

fun sampleMessage(): ChatMessage = ChatMessage(
    id = "m1",
    senderId = "u1",
    type = com.nearaid.core.model.MessageType.TEXT,
    body = "hi",
    imageUrl = null,
    createdAt = "2026-01-01T00:00:00Z",
    readAt = null,
)

fun sampleListingDetail(): ListingDetail = ListingDetail(
    id = "l1",
    type = ListingType.OFFER,
    status = ListingStatus.OPEN,
    title = "Rice",
    description = "5kg rice",
    quantity = "5kg",
    category = null,
    urgency = null,
    availableUntil = null,
    areaLabel = "Dhanmondi",
    locationFuzzed = null,
    locationExact = null,
    images = emptyList(),
    author = com.nearaid.core.model.Author("u1", "Rahim", null, 4.5, false),
    expiresAt = null,
    createdAt = "2026-01-01T00:00:00Z",
)
