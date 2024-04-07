package home

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import feed.FeedTab

@Composable
fun Home() {
    TabNavigator(FeedTab) {
        Scaffold(bottomBar = {
            NavigationBar {
                TabItem(FeedTab)
                TabItem(MessagesTab)
                TabItem(ProfileTab)
            }
        }) { CurrentTab() }
    }
}

@Composable
private fun RowScope.TabItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    NavigationBarItem(selected = tabNavigator.current == tab, onClick = { tabNavigator.current = tab }, icon = {
        tab.options.icon?.let { Icon(it, contentDescription = tab.options.title) }
    })
}