package dsl.navigation

import cafe.adriel.voyager.core.screen.Screen

sealed interface NavigationAction {

    data class Push(val screen: Screen) : NavigationAction

    data object Pop : NavigationAction

    data class Replace(val screen: Screen) : NavigationAction
}