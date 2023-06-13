package com.example.chatbot.ui.chat;

import com.example.chatbot.data.Questions;

import org.tensorflow.lite.Interpreter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChatBotModel {
    private Interpreter tflite;
    private final int NUM_WORDS = 212;
    private final int NUM_CLASSES = 36;
    private List<String> words;
    private List<String> classes;
    private Questions intents;

    public ChatBotModel() {
        try {
            loadFiles();

            ByteBuffer model = loadModelFile();
            Interpreter.Options options = new Interpreter.Options();
            tflite = new Interpreter(model, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String generateBotResponse(String userMessage) {
        return predictMessage(userMessage);
    }

    private void loadFiles() throws IOException {
        words = new ArrayList<>();
        classes = new ArrayList<>();

        InputStream wordInputStream = getClass().getResourceAsStream("/assets/words.txt");
        InputStreamReader wordInputStreamReader = new InputStreamReader(wordInputStream);
        BufferedReader wordsBr = new BufferedReader(wordInputStreamReader);
        String line;
        while ((line = wordsBr.readLine()) != null) {
            words.add(line);
        }
        wordsBr.close();

        InputStream classesInputStream = getClass().getResourceAsStream("/assets/classes.txt");
        InputStreamReader classesInputStreamReader = new InputStreamReader(classesInputStream);
        BufferedReader classesBr = new BufferedReader(classesInputStreamReader);
        while ((line = classesBr.readLine()) != null) {
            classes.add(line);
        }
        classesBr.close();

        InputStream intentsInputStream = getClass().getResourceAsStream("/assets/intents.json");
        assert intentsInputStream != null;
        int size = intentsInputStream.available();
        byte[] intentsBuffer = new byte[size];
        intentsInputStream.read(intentsBuffer);
        intentsInputStream.close();
        String intentsJsonString = new String(intentsBuffer, StandardCharsets.UTF_8);
        intents = new Questions(intentsJsonString);
    }

    private String predictMessage(String message) {
        String[] messageWords = message.split(" ");

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
        InputStream inputStream = getClass().getResourceAsStream("/assets/chatbotmodel.tflite");
        byte[] modelBuffer = new byte[inputStream.available()];
        inputStream.read(modelBuffer);
        inputStream.close();

        ByteBuffer modelByteBuffer = ByteBuffer.allocateDirect(modelBuffer.length);
        modelByteBuffer.order(ByteOrder.nativeOrder());
        modelByteBuffer.put(modelBuffer);
        modelByteBuffer.position(0);

        return modelByteBuffer;
    }

}
