package com.example.handson2

import java.util.*

class ChatbotEngine {

    private val contextMemory = mutableMapOf<String, Any>()
    private var lastTopic: String? = null

    fun getResponse(userMessage: String, conversationHistory: List<ChatMessage>): String {
        val message = userMessage.lowercase().trim()

        // Update context with current message
        updateContext(userMessage)

        // Handle follow-up questions based on context
        if (contextMemory.containsKey("lastTopic") && isFollowUpQuestion(message)) {
            return handleFollowUp(message)
        }

        // Main intent detection with context
        return when {
            message.contains("help") -> getHelpResponse()
            message.contains("hello") || message.contains("hi") -> getGreetingResponse()
            message.contains("how are you") -> getStatusResponse()
            message.contains("weather") -> getWeatherResponse()
            message.contains("name") -> getNameResponse()
            message.contains("time") -> getTimeResponse()
            message.contains("bye") || message.contains("goodbye") -> getGoodbyeResponse()
            message.contains("clear") || message.contains("reset") -> clearContextResponse()
            message.contains("recommend") || message.contains("suggest") -> getRecommendationResponse()
            else -> getGeneralResponse(userMessage)
        }
    }

    private fun updateContext(message: String) {
        // Extract potential topics from the message
        val topics = listOf("weather", "time", "name", "recommendation", "help", "greeting")
        for (topic in topics) {
            if (message.contains(topic)) {
                contextMemory["lastTopic"] = topic
                contextMemory["lastQuery"] = message
                lastTopic = topic
                break
            }
        }

        // Store conversation context
        contextMemory["lastMessage"] = message
        contextMemory["timestamp"] = System.currentTimeMillis()

        // Keep context for follow-up questions
        if (message.startsWith("and") || message.startsWith("also") ||
            message.startsWith("then") || message.contains("about")) {
            // Keep existing context
        }
    }

    private fun isFollowUpQuestion(message: String): Boolean {
        val followUpIndicators = listOf(
            "what about", "how about", "and", "also", "then",
            "so", "like", "such as", "for example", "more"
        )
        return followUpIndicators.any { message.contains(it) } || message.length < 10
    }

    private fun handleFollowUp(message: String): String {
        val lastTopic = contextMemory["lastTopic"] as? String ?: return getGeneralResponse(message)

        return when (lastTopic) {
            "weather" -> getWeatherFollowUp(message)
            "time" -> getTimeFollowUp(message)
            "name" -> getNameFollowUp(message)
            "recommendation" -> getRecommendationFollowUp(message)
            else -> getGeneralResponse(message)
        }
    }

    private fun getGreetingResponse(): String {
        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return when (hour) {
            in 0..11 -> "Good morning! How can I help you today?"
            in 12..16 -> "Good afternoon! What can I do for you?"
            else -> "Good evening! Nice to chat with you."
        } + " I remember we're having a conversation. What would you like to know?"
    }

    private fun getStatusResponse(): String {
        return "I'm functioning perfectly! Thanks for asking. " +
                "I've been processing your messages and maintaining context. " +
                "How can I assist you further?"
    }

    private fun getWeatherResponse(): String {
        contextMemory["lastTopic"] = "weather"
        return "I don't have real-time weather data, but I can tell you that " +
                "it's important to check local weather forecasts. " +
                "Would you like to know about the weather in a specific city?"
    }

    private fun getWeatherFollowUp(message: String): String {
        return if (message.contains("city") || message.contains("location")) {
            "To get weather information for any city, you can use weather APIs " +
                    "or check weather websites. Is there a specific city you're interested in?"
        } else {
            "For weather updates, I recommend using weather apps or checking " +
                    "your local weather forecast. Can I help you with anything else about weather?"
        }
    }

    private fun getTimeResponse(): String {
        val now = Calendar.getInstance()
        val time = String.format("%02d:%02d", now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE))
        contextMemory["lastTopic"] = "time"
        return "The current time is $time. Is there anything else about time you'd like to know?"
    }

    private fun getTimeFollowUp(message: String): String {
        return if (message.contains("zone") || message.contains("timezone")) {
            "Time zones vary across the world. Are you interested in a specific time zone?"
        } else {
            "You can check time information for different locations. " +
                    "Would you like to know the time in a specific country?"
        }
    }

    private fun getNameResponse(): String {
        contextMemory["lastTopic"] = "name"
        return "I'm your AI assistant! You can call me Assistant. " +
                "What's your name? I'd love to know who I'm talking to."
    }

    private fun getNameFollowUp(message: String): String {
        return if (message.contains("my name") || message.contains("call me")) {
            "That's a nice name! I'll remember that for our conversation. " +
                    "How else can I help you today?"
        } else {
            "I'm here to assist you with various topics. " +
                    "Feel free to ask me anything else!"
        }
    }

    private fun getHelpResponse(): String {
        return """I can help you with various topics:
                1. Ask me about weather
                2. Inquire about time
                3. Ask my name
                4. Get recommendations
                5. General questions
                6. Follow-up questions (I'll remember context!)
                
                What would you like to know about?""".trimIndent()
    }

    private fun getGoodbyeResponse(): String {
        return "Goodbye! It was nice talking to you. " +
                "Feel free to come back if you have more questions!"
    }

    private fun getRecommendationResponse(): String {
        contextMemory["lastTopic"] = "recommendation"
        return "Based on our conversation, I'd recommend exploring topics you're " +
                "interested in. Would you like recommendations about books, movies, " +
                "or something specific?"
    }

    private fun getRecommendationFollowUp(message: String): String {
        return when {
            message.contains("book") -> "For book recommendations, you might enjoy " +
                    "different genres. Do you prefer fiction or non-fiction?"
            message.contains("movie") -> "For movie recommendations, it depends on your " +
                    "preferences. Do you like action, comedy, or drama?"
            else -> "I can give you recommendations on various topics. " +
                    "What specific category are you interested in?"
        }
    }

    private fun clearContextResponse(): String {
        contextMemory.clear()
        lastTopic = null
        return "Context has been cleared. I've forgotten our previous conversation. " +
                "How would you like to start fresh?"
    }

    private fun getGeneralResponse(message: String): String {
        return "Interesting topic! I don't have specific information about '$message', " +
                "but I'm here to help. Could you provide more details or ask another question? " +
                "I remember our previous conversation and can connect topics if needed."
    }
}