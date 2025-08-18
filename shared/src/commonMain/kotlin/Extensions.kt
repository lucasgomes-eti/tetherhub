infix fun Long.numberOfPagesFor(pageSize: Int): Int {
    return if (this > 0) ((this + pageSize - 1) / pageSize).toInt() else 0
}