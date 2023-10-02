package com.example.food;

public class customer_profile_model {
    private String propic;
    private String Fname;
    private String Email;
    private String Phone;
    private String Pass;


    public int getUsertype() {
        return usertype;
    }

    public void setUsertype(int usertype) {
        this.usertype = usertype;
    }

    private  int usertype;


    public customer_profile_model(){}

    public customer_profile_model(String propic, String fname, String email, String phone, String pass,int usertype) {
        this.propic = propic;
        this.Fname = fname;
        this.Email = email;
        this.Phone = phone;
        this.Pass = pass;
        this.usertype =usertype;
    }

    public String getPropic() {
        return propic;
    }

    public void setPropic(String propic) {
        this.propic = propic;
    }

    public String getFname() {
        return Fname;
    }

    public void setFname(String fname) {
        this.Fname = fname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        this.Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        this.Phone = phone;
    }

    public String getPass() {
        return Pass;
    }

    public void setPass(String pass) {
        this.Pass = pass;
    }
}