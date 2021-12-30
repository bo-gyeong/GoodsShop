package com.example.goodsshop;

import java.util.ArrayList;

public class RegiGoods {

    private String goodsName;
    private ArrayList<String> regiImageUrl;
    private int picCount;
    private String category;
    private int price;
    private boolean post, parcel, directDealing;
    private String condition;
    private String explain;
    private String regiDate;
    private int jjimCnt;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public ArrayList<String> getRegiImageUrl() {
        return regiImageUrl;
    }

    public void setRegiImageUrl(ArrayList<String> regiImageUrl) {
        this.regiImageUrl = regiImageUrl;
    }

    public int getPicCount() {
        return picCount;
    }

    public void setPicCount(int picCount) {
        this.picCount = picCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public boolean isPost() {
        return post;
    }

    public void setPost(boolean post) {
        this.post = post;
    }

    public boolean isParcel() {
        return parcel;
    }

    public void setParcel(boolean parcel) {
        this.parcel = parcel;
    }

    public boolean isDirectDealing() {
        return directDealing;
    }

    public void setDirectDealing(boolean directDealing) {
        this.directDealing = directDealing;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getRegiDate() {
        return regiDate;
    }

    public void setRegiDate(String regiDate) {
        this.regiDate = regiDate;
    }

    public int getJjimCnt() {
        return jjimCnt;
    }

    public void setJjimCnt(int jjimCnt) {
        this.jjimCnt = jjimCnt;
    }
}
