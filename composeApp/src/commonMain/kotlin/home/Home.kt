package home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideIn
import androidx.compose.animation.slideOut
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.IntOffset
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import feed.FeedTab
import messages.MessagesTab
import profile.ProfileTab

@Composable
fun Home() {
    TabNavigator(ProfileTab) {

        val navigationAppBar = remember { NavigationAppBar(mutableStateOf(true)) }

        Scaffold(bottomBar = {
            AnimatedVisibility(
                visible = navigationAppBar.isNavigationBarVisible.value,
                enter = fadeIn() + slideIn { IntOffset(0, it.height) },
                exit = slideOut { IntOffset(0, it.height) } + fadeOut()
            ) {
                NavigationBar {
                    TabItem(FeedTab)
                    TabItem(MessagesTab)
                    TabItem(ProfileTab)
                }
            }
        }) {
            CompositionLocalProvider(LocalNavigationAppBar provides navigationAppBar) {
                CurrentTab()
            }
        }
    }
}

internal val LocalNavigationAppBar = compositionLocalOf<NavigationAppBar> { error("No NavigationBar provided") }

class NavigationAppBar(val isNavigationBarVisible: MutableState<Boolean>) {
    fun show() {
        isNavigationBarVisible.value = true
    }

    fun hide() {
        isNavigationBarVisible.value = false
    }
}

@Composable
private fun RowScope.TabItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(selected = tabNavigator.current == tab, onClick = { tabNavigator.current = tab }, icon = {
        tab.options.icon?.let { Icon(it, contentDescription = tab.options.title) }
    }, label = { Text(tab.options.title, style = typography.bodyMedium) })
}