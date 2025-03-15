package dsl.eventbus

import androidx.compose.runtime.compositionLocalOf

internal val LocalEventBus =
    compositionLocalOf<EventBus> { error("No dsl.eventbus.EventBus provided") }