package com.nearaid.core.network.di

/**
 * Endpoints the network layer needs but can't know itself. `:app` provides the concrete
 * values from its `BuildConfig` (debug → local, release → production), so the base/WS URLs
 * differ per build type without the network layer depending on `:app`.
 */
data class NetworkConfig(
    val baseUrl: String,
    val wsUrl: String,
    val debugLogging: Boolean = false,
)
