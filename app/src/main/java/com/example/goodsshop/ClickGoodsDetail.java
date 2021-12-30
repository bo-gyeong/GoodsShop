package com.example.goodsshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

public class ClickGoodsDetail extends AppCompatActivity {

    ImageButton closeImgBtn;
    ViewPager2 goodsDetailViewPager2;
    LinearLayout indicatorLinearLayout;

    int picCount;
    ArrayList<String> images = new ArrayList<>();
    ArrayList<String> imgUrlArrList = new ArrayList<>();
    Intent intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.click_goods_detail);
        getSupportActionBar().hide();

        intent = getIntent();
        imgUrlArrList = intent.getStringArrayListExtra("imgUrlArrList");
        picCount = intent.getIntExtra("picCount", 1);

        closeImgBtn = findViewById(R.id.closeImgBtn);
        closeImgBtn.bringToFront();
        closeImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        goodsDetailViewPager2 = findViewById(R.id.goodsDetailViewPager2);
        indicatorLinearLayout = findViewById(R.id.indicatorLinearLayout);

        for (int i=0; i<picCount; i++){
            images.add(imgUrlArrList.get(i));
        }

        goodsDetailViewPager2.setOffscreenPageLimit(picCount);  //이미지 미리 로딩하는 개수
        goodsDetailViewPager2.setAdapter(new ImageSliderDetail(this, images));

        goodsDetailViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
            }
        });  //굿즈 이미지들 보여주는 뷰페이저

        setupIndicators(images.size());
    }

    private void setupIndicators(int count) {
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(16, 8, 16, 8);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(this);
            indicators[i].setImageDrawable(ContextCompat.getDrawable(this,
                    R.drawable.indicator_inactive));
            indicators[i].setLayoutParams(params);
            indicatorLinearLayout.addView(indicators[i]);
        }
        setCurrentIndicator(0);
    }  //사진 개수만큼 뷰페이저 밑에 ...생성

    private void setCurrentIndicator(int position) {
        int childCount = indicatorLinearLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) indicatorLinearLayout.getChildAt(i);
            if (i == position) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_active
                ));
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_inactive
                ));
            }
        }
    }  //굿즈 사진이 넘겨짐에 따라 뷰페이저 밑의 ... 색상 변환
}
