package com.example.chatbot;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class ChatMessageDiffUtilCallback extends DiffUtil.Callback {
    private List<ChatMessage> oldList;
    private List<ChatMessage> newList;

    public ChatMessageDiffUtilCallback(List<ChatMessage> oldList, List<ChatMessage> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        ChatMessage oldMessage = oldList.get(oldItemPosition);
        ChatMessage newMessage = newList.get(newItemPosition);
        return oldMessage == newMessage; // Compare references of the ChatMessage objects
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        ChatMessage oldMessage = oldList.get(oldItemPosition);
        ChatMessage newMessage = newList.get(newItemPosition);
        return oldMessage.getMessageText().equals(newMessage.getMessageText())
                && oldMessage.isUserMessage() == newMessage.isUserMessage();
    }
}

