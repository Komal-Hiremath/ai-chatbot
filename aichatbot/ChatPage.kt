package com.example.aichatbot

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController


@Composable
fun ChatPage(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    viewModel: ChatViewModel,
    tts: TextToSpeech,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogoutClick: () -> Unit // Add this parameter
) {
    Column(modifier = modifier) {
        AppHeader(
            isDarkTheme = isDarkTheme,
            onThemeToggle = onThemeToggle,
            onLogoutClick = onLogoutClick // Use the passed function here
        )

        // Rest of your UI
        MessageList(
            modifier = Modifier.weight(1f),
            messageList = viewModel.messageList,
            tts = tts
        )

        MessageInput(
            onMessageSend = { message ->
                viewModel.sendMessage(message)
            }
        )
    }
}


@Composable
fun AppHeader(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.primary)
            .shadow(4.dp)
            .padding(vertical = 12.dp)
    ) {
        // Title in the center
        Text(
            text = "AI Chatbot",
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.align(Alignment.Center)
        )

        // Theme toggle button in the top-right corner
        IconButton(
            onClick = onThemeToggle,
            modifier = Modifier.align(Alignment.CenterEnd).padding(end = 16.dp)
        ) {
            Icon(
                imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }

        // Logout button in the top-left corner
        IconButton(
            onClick = onLogoutClick,
            modifier = Modifier.align(Alignment.CenterStart).padding(start = 16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Sign Out",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
fun MessageInput(onMessageSend: (String) -> Unit) {
    var message by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(
                MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Input Field
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            value = message,
            onValueChange = { message = it },
            placeholder = { Text("Type a message...") },
            maxLines = 5,  // Allow a maximum of 5 lines
            minLines = 1,  // Minimum 1 line
            singleLine = false,  // Allow multiline input
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )

        // Send Button
        IconButton(
            onClick = {
                if (message.isNotEmpty()) {
                    onMessageSend(message)
                    message = "" // Clear input after sending
                }
            },
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small
                )
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Send",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}


@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messageList: List<MessageModel>,
    tts: TextToSpeech // Accept tts here
) {
    if (messageList.isEmpty()) {
        // Empty State UI
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                modifier = Modifier.size(72.dp),
                painter = painterResource(id = R.drawable.baseline_chat_bubble_24),
                contentDescription = "Chat Icon",
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Ask anything!",
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    } else {
        // Message List UI
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            reverseLayout = true // Ensures the latest message is at the bottom
        ) {
            items(messageList.reversed()) { message ->
                MessageRow(messageModel = message, tts = tts) // Pass tts here
                Spacer(modifier = Modifier.height(8.dp)) // Space between messages
            }
        }
    }
}


@Composable
fun MessageRow(messageModel: MessageModel, tts: TextToSpeech) {
    val isModel = messageModel.role == "model" // Check if message is from the chatbot
    var isSpeaking by remember { mutableStateOf(false) } // Track if TTS is currently speaking
    val context = LocalContext.current // Access the context for clipboard and toast

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isModel) Arrangement.Start else Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = if (isModel) 8.dp else 70.dp,
                    end = if (isModel) 70.dp else 8.dp,
                    top = 8.dp,
                    bottom = 8.dp
                )
                .clip(RoundedCornerShape(16.dp))
                .background(if (isModel) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.primary)
                .padding(12.dp)
        ) {
            Column {
                RenderRichText(
                    text = messageModel.message,
                    color = if (isModel) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimary
                )

                // Add Read Aloud and Copy Buttons for chatbot's message
                if (isModel) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Read Aloud Button
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    tts.stop() // Stop TTS if speaking
                                } else {
                                    tts.speak(messageModel.message, TextToSpeech.QUEUE_FLUSH, null, null) // Speak message
                                }
                                isSpeaking = !isSpeaking // Toggle speaking state
                            }
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.Pause else Icons.AutoMirrored.Filled.VolumeUp, // Change icon based on speaking state
                                contentDescription = if (isSpeaking) "Stop Reading" else "Read Aloud",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        // Copy Button
                        IconButton(
                            onClick = {
                                val clipboardManager =
                                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("Copied Text", messageModel.message)
                                clipboardManager.setPrimaryClip(clip)
                                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RenderRichText(text: String, color: Color) {
    val annotatedString = remember(text) {
        parseRichText(text)
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyMedium.copy(color = color, lineHeight = 20.sp),
        modifier = Modifier.clickable {
            // Handle any interactions with the rendered text if needed
            // For now, we'll print the clicked offset, but you can add specific logic based on offset
            println("Clicked text")
        }
    )
}

fun parseRichText(input: String): AnnotatedString {
    return buildAnnotatedString {
        val lines = input.split("\n")
        lines.forEach { line ->
            when {
                // Bold text wrapped in ** (Gemini's possible format for bold text)
                line.startsWith("**") && line.endsWith("**") -> {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp // Larger font size for bold text
                        )
                    ) {
                        append(line.removeSurrounding("**")) // Remove ** for bold formatting
                    }
                }
                // Italic text wrapped in * (Gemini's possible format for italics)
                line.startsWith("*") && line.endsWith("*") -> {
                    withStyle(style = SpanStyle(fontStyle = FontStyle.Italic)) {
                        append(line.removeSurrounding("*")) // Remove * for italic formatting
                    }
                }
                // Bullet points starting with '-'
                line.startsWith("- ") -> {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append("• ") // Bullet point symbol
                    }
                    append(line.removePrefix("- ")) // Remove '- ' prefix for list items
                }
                // Normal text (no formatting)
                else -> {
                    append(line)
                }
            }
            append("\n") // Newline after each line of text
        }
    }
}
