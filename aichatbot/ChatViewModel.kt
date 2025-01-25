package com.example.aichatbot

import com.example.aichatbot.apikey.Constants
import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.launch


class ChatViewModel : ViewModel() {

    val messageList = mutableStateListOf<MessageModel>()

    private val generativeModel: GenerativeModel = GenerativeModel(
        modelName = "gemini-pro",
        apiKey = Constants.apiKey
    )

    fun sendMessage(question: String) {
        viewModelScope.launch {
            try {
                val chat = generativeModel.startChat(
                    history = messageList.map {
                        content(it.role) {
                            text(it.message)
                        }
                    }.toList()
                )
                messageList.add(MessageModel(question, role = "user"))
                messageList.add(MessageModel("Typing...", role = "model"))

                val response = chat.sendMessage(question)

                messageList.removeAt(messageList.lastIndex) // Remove "Typing..." message
                messageList.add(MessageModel(response.text.toString(), role = "model"))
            } catch (e: Exception) {
                messageList.removeAt(messageList.lastIndex) // Remove "Typing..." message
                messageList.add(MessageModel("Error: Could not process your request.", role = "model"))
            }
        }
    }
}
