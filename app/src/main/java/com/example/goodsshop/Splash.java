package com.example.goodsshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    TextView shopTextView;
    Animation anim;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        getSupportActionBar().hide();

        shopTextView = findViewById(R.id.shopTextView);
        anim = AnimationUtils.loadAnimation(this, R.anim.loading);
        shopTextView.setAnimation(anim);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                shopTextView.clearAnimation();
                finish();
            }
        },1500);
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }  //다른 액티비티가 활성화되면 finish
}
