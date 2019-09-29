package com.example.qurrahtest;



public class UserProfile {
    public String userEmail;
    public String userName;
    public String phone;

    public UserProfile(){
    }



    public UserProfile(String userEmail, String userName, String phone) {
//        this.userAge = userAge;
        this.userEmail = userEmail;
        this.userName = userName;
        this.phone = phone;
    }
    public UserProfile(String userEmail, String userName){
        this.userEmail = userEmail;
        this.userName = userName;
    }



    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
