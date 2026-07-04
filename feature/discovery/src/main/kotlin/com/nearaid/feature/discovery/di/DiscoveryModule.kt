package com.nearaid.feature.discovery.di

import com.nearaid.feature.discovery.home.HomeViewModel
import com.nearaid.feature.discovery.listingdetail.ListingDetailViewModel
import com.nearaid.feature.discovery.notifications.NotificationsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val discoveryModule = module {
    viewModelOf(::HomeViewModel)
    viewModelOf(::ListingDetailViewModel)
    viewModelOf(::NotificationsViewModel)
}
