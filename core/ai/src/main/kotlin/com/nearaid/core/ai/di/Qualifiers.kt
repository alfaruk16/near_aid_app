package com.nearaid.core.ai.di

import javax.inject.Qualifier

/** The on-device semantic embedder (MediaPipe/TFLite). May be unavailable if the model asset is missing. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Semantic

/** The dependency-free lexical embedder used as a zero-setup fallback. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Lexical
