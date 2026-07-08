package com.nearaid.feature.post.di

import com.nearaid.feature.post.create.CreateListingViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val postModule = module {
    viewModelOf(::CreateListingViewModel)
}
