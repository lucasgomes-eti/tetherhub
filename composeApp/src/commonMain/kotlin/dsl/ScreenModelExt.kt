package dsl

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.launch

inline fun ScreenModel.withScreenModelScope(crossinline block: suspend () -> Unit) {
    screenModelScope.launch { block() }
}