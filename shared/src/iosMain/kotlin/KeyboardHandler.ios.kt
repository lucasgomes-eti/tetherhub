import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCAction
import platform.CoreGraphics.CGRect
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSSelectorFromString
import platform.darwin.NSObject

class KeyboardObserver : NSObject() {

    lateinit var listener: KeyboardEvents

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun keyboardWillShow(notification: NSNotification) {
        val height = (notification.userInfo?.get(platform.UIKit.UIKeyboardFrameEndUserInfoKey) as? CGRect)?.size?.height
        listener.onShow(height?.toInt() ?: 0)
    }

    @OptIn(BetaInteropApi::class)
    @ObjCAction
    fun keyboardWillHide(notification: NSNotification) {
        listener.onHide()
    }
}

class IOSKeyboardHandler : KeyboardHandler, KeyboardEvents {

    override lateinit var keyboardEvents: KeyboardEvents

    private val keyboardObserver = KeyboardObserver()

    @OptIn(ExperimentalForeignApi::class)
    override fun registerObserver() {
        keyboardObserver.listener = this
        NSNotificationCenter.defaultCenter.addObserver(
            observer = keyboardObserver,
            selector = NSSelectorFromString(KeyboardObserver::keyboardWillShow.name + ":"),
            name = platform.UIKit.UIKeyboardWillShowNotification,
            `object` = null
        )
        NSNotificationCenter.defaultCenter.addObserver(
            observer = keyboardObserver,
            selector = NSSelectorFromString(KeyboardObserver::keyboardWillHide.name + ":"),
            name = platform.UIKit.UIKeyboardWillHideNotification,
            `object` = null
        )
    }

    override fun removeObserver() {
        NSNotificationCenter.defaultCenter.removeObserver(keyboardObserver)
    }

    override fun onShow(height: Int) {
        keyboardEvents.onShow(height)
    }

    override fun onHide() {
        keyboardEvents.onHide()
    }

}

actual fun getKeyboardHandler(): KeyboardHandler = IOSKeyboardHandler()