package com.example.food;

public class foodmodel
{
    private String full_name;
    private String email;
    private String food_name;
    private String desc;
    private String price;
    private String phone;
    private String pic;
    private String ownerid;

    public foodmodel(String toString, String toString1, String toString2, String toString3
            , String toString4, String download){}


            public foodmodel(){}

    public foodmodel(String full_name, String email, String food_name, String desc, String price, String phone, String pic, String ownerid) {
        this.full_name = full_name;
        this.email = email;
        this.food_name = food_name;
        this.desc = desc;
        this.price = price;
        this.phone = phone;
        this.pic = pic;
        this.ownerid = ownerid;
    }

    public String getfull_name() {
        return full_name;
    }

    public void setfull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFood_name() {
        return food_name;
    }

    public void setFood_name(String food_name) {
        this.food_name = food_name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getOwnerid() {
        return ownerid;
    }

    public void setOwnerid(String ownerid) {
        this.ownerid = ownerid;
    }


}
