package com.nearaid.core.data.repository

import com.nearaid.core.network.util.safeApiCall
import com.nearaid.core.network.api.SafetyApi
import com.nearaid.core.network.dto.BlockBody
import com.nearaid.core.network.dto.ReportBody
import com.nearaid.core.data.mapper.toDomain
import com.nearaid.core.model.PublicUser
import com.nearaid.core.model.ReportTargetType
import com.nearaid.core.domain.repository.SafetyRepository
import com.nearaid.core.common.result.DataResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class SafetyRepositoryImpl(
    private val safetyApi: SafetyApi,
    private val ioDispatcher: CoroutineDispatcher,
) : SafetyRepository {

    override suspend fun report(targetType: ReportTargetType, targetId: String, reason: String): DataResult<Unit> =
        withContext(ioDispatcher) {
            safeApiCall { safetyApi.report(ReportBody(targetType.wire, targetId, reason)) }
        }

    override suspend fun block(userId: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { safetyApi.block(BlockBody(userId)) } }

    override suspend fun unblock(userId: String): DataResult<Unit> =
        withContext(ioDispatcher) { safeApiCall { safetyApi.unblock(userId) } }

    override suspend fun getBlockedUsers(): DataResult<List<PublicUser>> =
        withContext(ioDispatcher) {
            safeApiCall { safetyApi.getBlocked().results.map { it.toDomain() } }
        }
}
