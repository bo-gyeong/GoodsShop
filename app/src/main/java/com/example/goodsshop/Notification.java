package com.example.goodsshop;

import android.content.Intent;
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

public class Notification extends AppCompatActivity {

    ListView notificationListView;

    String getMyUid, goodsName, otherShopName;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification);

        getSupportActionBar().setTitle("알림");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        getMyUid = MainActivity.sharedUid();

        notificationListView = findViewById(R.id.notificationListView);

        getNotiInfo();
    }

    //  리뷰 작성 끝나면 이 액티비티도 finish()
    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    ArrayList<NotificationData> showNotiArr = new ArrayList<>();

    // 리스트뷰로 알림 나열
    void makeNotiList(ListView notiListView) {
        NotificationData notiData = new NotificationData();

        notiData.shopNameNotiTextView = otherShopName;
        notiData.goodsNameNotiTextView = goodsName;

        showNotiArr.add(notiData);

        ListAdapter myListAdapter = new ShowNotification(showNotiArr);

        notiListView.setAdapter(myListAdapter);
        notiListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), WriteReview.class);
                intent.putExtra("otherUid", getSellerUid.get(position));
                intent.putExtra("goodsName", getGoodsName.get(position));
                intent.putExtra("goodsCtgry", getCtgry.get(position));

                startActivity(intent);
            }
        });
    }

    ArrayList<String> getSellerUid, getShopName, getGoodsName, getCtgry;

    private void getNotiInfo(){
        getSellerUid = new ArrayList<>(); getShopName = new ArrayList<>();
        getGoodsName = new ArrayList<>(); getCtgry = new ArrayList<>();

        databaseReference.child("notifications").child(getMyUid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ctgrySnapshot, @Nullable String previousChildName) {
                databaseReference.child("notifications").child(getMyUid)
                        .child(ctgrySnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot goodsNameSnapshot, @Nullable String previousChildName) {
                        databaseReference.child("notifications").child(getMyUid).child(ctgrySnapshot.getKey())
                                .child(goodsNameSnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                                if (snapshot.getKey().equals("sellerUid")){
                                    getSellerUid.add(String.valueOf(snapshot.getValue()));
                                }
                                else if (snapshot.getKey().equals("shopName")){
                                    otherShopName = String.valueOf(snapshot.getValue());
                                    getShopName.add(otherShopName);

                                    goodsName = goodsNameSnapshot.getKey();
                                    getGoodsName.add(goodsName);

                                    getCtgry.add(ctgrySnapshot.getKey());

                                    makeNotiList(notificationListView);
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
