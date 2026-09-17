package com.example.handson2

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter : ListAdapter<ChatMessage, ChatAdapter.ChatViewHolder>(ChatDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chat_message, parent, false)
        return ChatViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.messageText)
        private val timestampText: TextView = itemView.findViewById(R.id.timestampText)
        private val userMessageContainer: View = itemView.findViewById(R.id.userMessageContainer)
        private val botMessageContainer: View = itemView.findViewById(R.id.botMessageContainer)

        fun bind(message: ChatMessage) {
            messageText.text = message.text

            // Format timestamp
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            timestampText.text = dateFormat.format(Date(message.timestamp))

            // Show appropriate container
            if (message.isUser) {
                userMessageContainer.visibility = View.VISIBLE
                botMessageContainer.visibility = View.GONE
            } else {
                userMessageContainer.visibility = View.GONE
                botMessageContainer.visibility = View.VISIBLE
            }
        }
    }

    class ChatDiffCallback : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem == newItem
        }
    }
}