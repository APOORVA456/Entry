package com.example.entrymanagement;

public class Detail {
    String vname;
    String vphone;
    String vemail;
    String hname;
    String address;
    String hphone;
    String hemail;
    String checkInTime;
    String checkOutTime;


    public Detail(){ }

    public Detail(String vname, String vphone, String vemail,
                  String hname, String hphone, String hemail,
                  String checkInTime, String address,String checkOutTime) {
        this.vname = vname;
        this.vphone = vphone;
        this.vemail = vemail;
        this.hname = hname;
        this.hphone = hphone;
        this.hemail = hemail;
        this.checkInTime = checkInTime;
        this.address = address;
        this.checkOutTime = checkOutTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getVphone() {
        return vphone;
    }

    public void setVphone(String vphone) {
        this.vphone = vphone;
    }

    public String getVemail() {
        return vemail;
    }

    public void setVemail(String vemail) {
        this.vemail = vemail;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public String getHphone() {
        return hphone;
    }

    public void setHphone(String hphone) {
        this.hphone = hphone;
    }

    public String getHemail() {
        return hemail;
    }

    public void setHemail(String hemail) {
        this.hemail = hemail;
    }
}
