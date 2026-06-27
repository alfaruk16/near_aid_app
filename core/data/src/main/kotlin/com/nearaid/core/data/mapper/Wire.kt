package com.nearaid.core.data.mapper

import com.nearaid.core.model.ClaimStatus
import com.nearaid.core.model.ListingStatus
import com.nearaid.core.model.ListingType
import com.nearaid.core.model.Urgency

fun ListingType.wire(): String = if (this == ListingType.OFFER) "offer" else "request"
fun Urgency.wire(): String = name.lowercase()
fun ListingStatus.wire(): String = name.lowercase()
fun ClaimStatus.wire(): String = name.lowercase()
