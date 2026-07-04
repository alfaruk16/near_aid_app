package com.nearaid.feature.auth.di

import com.nearaid.feature.auth.otp.OtpViewModel
import com.nearaid.feature.auth.phone.PhoneViewModel
import com.nearaid.feature.auth.profilesetup.ProfileSetupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    viewModelOf(::PhoneViewModel)
    viewModelOf(::OtpViewModel)
    viewModelOf(::ProfileSetupViewModel)
}
