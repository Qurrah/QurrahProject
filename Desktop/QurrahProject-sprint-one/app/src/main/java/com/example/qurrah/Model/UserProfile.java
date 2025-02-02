package com.example.qurrah.Model;


public class UserProfile {
    public String id;
    public String userEmail;
    public String userName;
    public String phone;
    public String imageURL;
    public String status;
    public String search;
    public String allowPhone;


    //constructors

    public UserProfile() {
    }

    public UserProfile(String id, String userEmail, String userName, String phone , String imageURL) {
        this.id = id;
        this.userEmail = userEmail;
        this.userName = userName;
        this.phone = phone;
        this.imageURL = imageURL;
        this.status = "offline";
        this.search = userName.toLowerCase();
        this.allowPhone="true";
    }

    public UserProfile(String userEmail, String userName){
        this.userEmail = userEmail;
        this.userName = userName;
    }

    //getters

    public String getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public String getPhone() {
        return phone;
    }

    public String getImageURL() {
        return imageURL;
    }

    public String getStatus() {
        return status;
    }

    public String getSearch() {
        return search;
    }

    public String getAllowPhone() { return allowPhone;}

    //setters

    public void setId(String id) {
        this.id = id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void setAllowPhone(String allowPhone) {
        this.allowPhone = allowPhone;
    }

}
