package dsl.eventbus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance

class EventBus {
    val events = MutableSharedFlow<Event>()

    suspend fun publish(event: Event) {
        events.emit(event)
    }

    suspend inline fun <reified T : Event> subscribe(crossinline block: (T) -> Unit) {
        events.filterIsInstance<T>().collect { block(it) }
    }
}