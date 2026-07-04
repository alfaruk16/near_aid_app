package com.nearaid.core.model

actual fun platform(): String = "Android ${android.os.Build.VERSION.SDK_INT}"
