package com.example.qurrah.Model;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicInteger;

public class Report implements Serializable {
    private String lostTitle;
    private String lostDescription;
    private String categoryOption;
    private String ReportTypeOption;
    private String ReportStatus;
    private String photo;
    private String location;
    private String date;//
    private String latitude;
    private String longitude;
    private String address;

    public Report() {
        this.ReportStatus = "نشط";
    }

    public Report(String lostTitle, String lostDescription, String categoryOption,String ReportTypeOption, String photo, String location, String date, String latitude, String longitude, String address) {
        this.lostTitle = lostTitle;
        this.lostDescription = lostDescription;
        this.categoryOption = categoryOption;
        this.ReportTypeOption=ReportTypeOption;
        this.photo = photo;
        this.location = location;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.ReportStatus = "نشط";

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
    public String getReportStatus() {
        return ReportStatus;
    }

    public void setReportStatus(String status) {
        this.ReportStatus = status;
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


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}

