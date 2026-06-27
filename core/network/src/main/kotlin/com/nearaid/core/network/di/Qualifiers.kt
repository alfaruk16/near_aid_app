package com.nearaid.core.network.di

import javax.inject.Qualifier

/**
 * Qualifier for the REST base URL. The `:app` module binds the concrete value from
 * its `BuildConfig.BASE_URL` so it differs per build type (debug → local, release →
 * production) without the network layer knowing.
 */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BaseUrl

/** Qualifier for the realtime WebSocket base URL (§10). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class WsUrl
