package com.example.goodsshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

//ClickGoods의 이미지 슬라이드 구현
public class ImageSlider extends RecyclerView.Adapter<ImageSlider.GoodsViewHolder> {
    Context context;
    ArrayList<String> sliderImage;

    public ImageSlider(Context context, ArrayList<String> sliderImage) {
        this.context = context;
        this.sliderImage = sliderImage;
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.click_goods_img_slider, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        holder.bindSliderImage(sliderImage.get(position));
    }

    @Override
    public int getItemCount() {
        return sliderImage.size();
    }

    public class GoodsViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSliderImgView;

        public GoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            imgSliderImgView = itemView.findViewById(R.id.imgSliderImgView);
        }

        public void bindSliderImage(String imageURL) {
            Glide.with(context).load(imageURL).into(imgSliderImgView);
        }
    }
}
