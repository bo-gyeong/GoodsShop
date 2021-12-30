package com.example.goodsshop;

public class ReviewData {
    private String shopName;
    private String uid;
    private float rBarScore;
    private String rvContent;
    private String rvImgUrl;
    private String rvDate;

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public float getrBarScore() {
        return rBarScore;
    }

    public void setrBarScore(float rBarScore) {
        this.rBarScore = rBarScore;
    }

    public String getRvContent() {
        return rvContent;
    }

    public void setRvContent(String rvContent) {
        this.rvContent = rvContent;
    }

    public String getRvImgUrl() {
        return rvImgUrl;
    }

    public void setRvImgUrl(String rvImgUrl) {
        this.rvImgUrl = rvImgUrl;
    }

    public String getRvDate() {
        return rvDate;
    }

    public void setRvDate(String rvDate) {
        this.rvDate = rvDate;
    }
}
