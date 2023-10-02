package com.example.food;

public class Restaurant_Profile_model {
    private String propic;
    private String full_Name;
    private String email;
    private String location;
    private String phone_Number;
    private String password;


    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    private int usertype;

    public Restaurant_Profile_model() {

    }

    public Restaurant_Profile_model(String propic, String full_Name, String email, String location, String phone_Number, String password, int usertype) {
        this.propic = propic;
        this.full_Name = full_Name;
        this.email = email;
        this.location = location;
        this.phone_Number = phone_Number;
        this.password = password;
        this.usertype = usertype;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getFull_Name() {
        return full_Name;
    }

    public void setFull_Name(String full_Name) {
        this.full_Name = full_Name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPhone_Number() {
        return phone_Number;
    }

    public void setPhone_Number(String phone_Number) {
        this.phone_Number = phone_Number;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}