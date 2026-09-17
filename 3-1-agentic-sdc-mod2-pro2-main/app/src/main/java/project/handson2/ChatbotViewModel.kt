// ChatbotViewModel.kt (API version)
package com.example.handson2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChatbotViewModel : ViewModel() {

    private val apiClient = ApiClient()  // Use this instead of ChatbotEngine

    private val _messages = MutableLiveData<List<ChatMessage>>(emptyList())
    val messages: LiveData<List<ChatMessage>> = _messages

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    fun addWelcomeMessage() {
        val welcomeMessage = ChatMessage(
            id = System.currentTimeMillis(),
            text = "Hello! I'm your AI assistant. I can help you with various topics. " +
                    "Feel free to ask me anything or say 'help' for suggestions.",
            isUser = false,
            timestamp = System.currentTimeMillis()
        )
        _messages.value = _messages.value?.plus(welcomeMessage) ?: listOf(welcomeMessage)
    }

    fun sendMessage(message: String, onError: (String) -> Unit) {
        if (message.trim().isEmpty()) return

        // Add user message
        val userMessage = ChatMessage(
            id = System.currentTimeMillis(),
            text = message,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )
        _messages.value = _messages.value?.plus(userMessage) ?: listOf(userMessage)

        _isLoading.value = true

        viewModelScope.launch {
            try {
                // Call Python API
                val response = apiClient.sendMessage(message)

                val botMessage = ChatMessage(
                    id = System.currentTimeMillis() + 1,
                    text = response,
                    isUser = false,
                    timestamp = System.currentTimeMillis()
                )
                _messages.value = _messages.value?.plus(botMessage) ?: listOf(botMessage)

            } catch (e: Exception) {
                onError("Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}