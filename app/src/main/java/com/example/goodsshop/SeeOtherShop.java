package com.example.goodsshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SeeOtherShop extends AppCompatActivity {

    ImageView personImgView;
    TextView shopName, reviewCnt;
    RatingBar believeratingbar;
    Button allSaleListBtn;
    ListView reviewlist;

    Intent getShopInfoIntent;
    String getUid, getShopName, profileImgUrl;
    float rBarNum;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.see_other_shop);

        getSupportActionBar().setTitle("프로필");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        getShopInfoIntent = getIntent();
        getUid = getShopInfoIntent.getStringExtra("getUid");
        getShopName = getShopInfoIntent.getStringExtra("otherShopName");
        profileImgUrl = getShopInfoIntent.getStringExtra("profileImgUrl");
        rBarNum = getShopInfoIntent.getFloatExtra("otherRBar", 3);

        personImgView = findViewById(R.id.personImgView);
        if (profileImgUrl != null){ // 프로필 사진을 등록했다면 원형크롭해서 프로필에 올리기
            Glide.with(getApplicationContext()).load(profileImgUrl).circleCrop()
                    .placeholder(R.drawable.ic_baseline_person_24).into(personImgView);
        }

        shopName = findViewById(R.id.shopName);
        shopName.setText(getShopName);

        believeratingbar = findViewById(R.id.believeratingbar);
        believeratingbar.setRating(rBarNum);

        allSaleListBtn = findViewById(R.id.viewallsalelist);
        allSaleListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Sales.class);
                intent.putExtra("fromSeeOtherShop", true);
                intent.putExtra("getUid", getUid);
                startActivity(intent);
            }
        });  //판매내역 보기

        reviewCnt = findViewById(R.id.reviewCnt);
        reviewlist = findViewById(R.id.reviewlist);

        getReview();
    }

    ArrayList<String> getRBarScoreArr, getRvContentArr, getRvDateArr, getRvImgUrlArr, getShopNameArr, getUidArr;
    ArrayList<ListData> reviewsArrList = new ArrayList<>();

    // 리뷰 리스트뷰 만들기
    private void setReviewList(){
        ListData listData = new ListData();

        listData.goodsImgView = rvImgUrl;
        listData.shopNameTextView = shopNameStr;
        listData.evaluateRBar = Float.parseFloat(rBarScore);
        listData.goodsContentTextView = rvContent;
        listData.dateTextView = rvDate;

        reviewsArrList.add(listData);

        reviewCnt.setText(String.valueOf(reviewCount));

        //리스트뷰로 리뷰 나열
        ListAdapter liAdapter = new ShowReviewsList(reviewsArrList);
        reviewlist.setAdapter(liAdapter);

        reviewlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ReviewDetail.class);
                intent.putExtra("getRBarScore", getRBarScoreArr.get(position));
                intent.putExtra("getRvContent", getRvContentArr.get(position));
                intent.putExtra("getRvDate", getRvDateArr.get(position));
                intent.putExtra("getRvImgUrl", getRvImgUrlArr.get(position));
                intent.putExtra("getUid", getUidArr.get(position));

                startActivity(intent);
            }
        }); //리스트 클릭 후
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String rBarScore, rvContent, rvDate, rvImgUrl, shopNameStr, uid;
    boolean getRvImg;
    int reviewCount;

    // 리뷰 가져오기
    private void getReview(){
        getRBarScoreArr = new ArrayList<>();getRvContentArr = new ArrayList<>();
        getRvDateArr = new ArrayList<>(); getRvImgUrlArr = new ArrayList<>();
        getShopNameArr = new ArrayList<>(); getUidArr = new ArrayList<>();

        reviewCount = 0;

        databaseReference.child("reviews").child(getUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ctgrySnapshot, @Nullable String previousChildName) {
                databaseReference.child("reviews").child(getUid)
                        .child(ctgrySnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot goodsNameSnapshot, @Nullable String previousChildName) {
                        databaseReference.child("reviews").child(getUid).child(ctgrySnapshot.getKey())
                                .child(goodsNameSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                getRvImg = false;

                                for (DataSnapshot dSnapshot : snapshot.getChildren()){
                                    if (dSnapshot.getKey().equals("rBarScore")){
                                        rBarScore = String.valueOf(dSnapshot.getValue());
                                        getRBarScoreArr.add(rBarScore);
                                    }
                                    else if (dSnapshot.getKey().equals("rvContent")){
                                        rvContent = String.valueOf(dSnapshot.getValue());
                                        getRvContentArr.add(rvContent);
                                    }
                                    else if (dSnapshot.getKey().equals("rvDate")){
                                        rvDate = String.valueOf(dSnapshot.getValue());
                                        getRvDateArr.add(rvDate);
                                    }
                                    else if (dSnapshot.getKey().equals("rvImgUrl")){
                                        rvImgUrl = String.valueOf(dSnapshot.getValue());
                                        getRvImgUrlArr.add(rvImgUrl);

                                        getRvImg = true;
                                    }
                                    else if (dSnapshot.getKey().equals("shopName")){
                                        shopNameStr = String.valueOf(dSnapshot.getValue());
                                        getShopNameArr.add(shopNameStr);
                                    }
                                    else if (dSnapshot.getKey().equals("uid")){
                                        uid = String.valueOf(dSnapshot.getValue());
                                        getUidArr.add(uid);
                                        reviewCount++;

                                        if (!getRvImg){
                                            getRvImgUrlArr.add("");
                                        }
                                    }
                                }
                                setReviewList();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
