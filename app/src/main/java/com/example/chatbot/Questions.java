package com.example.chatbot;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class Questions {
    List<Question> all_questions;

    public Questions(String intentsJsonString){
        try {
            Gson gson = new Gson();
            Type listQuestionType = new TypeToken<List<Question>>() {
            }.getType();

            all_questions = gson.fromJson(intentsJsonString, listQuestionType);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String GetResponse(String tag)
    {
        for (Question Qs:all_questions) {
            if(Qs.getTag().equals(tag)) {
                return Qs.getResponses().get(0);
            }
        }
        return "عذراً، لا استطيع التعرف علي سؤالك ربما يكون السؤال غير مدرج؛ للتأكد ادخل السؤال بصيغه مختلفة.";
    }

}
