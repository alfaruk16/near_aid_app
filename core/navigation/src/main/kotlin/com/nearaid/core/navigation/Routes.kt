package com.nearaid.core.navigation

import kotlinx.serialization.Serializable

/**
 * Type-safe navigation destinations (Navigation-Compose 2.8+). Feature modules own
 * their screen composables and register them against these routes in their own
 * NavGraphBuilder extensions, so no feature depends on another feature.
 */

// ---- Top-level graphs ----
@Serializable
data object AuthGraph

@Serializable
data object MainGraph

// ---- Auth ----
@Serializable
data object WelcomeRoute

@Serializable
data object PhoneRoute

@Serializable
data class OtpRoute(val requestId: String, val phone: String)

@Serializable
data object ProfileSetupRoute

// ---- Main bottom-nav tabs ----
@Serializable
data object HomeRoute

@Serializable
data object ActivityRoute

@Serializable
data object MessagesRoute

@Serializable
data object ProfileRoute

// ---- Detail / pushed destinations ----
@Serializable
data class ListingDetailRoute(val listingId: String)

@Serializable
data class ChatRoute(val claimId: String, val threadId: String, val title: String)

@Serializable
data object NotificationsRoute

@Serializable
data class PublicProfileRoute(val userId: String)

@Serializable
data object VerificationRoute

@Serializable
data object SettingsRoute

@Serializable
data object PostChooserRoute

@Serializable
data class CreateListingRoute(val type: String)
