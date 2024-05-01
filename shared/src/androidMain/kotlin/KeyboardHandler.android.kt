class AndroidKeyboardHandler : KeyboardHandler, KeyboardEvents {

    override lateinit var keyboardEvents: KeyboardEvents

    override fun registerObserver() {
    }

    override fun removeObserver() {

    }

    override fun onShow(height: Int) {
    }

    override fun onHide() {

    }

}

actual fun getKeyboardHandler(): KeyboardHandler = AndroidKeyboardHandler()