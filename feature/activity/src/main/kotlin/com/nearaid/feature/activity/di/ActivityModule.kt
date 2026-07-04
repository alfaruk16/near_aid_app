package com.nearaid.feature.activity.di

import com.nearaid.feature.activity.ActivityViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val activityModule = module {
    viewModelOf(::ActivityViewModel)
}
