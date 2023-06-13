package com.example.chatbot;

import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatbot.databinding.ActivityChatScreenBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatScreen extends AppCompatActivity {
    private ActivityChatScreenBinding binding;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private Interpreter tflite;
    private final int NUM_WORDS = 212;
    private final int NUM_CLASSES = 36;
    private List<String> words;
    private List<String> classes;
    private Questions intents;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        try {
            LoadFiles();

            ByteBuffer model = loadModelFile();
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(model, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        RecyclerView recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);
        recyclerView.setAdapter(chatAdapter);

        TextInputEditText editText = binding.editTextMessage;
        Button sendButton = binding.buttonSend;

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userInput = editText.getText().toString().trim();
                if (!userInput.isEmpty()) {
                    // Add user message to the list
                    ChatMessage userMessage = new ChatMessage(userInput, true);
                    messageList.add(userMessage);

                    // Clear the input field
                    editText.getText().clear();

                    // Generate bot response
                    String botResponse = generateBotResponse(userInput);
                    // Add bot response to the list
                    ChatMessage botMessage = new ChatMessage(botResponse, false);
                    messageList.add(botMessage);

                    // Notify the adapter about the updated message list
                    chatAdapter.notifyDataSetChanged();

                    // Scroll the RecyclerView to the bottom to show the latest message
                    recyclerView.scrollToPosition(messageList.size() - 1);
                }
            }
        });

        String initialBotMessage = "مرحباً ، ما هو السؤال الذي تريد الإجابة عليه.";
        ChatMessage initialBotChatMessage = new ChatMessage(initialBotMessage, false);
        messageList.add(initialBotChatMessage);
        chatAdapter.notifyDataSetChanged();
    }

    private String generateBotResponse(String userMessage) {
        return predictMessage(userMessage);
    }
    private void LoadFiles() {
        try {
            words = new ArrayList<String>();
            classes = new ArrayList<String>();

            InputStream wordInputStream = getAssets().open("words.txt");
            InputStreamReader wordInputStreamReader = new InputStreamReader(wordInputStream);
            BufferedReader wordsBr = new BufferedReader(wordInputStreamReader);
            String line;
            while ((line = wordsBr.readLine()) != null) {
                words.add(line);
            }
            wordsBr.close();

            InputStream classesInputStream = getAssets().open("classes.txt");
            InputStreamReader classesInputStreamReader = new InputStreamReader(classesInputStream);
            BufferedReader classesBr = new BufferedReader(classesInputStreamReader);
            while ((line = classesBr.readLine()) != null) {
                classes.add(line);
            }
            classesBr.close();

            InputStream intentsInputStream = getAssets().open("intents.json");
            int size = intentsInputStream.available();
            byte[] intentsBuffer = new byte[size];
            intentsInputStream.read(intentsBuffer);
            intentsInputStream.close();
            String intentsJsonString = new String(intentsBuffer, "UTF-8");
            intents = new Questions(intentsJsonString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String predictMessage(String message) {
        List<String> messageWords = Arrays.asList(message.split(" "));

        float[][] inputValues = new float[1][NUM_WORDS];
        for (String messageWord : messageWords) {
            int i = 0;
            for (String word : words) {
                if (messageWord.equals(word))
                    inputValues[0][i] = 1;
                i++;
            }
        }

        float[][] output = new float[1][NUM_CLASSES];
        tflite.run(inputValues, output);

        float max = output[0][0];
        int maxIndex = 0;
        for (int i = 1; i < NUM_CLASSES; i++) {
            if (max < output[0][i]) {
                max = output[0][i];
                maxIndex = i;
            }
        }

        if (max >= 0.80)
            return intents.GetResponse(classes.get(maxIndex));
        else
            return intents.GetResponse("Not Found");
    }

    private ByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = getAssets().openFd("chatbotmodel.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}
