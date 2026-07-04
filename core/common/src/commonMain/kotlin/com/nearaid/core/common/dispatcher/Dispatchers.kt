package com.nearaid.core.common.dispatcher

import kotlinx.coroutines.CoroutineDispatcher

/**
 * The dispatcher for blocking IO. `Dispatchers.IO` exists only on JVM/Android, so it is
 * supplied per-platform: `Dispatchers.IO` on Android, `Dispatchers.Default` on iOS.
 * Exposed to the DI graph via `commonModule` under `named("io")`.
 */
internal expect val ioDispatcher: CoroutineDispatcher
