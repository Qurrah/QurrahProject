package com.example.qurrah.Model;

public class Chat {

    private String sender;
    private String receiver;
    private String message;
    private String messageType;
    private String date;
    private boolean isseen;
    private boolean senderDelete;
    private boolean receiverDelete;

    public Chat(String sender, String receiver, String messageType, String message, String date, boolean isseen) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.message = message;
        this.date = date;
        this.isseen = isseen;
        senderDelete=false;
        receiverDelete=false;
    }

    public Chat() {
    }

    public boolean isSenderDelete() {
        return senderDelete;
    }

    public boolean isReceiverDelete() {
        return receiverDelete;
    }

    public void setSenderDelete(boolean senderDelete) {
        this.senderDelete = senderDelete;
    }

    public void setReceiverDelete(boolean receiverDelete) {
        this.receiverDelete = receiverDelete;
    }

    public String getDate() {
        return date;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isIsseen() {
        return isseen;
    }

    public void setIsseen(boolean isseen) {
        this.isseen = isseen;
    }
}
