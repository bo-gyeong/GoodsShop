package com.example.goodsshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//ClickGoodsDetail의 이미지 슬라이드 구현
public class ImageSliderDetail extends RecyclerView.Adapter<ImageSliderDetail.GoodsDetailViewHolder> {
    Context context;
    ArrayList<String> sliderImage;

    public ImageSliderDetail(Context context, ArrayList<String> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public GoodsDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.click_goods_detail_img_slider, parent, false);
        return new GoodsDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsDetailViewHolder holder, int position) {
        holder.bindSliderImage(sliderImage.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }

    public class GoodsDetailViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSliderImgView;

        public GoodsDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSliderImgView = itemView.findViewById(R.id.imgSliderImgView);
        }

        public void bindSliderImage(String imageURL) {
            Glide.with(context).load(imageURL).into(imgSliderImgView);
        }
    }
}
