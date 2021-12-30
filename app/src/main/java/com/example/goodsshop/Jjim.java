package com.example.goodsshop;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Jjim extends AppCompatActivity {

    ProgressBar jjimProgressBar;
    TextView noJjimTextView;
    GridView showJjimGoodsGridView;

    String getMyUid, getMyUidFromDB;
    JjimGoodsInfo jjimGoodsInfo = new JjimGoodsInfo();
    ArrayList<JjimGoodsInfo> goodsInfoArrList = new ArrayList<>();

    @Override
    protected void onResume() {
        getUid = new ArrayList<>(); getGoodsName = new ArrayList<>(); getPrice = new ArrayList<>();
        getImgUrl = new ArrayList<>(); getPost = new ArrayList<>(); getParcel = new ArrayList<>();
        getDirectDealing = new ArrayList<>(); getCondition = new ArrayList<>(); getExplain = new ArrayList<>();
        getRegiDate = new ArrayList<>(); getJjimCnt = new ArrayList<>(); getPicCount = new ArrayList<>();
        getCtgry = new ArrayList<>(); getImgUrlArrList = new ArrayList<>(); saleState = new ArrayList<>();

        super.onResume();
        setGridView();
    }

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jjim);

        getSupportActionBar().setTitle("찜한 상품");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        jjimProgressBar = findViewById(R.id.jjimProgressBar);

        getMyUidFromDB = null;
        getMyUid = MainActivity.sharedUid();

        showJjimGoodsGridView = (GridView)findViewById(R.id.showJjimGoodsGridView);
        noJjimTextView = findViewById(R.id.noJjimTextView);
    }

    ArrayList<String> getUid = new ArrayList<>(), getGoodsName = new ArrayList<>(), getPrice = new ArrayList<>()
            , getImgUrl = new ArrayList<>(), getPost = new ArrayList<>(), getParcel = new ArrayList<>()
            , getDirectDealing = new ArrayList<>(), getCondition = new ArrayList<>(), getExplain = new ArrayList<>()
            ,  getRegiDate = new ArrayList<>(), getJjimCnt = new ArrayList<>(), getCtgry = new ArrayList<>()
            , saleState = new ArrayList<>();
    ArrayList<Integer> getPicCount = new ArrayList<>();
    ArrayList<ArrayList<String>> getImgUrlArrList = new ArrayList<>();

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    ArrayList<JjimGoodsData> goodsArrList = new ArrayList<>();
    boolean finish;

    private void setGridView(){
        if (getMyUid == null){
            noJjimTextView.setVisibility(View.VISIBLE);
        } else{
            finish = false;
            goodsArrList = new ArrayList<>();

            databaseReference.child("users").child(getMyUid).child("jjim").addChildEventListener(new ChildEventListener() {

                @Override
                public void onChildAdded(@NonNull DataSnapshot sellerUidSnapshot, @Nullable String previousChildName) {
                    databaseReference.child("users").child(getMyUid).child("jjim")
                            .child(sellerUidSnapshot.getKey()).addChildEventListener(new ChildEventListener() {

                        @Override
                        public void onChildAdded(@NonNull DataSnapshot ctgrySnapshot, @Nullable String previousChildName) {
                            databaseReference.child("users").child(getMyUid).child("jjim")
                                    .child(sellerUidSnapshot.getKey()).child(ctgrySnapshot.getKey())
                                    .addChildEventListener(new ChildEventListener() {

                                        @Override
                                        public void onChildAdded(@NonNull DataSnapshot goodsNameSnapshot, @Nullable String previousChildName) {
                                            databaseReference.child("users").child(getMyUid).child("jjim")
                                                    .child(sellerUidSnapshot.getKey()).child(ctgrySnapshot.getKey())
                                                    .child(goodsNameSnapshot.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {

                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                    JjimGoodsData jjimGoodsData = new JjimGoodsData();

                                                    int picCnt = 0;
                                                    getUid.add(sellerUidSnapshot.getKey());
                                                    getCtgry.add(ctgrySnapshot.getKey());
                                                    jjimGoodsData.goodsTitleTextView = goodsNameSnapshot.getKey();
                                                    getGoodsName.add(goodsNameSnapshot.getKey());

                                                    for(DataSnapshot dSnapshot : snapshot.getChildren())
                                                    {
                                                        if (dSnapshot.getKey().equals("picCount")){
                                                            picCnt = Integer.parseInt(String.valueOf(dSnapshot.getValue()));
                                                            getPicCount.add(picCnt);
                                                        }
                                                        else if (dSnapshot.getKey().equals("regiImageUrl")){
                                                            jjimGoodsData.goodsImgView = String.valueOf(dSnapshot.getValue());
                                                            finish = true;
                                                        }
                                                        else if (dSnapshot.getKey().equals("price")){
                                                            jjimGoodsData.priceTextView = Integer.parseInt(String.valueOf(dSnapshot.getValue()));
                                                        }

                                                    }
                                                    jjimGoodsData.jjimImgView = R.drawable.ic_baseline_favorite_24;

                                                    goodsArrList.add(jjimGoodsData);
                                                    getGoodsInfoFromRealtimeDB(sellerUidSnapshot.getKey(), ctgrySnapshot.getKey(),  goodsNameSnapshot.getKey(), picCnt);
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

        showJjimGoodsGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (saleState.get(position).equals(".")){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(Jjim.this);
                    dlg.setMessage("삭제된 상품입니다");
                    dlg.setPositiveButton("확인", null);
                    dlg.show();
                } else{
                    Intent intent = new Intent(getApplicationContext(), ClickGoods.class);
                    intent.putExtra("uid", getUid.get(position));
                    intent.putExtra("goodsName", getGoodsName.get(position));
                    intent.putExtra("category", getCtgry.get(position));
                    intent.putExtra("imgUrlArrList", getImgUrlArrList.get(position));
                    intent.putExtra("picCount", getPicCount.get(position));
                    intent.putExtra("price", getPrice.get(position));
                    intent.putExtra("post", Boolean.valueOf(getPost.get(position)));
                    intent.putExtra("parcel", Boolean.valueOf(getParcel.get(position)));
                    intent.putExtra("directDealing", Boolean.valueOf(getDirectDealing.get(position)));
                    intent.putExtra("condition", getCondition.get(position));
                    intent.putExtra("explain", getExplain.get(position));
                    intent.putExtra("regiDate", getRegiDate.get(position));
                    intent.putExtra("jjimCnt", getJjimCnt.get(position));
                    intent.putExtra("saleState", saleState.get(position));

                    startActivity(intent);
                }
            }
        });

        jjimProgressBar.bringToFront();
        jjimProgressBar.setVisibility(View.VISIBLE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ShowJjimGoods showJjimGoods = new ShowJjimGoods(goodsArrList, goodsInfoArrList);
                jjimProgressBar.setVisibility(View.INVISIBLE);
                if (!finish){
                    noJjimTextView.setVisibility(View.VISIBLE);
                    showJjimGoodsGridView.setVisibility(View.GONE);
                }
                else{
                    showJjimGoodsGridView.setVisibility(View.VISIBLE);
                    showJjimGoodsGridView.setAdapter(showJjimGoods);
                }
            }
        }, 1000);

    }

    // 리얼타임 데베에서 등록된 상품 가져오기
    private void getGoodsInfoFromRealtimeDB(String uid, String ctgry, String goodsName, int picCount){

        databaseReference.child("regiGoods").child(uid).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot saleStatusSnapshot, @Nullable String previousChildName) {

                databaseReference.child("regiGoods").child(uid).child(saleStatusSnapshot.getKey())
                        .child(ctgry).child(goodsName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        // 삭제된 상품이면 이상한 값 추가해서 다른 찜 상품들 클릭했을 때 올바른 값 나올 수 있도록
                        if (!snapshot.getChildren().iterator().hasNext()){
                            getPrice.add("0");
                            saleState.add(".");
                            getImgUrl.add(".");
                            getImgUrlArrList.add(getImgUrl);
                            getImgUrl.clear();
                            getPost.add(".");
                            getParcel.add(".");
                            getDirectDealing.add(".");
                            getCondition.add(".");
                            getExplain.add(".");
                            getRegiDate.add(".");
                            getJjimCnt.add("1");
                            getPicCount.add(0);
                        }

                        for (DataSnapshot dSnapshot: snapshot.getChildren()){

                            if (dSnapshot.getKey().equals("price")){

                                getPrice.add(String.valueOf(dSnapshot.getValue()));

                                saleState.add(saleStatusSnapshot.getKey());
                            }
                            else if (dSnapshot.getKey().equals("regiImageUrl")){
                                ArrayList<String> arr;
                                for (int j = 0; j<picCount; j++){
                                    getImgUrl.add(String.valueOf(dSnapshot.child(String.valueOf(j)).getValue()));
                                }
                                arr = new ArrayList<>(getImgUrl);
                                getImgUrlArrList.add(arr);
                                getImgUrl.clear();
                            }
                            else if (dSnapshot.getKey().equals("post")){
                                getPost.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("parcel")){
                                getParcel.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("directDealing")){
                                getDirectDealing.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("condition")){
                                getCondition.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("explain")){
                                getExplain.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("regiDate")){
                                getRegiDate.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("jjimCnt")){
                                getJjimCnt.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("picCount")){
                                getPicCount.add(Integer.parseInt(String.valueOf(dSnapshot.getValue())));
                            }

                            jjimGoodsInfo.getMyUid = getMyUid;
                            jjimGoodsInfo.sellerUid = getUid;
                            jjimGoodsInfo.category = getCtgry;
                            jjimGoodsInfo.goodsName = getGoodsName;
                            jjimGoodsInfo.price = getPrice;
                            jjimGoodsInfo.picCount = getPicCount;
                            jjimGoodsInfo.jjimCnt = getJjimCnt;
                            jjimGoodsInfo.imgUrlArrList = getImgUrlArrList;

                            goodsInfoArrList.add(jjimGoodsInfo);
                        }
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
