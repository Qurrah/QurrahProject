package com.example.qurrah;

import java.util.concurrent.atomic.AtomicInteger;

public class Report {
    private String lostTitle;
    private String lostDescription;
    private String categoryOption;
    private String ReportTypeOption;
    private String photo;
    private String location;
    private String date;
    private static final AtomicInteger count = new AtomicInteger(0);

    public Report() {

    }

    public Report(String lostTitle, String lostDescription, String categoryOption,String ReportTypeOption, String photo, String location, String date) {
        this.lostTitle = lostTitle;
        this.lostDescription = lostDescription;
        this.categoryOption = categoryOption;
        this.ReportTypeOption=ReportTypeOption;
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

    public String getReportTypeOption() {
        return ReportTypeOption;
    }

    public void setReportTypeOption(String ReportTypeOption) {
        this.ReportTypeOption = ReportTypeOption;
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

