package com.example.swipper.chat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.swipper.R
import com.example.swipper.chat.models.Message

class MessagesAdapter(private var messages: ArrayList<Message>, val userId: String) :
    RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>() {

    companion object {
        val MESSAGE_CURRENT_USER = 1
        val MESSAGE_OTHER_USER = 2
    }

    fun addMessage(message: Message){
        messages.add(message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        if (viewType == MESSAGE_CURRENT_USER) {
            return MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_current_user_message,
                    parent,
                    false
                )
            )
        } else
            return MessageViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_other_user_message,
                    parent,
                    false
                )
            )

    }

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        holder.bind(messages[position])
    }


    override fun getItemViewType(position: Int): Int {
        if (messages[position].sentBy.equals(userId)) {
            return MESSAGE_CURRENT_USER
        }
        return MESSAGE_OTHER_USER
    }

    class MessageViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(message: Message) {
            view.findViewById<TextView>(R.id.messageTV).text = message.message
        }
    }
}