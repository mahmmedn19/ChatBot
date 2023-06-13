package com.example.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> messageList;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    public void setMessageList(List<ChatMessage> messageList) {
        ChatMessageDiffUtilCallback diffUtilCallback = new ChatMessageDiffUtilCallback(this.messageList, messageList);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffUtilCallback);
        this.messageList.clear();
        this.messageList.addAll(messageList);
        diffResult.dispatchUpdatesTo(this);
    }

    public void addMessage(ChatMessage message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage message = messageList.get(position);
        return message.isUserMessage() ? R.layout.item_message_user : R.layout.item_message_bot;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false);
        if (viewType == R.layout.item_message_user) {
            return new UserMessageViewHolder(view);
        } else {
            return new BotMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof UserMessageViewHolder) {
            ((UserMessageViewHolder) holder).bind(message);
        } else if (holder instanceof BotMessageViewHolder) {
            ((BotMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    private static class UserMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView userMessageTextView;

        public UserMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            userMessageTextView = itemView.findViewById(R.id.textViewUserMessage);
        }

        public void bind(ChatMessage message) {
            userMessageTextView.setText(message.getMessageText());
        }
    }

    private static class BotMessageViewHolder extends RecyclerView.ViewHolder {
        private TextView botMessageTextView;

        public BotMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            botMessageTextView = itemView.findViewById(R.id.textViewBotMessage);
        }

        public void bind(ChatMessage message) {
            botMessageTextView.setText(message.getMessageText());
        }
    }
}
