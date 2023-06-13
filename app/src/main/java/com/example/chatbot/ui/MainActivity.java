package com.example.chatbot.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.chatbot.databinding.ActivityMainBinding;
import com.example.chatbot.ui.chat.ChatScreen;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Launch SecondActivity
        binding.floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChatScreen.class);
            startActivity(intent);
        });

    }
}