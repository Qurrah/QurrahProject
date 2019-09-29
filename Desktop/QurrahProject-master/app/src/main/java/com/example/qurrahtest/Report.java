package com.example.qurrahtest;

public class Report {
    private String lostTitle;
    private String lostDescription;
    private String categoryOption;
    private String senderName;
    private String phoneNumber;
    private String photo;
    private String location;
    private String date;

    public Report() {

    }

    public Report(String lostTitle, String lostDescription, String categoryOption, String senderName, String phoneNumber, String photo, String location, String date) {
        this.lostTitle = lostTitle;
        this.lostDescription = lostDescription;
        this.categoryOption = categoryOption;
        this.senderName = senderName;
        this.phoneNumber = phoneNumber;
        this.photo = photo;
        this.location = location;
        this.date = date;
    }

    public String getLostTitle() {
        return lostTitle;
    }

    public void setLostTitle(String lostTitle) {
        this.lostTitle = lostTitle;
    }

    public String getLostDescription() {
        return lostDescription;
    }

    public void setLostDescription(String lostDescription) {
        this.lostDescription = lostDescription;
    }

    public String getCategoryOption() {
        return categoryOption;
    }

    public void setCategoryOption(String categoryOption) {
        this.categoryOption = categoryOption;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }





}

