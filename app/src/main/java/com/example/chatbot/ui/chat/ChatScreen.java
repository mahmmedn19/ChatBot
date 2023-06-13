package com.example.chatbot.ui.chat;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbot.data.ChatMessage;
import com.example.chatbot.databinding.ActivityChatScreenBinding;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChatScreen extends AppCompatActivity {
    private ActivityChatScreenBinding binding;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private ChatBotModel chatBotModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        chatBotModel = new ChatBotModel();

        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);

        TextInputEditText editText = binding.editTextMessage;
        Button sendButton = binding.buttonSend;

        sendButton.setOnClickListener(v -> {
            String userInput = Objects.requireNonNull(editText.getText()).toString().trim();
            if (!userInput.isEmpty()) {
                // Add user message to the list
                ChatMessage userMessage = new ChatMessage(userInput, true);
                messageList.add(userMessage);

                // Clear the input field
                editText.getText().clear();

                // Generate bot response
                String botResponse = chatBotModel.generateBotResponse(userInput);
                // Add bot response to the list
                ChatMessage botMessage = new ChatMessage(botResponse, false);
                messageList.add(botMessage);

                    // Notify the adapter about the updated message list
                    chatAdapter.notifyDataSetChanged();

                // Scroll the RecyclerView to the bottom to show the latest message
                recyclerView.scrollToPosition(messageList.size() - 1);
            }
        });

        String initialBotMessage = "مرحباً ، ما هو السؤال الذي تريد الإجابة عليه.";
        ChatMessage initialBotChatMessage = new ChatMessage(initialBotMessage, false);
        messageList.add(initialBotChatMessage);
        chatAdapter.notifyDataSetChanged();
    }

}
