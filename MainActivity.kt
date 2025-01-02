package com.example.aichatbot

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.aichatbot.ui.theme.AIChatbotTheme
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var tts: TextToSpeech

    // Initialize TextToSpeech
    private fun initializeTextToSpeech() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val langResult = tts.setLanguage(Locale.US)
                if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported or missing data.")
                } else {
                    Log.d("TTS", "TextToSpeech initialized successfully.")
                }
            } else {
                Log.e("TTS", "Initialization failed!")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeTextToSpeech()

        // Initialize AuthViewModel for authentication
        val authViewModel: AuthViewModel by viewModels()

        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }

            AIChatbotTheme(darkTheme = isDarkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(
                        modifier = Modifier.padding(innerPadding),
                        authViewModel = authViewModel,
                        viewModel = ChatViewModel(),
                        tts = tts,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { isDarkTheme = !isDarkTheme }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }
}
