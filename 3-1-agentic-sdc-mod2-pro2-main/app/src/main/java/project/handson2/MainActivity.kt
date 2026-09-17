package com.example.handson2

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: ChatbotViewModel
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var inputMessage: EditText
    private lateinit var sendButton: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize ViewModel
        viewModel = ViewModelProvider(this).get(ChatbotViewModel::class.java)

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewChat)
        inputMessage = findViewById(R.id.inputMessage)
        sendButton = findViewById(R.id.sendButton)
        progressBar = findViewById(R.id.progressBar)

        // Setup RecyclerView
        chatAdapter = ChatAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = chatAdapter

        // Observe chat messages
        viewModel.messages.observe(this) { messages ->
            chatAdapter.submitList(messages)
            recyclerView.scrollToPosition(messages.size - 1)
        }

        // Observe loading state
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            sendButton.isEnabled = !isLoading
        }

        // Send button click listener
        sendButton.setOnClickListener {
            sendMessage()
        }

        // Add welcome message if chat is empty
        if (viewModel.messages.value.isNullOrEmpty()) {
            viewModel.addWelcomeMessage()
        }
    }

    private fun sendMessage() {
        val message = inputMessage.text.toString().trim()
        if (message.isNotEmpty()) {
            inputMessage.text.clear()
            viewModel.sendMessage(message) { error ->
                Snackbar.make(recyclerView, error, Snackbar.LENGTH_SHORT).show()
            }
        }
    }
}