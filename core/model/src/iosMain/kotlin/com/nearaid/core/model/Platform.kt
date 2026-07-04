package com.nearaid.core.model

import platform.UIKit.UIDevice

actual fun platform(): String =
    UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
