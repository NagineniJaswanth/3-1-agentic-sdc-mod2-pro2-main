// ApiClient.kt
package com.example.handson2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class ApiClient {
    // Access the key from BuildConfig (requires a successful Gradle Sync)
    private val apiKey = BuildConfig.GEMINI_API_KEY
    private val urlString = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

    suspend fun sendMessage(message: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val url = URL(urlString)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Correct JSON Structure for Gemini API
                val jsonBody = JSONObject().apply {
                    val contentsArray = JSONArray().apply {
                        val contentObject = JSONObject().apply {
                            val partsArray = JSONArray().apply {
                                put(JSONObject().put("text", message))
                            }
                            put("parts", partsArray)
                        }
                        put(contentObject)
                    }
                    put("contents", contentsArray)
                }

                connection.outputStream.write(jsonBody.toString().toByteArray())

                val responseCode = connection.responseCode
                if (responseCode == 200) {
                    val responseString = connection.inputStream.bufferedReader().use { it.readText() }
                    val jsonResponse = JSONObject(responseString)

                    // Extract the text response
                    jsonResponse.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text")
                } else {
                    val errorDetail = connection.errorStream?.bufferedReader()?.readText()
                    "Error $responseCode: $errorDetail"
                }
            } catch (e: Exception) {
                "Error: ${e.message}"
            }
        }
    }
}