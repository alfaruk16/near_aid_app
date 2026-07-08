package com.nearaid.core.model

/**
 * Walking-skeleton probe for the KMP toolchain: returns a human-readable identifier of the
 * platform the shared code is running on. Proves `expect`/`actual` resolves on both targets.
 */
expect fun platform(): String
