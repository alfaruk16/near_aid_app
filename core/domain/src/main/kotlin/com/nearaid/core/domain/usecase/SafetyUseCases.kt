package com.nearaid.core.domain.usecase

import com.nearaid.core.common.result.DataResult
import com.nearaid.core.domain.repository.SafetyRepository
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.ReportTargetType

class ReportUseCase(
    private val repository: SafetyRepository,
) {
    suspend operator fun invoke(targetType: ReportTargetType, targetId: String, reason: String): DataResult<Unit> =
        repository.report(targetType, targetId, reason)
}

class BlockUserUseCase(
    private val repository: SafetyRepository,
) {
    suspend operator fun invoke(userId: String): DataResult<Unit> = repository.block(userId)
}

class UnblockUserUseCase(
    private val repository: SafetyRepository,
) {
    suspend operator fun invoke(userId: String): DataResult<Unit> = repository.unblock(userId)
}

class GetBlockedUsersUseCase(
    private val repository: SafetyRepository,
) {
    suspend operator fun invoke(): DataResult<List<PublicUser>> = repository.getBlockedUsers()
}
