package navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

@Composable
fun ObserveNavigationEvents(flow: Flow<NavigationAction>) {
    val navigator = LocalNavigator.currentOrThrow
    LaunchedEffect(Unit) {
        withContext(Dispatchers.Main.immediate) {
            flow.collect {
                when (it) {
                    is NavigationAction.Push -> navigator.push(it.screen)
                    NavigationAction.Pop -> navigator.pop()
                }
            }
        }
    }
}