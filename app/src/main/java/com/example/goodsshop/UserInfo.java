package com.example.goodsshop;

public class UserInfo {

    public String shopName; //샵 이름
    public String profileImageUrl; //프로필 사진
    public String uid; // 현재 사용자(로그인한)
    public float initialRBar;  //별점
    public String fcmToken;

    public String email;    //이메일

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getInitialRBar() {
        return initialRBar;
    }

    public void setInitialRBar(float initialRBar) {
        this.initialRBar = initialRBar;
    }

    public String getEmail() {
        return email;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}