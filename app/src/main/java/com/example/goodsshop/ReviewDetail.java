package com.example.goodsshop;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ReviewDetail extends AppCompatActivity {

    ConstraintLayout reviewShopCLayout, rvImgCLayout;
    ImageView profileImgView, goodsImgView;
    TextView shopNameTextView, dateTextView, rBarNameTextView, goodsContentTextView;
    RatingBar evaluateRBar;
    View brView2;

    Intent getRvInfoIntent;
    String profileImageUrl, rvContent, rvDate, rvImgUrl, shopName, uid;
    float rBarScore, otherRBarScore;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.review_detail);

        getSupportActionBar().setTitle("상세 후기");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        getRvInfoIntent = getIntent();
        rBarScore = Float.parseFloat(getRvInfoIntent.getStringExtra("getRBarScore"));
        rvContent = getRvInfoIntent.getStringExtra("getRvContent");
        rvDate = getRvInfoIntent.getStringExtra("getRvDate");
        rvImgUrl= getRvInfoIntent.getStringExtra("getRvImgUrl");
        uid = getRvInfoIntent.getStringExtra("getUid");

        reviewShopCLayout = findViewById(R.id.reviewShopConstraintLayout);
        reviewShopCLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SeeOtherShop.class);
                intent.putExtra("getUid", uid);
                intent.putExtra("otherShopName", shopName);
                intent.putExtra("profileImgUrl", profileImageUrl);
                intent.putExtra("otherRBar", otherRBarScore);

                startActivity(intent);
            }
        }); //프로필 눌렀을 경우 화면 전환

        profileImgView = findViewById(R.id.profileImgView);
        shopNameTextView = findViewById(R.id.shopNameTextView);
        rvImgCLayout = findViewById(R.id.rvImgCLayout);
        goodsImgView = findViewById(R.id.goodsImgView);
        brView2 = findViewById(R.id.brView2);
        rBarNameTextView = findViewById(R.id.rBarNameTextView);

        dateTextView = findViewById(R.id.dateTextView);
        dateTextView.setText(rvDate);

        evaluateRBar = findViewById(R.id.evaluateRBar);
        evaluateRBar.setRating(rBarScore);

        getSellerInfo(uid);

        if (!rvImgUrl.equals("")){ // 리뷰 사진을 등록했다면 보여주기
            Glide.with(getApplicationContext()).load(rvImgUrl).into(goodsImgView);
        } else{
            //rvImgCLayout.setVisibility(View.GONE);
            goodsImgView.setVisibility(View.GONE);
            brView2.setVisibility(View.GONE);
            rBarNameTextView.setPaddingRelative(10, 60, 10, 30);
            evaluateRBar.setPaddingRelative(10, 60, 0, 30);
        }

        goodsContentTextView = findViewById(R.id.goodsContentTextView);
        goodsContentTextView.setText(rvContent);
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // 리뷰 쓴 사용자의 정보 가져오기
    private void getSellerInfo(String otherUid){
        databaseReference.child("users").child(otherUid).child("shopName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopName = snapshot.getValue(String.class);
                shopNameTextView.setText(shopName);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("users").child(otherUid).child("profileImageUrl").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                profileImageUrl = snapshot.getValue(String.class);

                if (profileImageUrl != null){ // 프로필 사진을 등록했다면 원형크롭해서 프로필에 올리기
                    Glide.with(getApplicationContext()).load(profileImageUrl).circleCrop()
                            .placeholder(R.drawable.ic_baseline_person_24).into(profileImgView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("users").child(otherUid).child("initialRBar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                otherRBarScore = snapshot.getValue(float.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
