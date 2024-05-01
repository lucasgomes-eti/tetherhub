interface KeyboardEvents {
    fun onShow(height: Int)
    fun onHide()
}

interface KeyboardHandler {
    var keyboardEvents: KeyboardEvents

    fun registerObserver()

    fun removeObserver()
}

expect fun getKeyboardHandler(): KeyboardHandler