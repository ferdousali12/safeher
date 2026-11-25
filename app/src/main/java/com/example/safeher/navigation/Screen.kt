package com.example.safeher.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Onboarding : Screen("onboarding")
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Contacts : Screen("contacts")
    object SafeJourney : Screen("safe_journey")
    object Emergency : Screen("emergency")
    object Profile : Screen("profile")
    object Settings : Screen("settings")
    object FakeCall : Screen("fake_call")
}
