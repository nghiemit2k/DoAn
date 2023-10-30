package com.devzen.chatbotai.chathistory;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    public static String SENT_BY_ME = "com";
    public static String SENT_BY_BOT="bot";

    String message;
    String sentBy;
    String time;
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
    String currentDateandTime = sdf.format(new Date());

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTime() {
        return currentDateandTime;
    }

    public void setTime(String time) {
        this.time = currentDateandTime;
    }

    public String getSentBy() {
        return sentBy;
    }

    public void setSentBy(String sentBy) {
        this.sentBy = sentBy;
    }

    public Message(String message, String sentBy) {
        this.message = message;
        this.sentBy = sentBy;
    }
}
