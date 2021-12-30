package com.example.goodsshop;

import android.net.Uri;

public class SearchGoodsData {
    private Uri goodsImgView;
    private String goodsTitleTextView = "";
    private String priceTextView = "";

    public Uri getGoodsImgView() {
        return goodsImgView;
    }

    public void setGoodsImgView(Uri goodsImgView) {
        this.goodsImgView = goodsImgView;
    }

    public String getGoodsTitleTextView() {
        return goodsTitleTextView;
    }

    public void setGoodsTitleTextView(String goodsTitleTextView) {
        this.goodsTitleTextView = goodsTitleTextView;
    }

    public String getPriceTextView() {
        return priceTextView;
    }

    public void setPriceTextView(String priceTextView) {
        this.priceTextView = priceTextView;
    }
}
