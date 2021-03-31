package com.bfong.governmentbf;

import android.os.Parcelable;

import java.io.Serializable;

public class Official implements Serializable {

    String name;
    String office;
    String party;
    String location;
    String website = "";
    String email = "";
    String phone = "";
    String photo = "";
    String fb = "";
    String twitter = "";
    String youtube = "";
    String address = "";

    public Official(String name, String office, String party, String location) {
        this.name = name;
        this.office = office;
        this.party = party;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getOffice() {
        return office;
    }

    public String getParty() {
        return party;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setFb(String fb) {
        this.fb = fb;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLocation() {
        return location;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getPhoto() {
        return photo;
    }

    public String getFb() {
        return fb;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }

    public String getAddress() {
        return address;
    }
}
