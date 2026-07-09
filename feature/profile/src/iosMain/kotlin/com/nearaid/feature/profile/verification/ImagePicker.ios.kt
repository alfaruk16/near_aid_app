package com.nearaid.feature.profile.verification

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.writeToFile
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerFilter
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue

private const val IMAGE_UTI = "public.image"

/**
 * iOS image picking (Phase 5 platform edge). Presents a `PHPickerViewController` limited to a single
 * image; on selection it loads the picked image's bytes, writes them to a temp file under
 * `NSTemporaryDirectory` keyed by [fileName], and hands the resulting path back to [onPicked] on the
 * main queue — mirroring the Android `GetContent` + cache-copy actual so the shared verification
 * screen behaves the same on both platforms.
 */
@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberImagePicker(fileName: String, onPicked: (String) -> Unit): () -> Unit {
    // Keep the delegate alive: PHPickerViewController holds it weakly, so without a strong reference
    // it would be deallocated before the picker calls back.
    val delegateHolder = remember { arrayOfNulls<PickerDelegate>(1) }
    return {
        val delegate = PickerDelegate(fileName, onPicked) { delegateHolder[0] = null }
        delegateHolder[0] = delegate

        val config = PHPickerConfiguration().apply {
            filter = PHPickerFilter.imagesFilter()
            selectionLimit = 1
        }
        val picker = PHPickerViewController(configuration = config)
        picker.delegate = delegate
        topViewController()?.presentViewController(picker, animated = true, completion = null)
    }
}

@OptIn(ExperimentalForeignApi::class)
private class PickerDelegate(
    private val fileName: String,
    private val onPicked: (String) -> Unit,
    private val onFinished: () -> Unit,
) : NSObject(), PHPickerViewControllerDelegateProtocol {

    override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
        picker.dismissViewControllerAnimated(true, completion = null)

        val provider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider
        if (provider == null || !provider.hasItemConformingToTypeIdentifier(IMAGE_UTI)) {
            onFinished()
            return
        }

        provider.loadDataRepresentationForTypeIdentifier(IMAGE_UTI) { data: NSData?, _ ->
            if (data != null) {
                val path = NSTemporaryDirectory() + fileName
                if (data.writeToFile(path, atomically = true)) {
                    dispatch_async(dispatch_get_main_queue()) { onPicked(path) }
                }
            }
            onFinished()
        }
    }
}

/** The front-most presented controller, so the picker shows above the Compose UIViewController. */
private fun topViewController(): UIViewController? {
    var top = UIApplication.sharedApplication.keyWindow?.rootViewController
    while (top?.presentedViewController != null) {
        top = top.presentedViewController
    }
    return top
}
