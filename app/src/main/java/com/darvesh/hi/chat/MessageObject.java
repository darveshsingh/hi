package com.darvesh.hi.chat;

import java.util.ArrayList;

public class MessageObject {
    String messageId,
            message,
            senderId;

    ArrayList<String> mediaUrlList;

    public MessageObject(String messageId, String senderId, String message, ArrayList<String> mediaUrlList){
        this.messageId = messageId;
        this.senderId = senderId;
        this.message = message;
        this.mediaUrlList = mediaUrlList;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderId() {
        return senderId;
    }

    public ArrayList<String> getMediaUrlList() {
        return mediaUrlList;
    }
}
