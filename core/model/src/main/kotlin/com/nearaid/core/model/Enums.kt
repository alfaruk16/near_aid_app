package com.nearaid.core.model

/** A listing is either a request ("I need") or an offer ("I have") — §8.1. */
enum class ListingType { REQUEST, OFFER }

enum class Urgency { LOW, MEDIUM, HIGH, CRITICAL }

/** The shared lifecycle state machine for both requests and offers — §14. */
enum class ListingStatus { OPEN, CLAIMED, DELIVERED, COMPLETED, CANCELLED, EXPIRED }

enum class ClaimStatus { ACTIVE, WITHDRAWN, COMPLETED, CANCELLED }

enum class AppLanguage(val code: String) {
    BN("bn"),
    EN("en");

    companion object {
        fun fromCode(code: String?): AppLanguage = entries.firstOrNull { it.code == code } ?: BN
    }
}

enum class AccountStatus { ACTIVE, SUSPENDED, BANNED }

enum class MessageType { TEXT, IMAGE }

enum class ReportTargetType(val wire: String) { USER("user"), LISTING("listing") }

/** Role of the current user within a claim thread — drives the contextual labels (§3, §14). */
enum class ClaimRole { HELPING, RECEIVING, GIVING, REQUESTING }
