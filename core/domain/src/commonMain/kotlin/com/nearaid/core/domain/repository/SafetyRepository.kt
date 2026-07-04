package com.nearaid.core.domain.repository

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.ReportTargetType

interface SafetyRepository {
    suspend fun report(targetType: ReportTargetType, targetId: String, reason: String): DataResult<Unit>
    suspend fun block(userId: String): DataResult<Unit>
    suspend fun unblock(userId: String): DataResult<Unit>
    suspend fun getBlockedUsers(): DataResult<List<PublicUser>>
}
