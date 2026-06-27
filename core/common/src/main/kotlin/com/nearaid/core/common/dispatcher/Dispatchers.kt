package com.nearaid.core.common.dispatcher

import javax.inject.Qualifier

/** Qualifiers so coroutine dispatchers can be injected and swapped in tests. */
@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val dispatcher: NearAidDispatcher)

enum class NearAidDispatcher { Default, IO, Main }
