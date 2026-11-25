package com.example.safeher.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.safeher.ui.screens.auth.LoginScreen
import com.example.safeher.ui.screens.auth.RegistrationScreen
import com.example.safeher.ui.screens.contacts.TrustedContactsScreen
import com.example.safeher.ui.screens.emergency.EmergencyScreen
import com.example.safeher.ui.screens.fakecall.FakeCallScreen
import com.example.safeher.ui.screens.home.HomeScreen
import com.example.safeher.ui.screens.journey.SafeJourneyScreen
import com.example.safeher.ui.screens.onboarding.OnboardingScreen
import com.example.safeher.ui.screens.profile.ProfileScreen
import com.example.safeher.ui.screens.settings.SettingsScreen
import com.example.safeher.ui.screens.splash.SplashScreen

@Composable
fun SafeHerNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToOnboarding = { navController.navigate(Screen.Onboarding.route) { popUpTo(Screen.Splash.route) { inclusive = true } } },
                onNavigateToHome = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Splash.route) { inclusive = true } } }
            )
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onGetStarted = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Onboarding.route) { inclusive = true } } }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Login.route) { inclusive = true } } },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) }
            )
        }
        composable(Screen.Register.route) {
            RegistrationScreen(
                onRegisterSuccess = { navController.navigate(Screen.Home.route) { popUpTo(Screen.Register.route) { inclusive = true } } },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSOS = { navController.navigate(Screen.Emergency.route) },
                onNavigateToJourney = { navController.navigate(Screen.SafeJourney.route) },
                onNavigateToContacts = { navController.navigate(Screen.Contacts.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToFakeCall = { navController.navigate(Screen.FakeCall.route) }
            )
        }
        composable(Screen.Contacts.route) {
            TrustedContactsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.SafeJourney.route) {
            SafeJourneyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Emergency.route) {
            EmergencyScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.Profile.route) {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = { navController.navigate(Screen.Login.route) { popUpTo(Screen.Home.route) { inclusive = true } } }
            )
        }
        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
        composable(Screen.FakeCall.route) {
            FakeCallScreen(onEndCall = { navController.popBackStack() })
        }
    }
}
