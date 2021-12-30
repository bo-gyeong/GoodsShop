package com.example.goodsshop;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

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

public class Sales extends AppCompatActivity {

    TabHost tabHost;
    ListView onSaleListView, reservationListView, salesCompletedListView;

    Intent fromSeeOtherShopIntent;
    String getUid, status;
    boolean fromSeeOtherShop;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sales);

        getSupportActionBar().setTitle("판매내역");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        fromSeeOtherShopIntent = getIntent();
        fromSeeOtherShop = fromSeeOtherShopIntent.getBooleanExtra("fromSeeOtherShop", false);

        if (fromSeeOtherShop){
            getUid = fromSeeOtherShopIntent.getStringExtra("getUid");
        } else{
            getUid = MainActivity.sharedUid();
        }

        tabHost = findViewById(R.id.tabHost);
        tabHost.setup();

        // 탭 만들기
        TabHost.TabSpec onSale = tabHost.newTabSpec("onSale").setIndicator("판매중");
        onSale.setContent(R.id.onSale);
        tabHost.addTab(onSale);

        onSaleListView = findViewById(R.id.onSaleListView);
        getGoodsInfoFromRealtimeDB("onSale", onSaleListView);

        TabHost.TabSpec reservation = tabHost.newTabSpec("reservation").setIndicator("예약중");
        reservation.setContent(R.id.reservation);
        tabHost.addTab(reservation);

        reservationListView = findViewById(R.id.reservationListView);

        TabHost.TabSpec salesCompleted = tabHost.newTabSpec("salesCompleted").setIndicator("판매완료");
        salesCompleted.setContent(R.id.salesCompleted);
        tabHost.addTab(salesCompleted);

        salesCompletedListView = findViewById(R.id.salesCompletedListView);

        // 각 탭 클릭 시
        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                switch (tabId){
                    case "onSale":
                        status = "판매중";
                        getGoodsInfoFromRealtimeDB("onSale", onSaleListView);
                        break;
                    case "reservation":
                        status = "예약중";
                        getGoodsInfoFromRealtimeDB("reservation", reservationListView);
                        break;
                    case "salesCompleted":
                        status = "판매완료";
                        getGoodsInfoFromRealtimeDB("salesCompleted", salesCompletedListView);
                        break;
                }
            }
        });
    }

    ArrayList<String> getGoodsName, getPrice, getImgUrl, getPost, getParcel, getDirectDealing
            , getCondition, getExplain, getRegiDate, getJjimCnt, getCtgry, saleState;
    ArrayList<Integer> getPicCount;
    ArrayList<ArrayList<String>> getImgUrlArrList;

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String price, imgUrl, picCount, date, goodsName, salesStatus, ctgry;
    ArrayList<MypageSalesData> showSaleArrListDataMypage = new ArrayList<>();

    // 나의 샵 페이지의 판매내역
    void getOnSaleMypage(ListView saleListView){
        MypageSalesData salesData = new MypageSalesData();

        salesData.goodsImgView = imgUrl;
        salesData.goodsNameTextView = goodsName;
        salesData.statusTextView = status;
        salesData.goodsPriceTextView = price;
        salesData.dateTextView = date;
        salesData.moreImgView = R.drawable.ic_baseline_more_vert_24;

        salesData.salesState = salesStatus;
        salesData.ctgry = ctgry;

        showSaleArrListDataMypage.add(salesData);

        ListAdapter myListAdapter = new ShowMypageSaleList(showSaleArrListDataMypage);

        // 리스트뷰로 판매내역 나열
        saleListView.setAdapter(myListAdapter);
        saleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ClickGoods.class);

                intent.putExtra("uid", getUid);
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
        });
    }

    ArrayList<OtherSalesData> showSaleArrListDataOther = new ArrayList<>();

    // 다른 사람 프로필 보기 페이지의 판매 내역
    void getOnSaleOther(ListView saleListView){
        OtherSalesData salesData = new OtherSalesData();

        salesData.goodsImgView = imgUrl;
        salesData.goodsNameTextView = goodsName;
        salesData.statusTextView = status;
        salesData.goodsPriceTextView = price;
        salesData.dateTextView = date;

        showSaleArrListDataOther.add(salesData);

        ListAdapter otherListAdapter = new ShowOtherSaleList(showSaleArrListDataOther);

        // 리스트뷰로 판매내역 나열
        saleListView.setAdapter(otherListAdapter);
        saleListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ClickGoods.class);

                intent.putExtra("uid", getUid);
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
        });
    }

    //boolean exist;
    // 리얼타임 데베에서 등록된 상품 가져오기
    private void getGoodsInfoFromRealtimeDB(String state, ListView saleListView){

        getGoodsName = new ArrayList<>(); getPrice = new ArrayList<>(); saleState = new ArrayList<>();
        getImgUrl = new ArrayList<>(); getPost = new ArrayList<>(); getParcel = new ArrayList<>();
        getDirectDealing = new ArrayList<>(); getCondition = new ArrayList<>(); getExplain = new ArrayList<>();
        getRegiDate = new ArrayList<>(); getJjimCnt = new ArrayList<>(); getPicCount = new ArrayList<>();
        getCtgry = new ArrayList<>(); getImgUrlArrList = new ArrayList<>();

        showSaleArrListDataMypage = new ArrayList<>();
        showSaleArrListDataOther = new ArrayList<>();

        databaseReference.child("regiGoods").child(getUid).child(state).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ctgrySnapshot, @Nullable String previousChildName) {

                databaseReference.child("regiGoods").child(getUid).child(state)
                        .child(ctgrySnapshot.getKey()).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        goodsName = snapshot.getKey();
                        getGoodsName.add(snapshot.getKey());

                        ctgry = ctgrySnapshot.getKey();
                        getCtgry.add(ctgrySnapshot.getKey());

                        salesStatus = state;
                        saleState.add(salesStatus);

                        for(DataSnapshot dSnapshot : snapshot.getChildren())
                        {
                            if (dSnapshot.getKey().equals("price")){
                                price = String.valueOf(dSnapshot.getValue());
                                getPrice.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("regiImageUrl")){
                                imgUrl = String.valueOf(dSnapshot.child("0").getValue());
                                ArrayList<String> arr;
                                for (int i=0; i<Integer.parseInt(picCount); i++){
                                    getImgUrl.add(String.valueOf(dSnapshot.child(String.valueOf(i)).getValue()));
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
                                date = String.valueOf(dSnapshot.getValue());
                                getRegiDate.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("jjimCnt")){
                                getJjimCnt.add(String.valueOf(dSnapshot.getValue()));
                            }
                            else if (dSnapshot.getKey().equals("picCount")){
                                picCount = String.valueOf(dSnapshot.getValue());
                                getPicCount.add(Integer.parseInt(String.valueOf(dSnapshot.getValue())));
                            }
                        }
                        if (fromSeeOtherShop){
                            getOnSaleOther(saleListView);
                        }
                        else{
                            getOnSaleMypage(saleListView);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home: //toolbar의 back키 눌렀을 때 동작
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
