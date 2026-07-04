package com.nearaid.core.common.di

import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * Coroutine dispatchers, injectable and swappable in tests. Repositories request the IO
 * dispatcher via `get(named("io"))`. (Replaces the former Hilt `@Dispatcher` qualifier.)
 */
val commonModule = module {
    single(named("io")) { Dispatchers.IO }
    single(named("default")) { Dispatchers.Default }
    single(named("main")) { Dispatchers.Main }
}
