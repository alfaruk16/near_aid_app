package com.nearaid.feature.profile.di

import com.nearaid.feature.profile.profile.ProfileViewModel
import com.nearaid.feature.profile.publicprofile.PublicProfileViewModel
import com.nearaid.feature.profile.settings.SettingsViewModel
import com.nearaid.feature.profile.verification.VerificationViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val profileModule = module {
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::VerificationViewModel)
    viewModelOf(::PublicProfileViewModel)
}
