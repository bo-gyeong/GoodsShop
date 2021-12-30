package com.example.goodsshop;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Purchase extends AppCompatActivity {

    ListView purchaseListView;

    String getMyUid;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase);

        getSupportActionBar().setTitle("구매내역");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        getMyUid = MainActivity.sharedUid();

        getPurchaseInfo();

        purchaseListView = findViewById(R.id.purchaseListView);

        // 리스트뷰 클릭 시 설정
        purchaseListView.setClickable(false);
        purchaseListView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    }

    ArrayList<PurchaseData> showPurchaseInfoArr = new ArrayList<>();

    // 리스트뷰로 판매내역 나열
    void makePurchaseList(ListView purchaseListView) {
        PurchaseData pData = new PurchaseData();

        pData.goodsImgView = getImgUrl;
        pData.goodsNameTextView = getGoodsName;
        pData.goodsPriceTextView = getPrice;
        pData.shopNameTextView = getShopName;
        pData.dateTextView = getPurchaseDate;

        showPurchaseInfoArr.add(pData);

        ListAdapter myListAdapter = new ShowPurchaseList(showPurchaseInfoArr);

        purchaseListView.setAdapter(myListAdapter);
    }

    String getSellerUid, getShopName, getGoodsName, getPrice, getImgUrl, getPurchaseDate;

    //  리얼타임디비에서 나의 구매내역 불러오기
    private void getPurchaseInfo(){
        databaseReference.child("purchase").child(getMyUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ctgrySnapshot, @Nullable String previousChildName) {
                databaseReference.child("purchase").child(getMyUid)
                        .child(ctgrySnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot goodsNameSnapshot, @Nullable String previousChildName) {
                        databaseReference.child("purchase").child(getMyUid).child(ctgrySnapshot.getKey())
                                .child(goodsNameSnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                if (snapshot.getKey().equals("goodsImgUrl")){
                                    getImgUrl = String.valueOf(snapshot.getValue());
                                }
                                else if (snapshot.getKey().equals("price")){
                                    getPrice = String.valueOf(snapshot.getValue());
                                }
                                else if (snapshot.getKey().equals("purchaseDate")){
                                    getPurchaseDate = String.valueOf(snapshot.getValue());
                                }
                                else if (snapshot.getKey().equals("sellerUid")){
                                    getSellerUid = String.valueOf(snapshot.getValue());
                                }
                                else if (snapshot.getKey().equals("shopName")){
                                    getShopName = String.valueOf(snapshot.getValue());

                                    getGoodsName = goodsNameSnapshot.getKey();

                                    makePurchaseList(purchaseListView);
                                }
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
