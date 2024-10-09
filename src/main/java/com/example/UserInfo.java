package com.example;
public class UserInfo {
    private String name;
    private String gender;
    private String emailAddress;
    private String phoneNumber;
    public UserInfo(String name, String gender, String emailAddress, String phoneNumber) {
        this.name = name;
        this.gender = gender;
        this.emailAddress = emailAddress;
        this.phoneNumber = phoneNumber;
    }
    // Getters
    public String getName() { return name; }
    public String getGender() { return gender; }
    public String getEmailAddress() { return emailAddress; }
    public String getPhoneNumber() { return phoneNumber; }
    // Setters
    public void setName(String name) { this.name = name; }
    public void setGender(String gender) { this.gender = gender; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}