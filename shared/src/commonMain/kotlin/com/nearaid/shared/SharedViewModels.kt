package com.nearaid.shared

import com.nearaid.feature.auth.phone.PhoneViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

/**
 * Swift-friendly access to the shared ViewModels. Resolving through Koin's `getKoin().get()` is
 * clumsy from Swift, so each ViewModel is exposed as a plain factory here. SwiftUI screens create
 * one per screen, observe its `state` and forward user actions to `onIntent`.
 */
object SharedViewModels : KoinComponent {
    fun phone(): PhoneViewModel = get()
}
