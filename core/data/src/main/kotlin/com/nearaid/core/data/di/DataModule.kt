package com.nearaid.core.data.di

import com.nearaid.core.data.repository.AuthRepositoryImpl
import com.nearaid.core.data.repository.CategoryRepositoryImpl
import com.nearaid.core.data.repository.ChatRepositoryImpl
import com.nearaid.core.data.repository.ClaimRepositoryImpl
import com.nearaid.core.data.repository.ListingRepositoryImpl
import com.nearaid.core.data.repository.NotificationRepositoryImpl
import com.nearaid.core.data.repository.PreferencesRepositoryImpl
import com.nearaid.core.data.repository.SafetyRepositoryImpl
import com.nearaid.core.data.repository.UserRepositoryImpl
import com.nearaid.core.domain.repository.AuthRepository
import com.nearaid.core.domain.repository.CategoryRepository
import com.nearaid.core.domain.repository.ChatRepository
import com.nearaid.core.domain.repository.ClaimRepository
import com.nearaid.core.domain.repository.ListingRepository
import com.nearaid.core.domain.repository.NotificationRepository
import com.nearaid.core.domain.repository.PreferencesRepository
import com.nearaid.core.domain.repository.SafetyRepository
import com.nearaid.core.domain.repository.UserRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds @Singleton abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
    @Binds @Singleton abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository
    @Binds @Singleton abstract fun bindCategoryRepository(impl: CategoryRepositoryImpl): CategoryRepository
    @Binds @Singleton abstract fun bindListingRepository(impl: ListingRepositoryImpl): ListingRepository
    @Binds @Singleton abstract fun bindClaimRepository(impl: ClaimRepositoryImpl): ClaimRepository
    @Binds @Singleton abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
    @Binds @Singleton abstract fun bindSafetyRepository(impl: SafetyRepositoryImpl): SafetyRepository
    @Binds @Singleton abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
    @Binds @Singleton abstract fun bindPreferencesRepository(impl: PreferencesRepositoryImpl): PreferencesRepository
}
