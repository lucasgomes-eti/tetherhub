class JVMKeyboardHandler : KeyboardHandler, KeyboardEvents {
    override fun onShow(height: Int) {
    }

    override fun onHide() {
    }

    override lateinit var keyboardEvents: KeyboardEvents

    override fun registerObserver() {
    }

    override fun removeObserver() {
    }

}

actual fun getKeyboardHandler(): KeyboardHandler = JVMKeyboardHandler()