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
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get(), get(), get(named("io"))) }
    single<UserRepository> { UserRepositoryImpl(get(), get(named("io"))) }
    single<CategoryRepository> { CategoryRepositoryImpl(get(), get(named("io"))) }
    single<ListingRepository> { ListingRepositoryImpl(get(), get(), get(named("io"))) }
    single<ClaimRepository> { ClaimRepositoryImpl(get(), get(), get(named("io"))) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get(), get(), get(), get(named("io"))) }
    single<SafetyRepository> { SafetyRepositoryImpl(get(), get(named("io"))) }
    single<NotificationRepository> { NotificationRepositoryImpl(get(), get(named("io"))) }
    singleOf(::PreferencesRepositoryImpl) { bind<PreferencesRepository>() }
}
