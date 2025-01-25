package com.example.aichatbot

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aichatbot.pages.LoginPage
import com.example.aichatbot.pages.SignupPage

@Composable
fun MyAppNavigation(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel,
    viewModel: ChatViewModel,
    tts: TextToSpeech,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.observeAsState(AuthState.UnAuthenticated)

    // Set the start destination based on authentication state
    val startDestination = if (authState is AuthState.Authenticated) "chat" else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        builder = {
            composable("login") {
                LoginPage(modifier, navController, authViewModel, isDarkTheme, onThemeToggle)
            }

            composable("signup") {
                SignupPage(modifier, navController, authViewModel, isDarkTheme, onThemeToggle)
            }

            composable("chat") {
                ChatPage(
                    modifier = modifier,
                    navController = navController,
                    authViewModel = authViewModel,
                    viewModel = viewModel(),
                    tts = tts,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    onLogoutClick = { // Logout function
                        authViewModel.signOut()
                        navController.navigate("login") {
                            popUpTo("chat") { inclusive = true }
                        }
                    }
                )
            }
        }
    )
}

