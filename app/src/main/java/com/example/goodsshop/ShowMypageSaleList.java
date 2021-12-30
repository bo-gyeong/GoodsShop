package com.example.goodsshop;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

//판매내역관련 코드
public class ShowMypageSaleList extends BaseAdapter {

    LayoutInflater layoutInflater = null;
    ArrayList<MypageSalesData> showSaleArrListData = null;
    int count = 0;
    View finalConvertView;

    String getUid, goodsName, getImgName, picCount, salesState, ctgry, condition
            , explain, regiDate, jjimCnt, shopName, otherToken;
    ArrayList<String> regiImageUri = new ArrayList<>();
    int price;
    boolean post, parcel, directDealing;
    ArrayList<String> chatShopNameArr = new ArrayList<>();
    ArrayList<String> chatUidArr = new ArrayList<>();

    public ShowMypageSaleList(ArrayList<MypageSalesData> salesArrList)
    {
        showSaleArrListData = salesArrList;
        count = salesArrList.size();
    }

    @Override
    public int getCount()
    {
        return count;
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            Context context = parent.getContext();
            if (layoutInflater == null)
            {
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = layoutInflater.inflate(R.layout.show_mypage_sale_list, parent, false);
        }

        getUid = MainActivity.sharedUid();

        ProgressBar salesProgressBar = convertView.findViewById(R.id.salesProgressBar);
        ImageView goodsImgView = convertView.findViewById(R.id.goodsImgView);
        TextView goodsNameTextView = convertView.findViewById(R.id.goodsNameTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
        TextView goodsPriceTextView = convertView.findViewById(R.id.goodsPriceTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);
        ImageView moreImgView = convertView.findViewById(R.id.moreImgView);

        Glide.with(convertView).load(showSaleArrListData.get(position).goodsImgView).centerCrop().into(goodsImgView);
        goodsNameTextView.setText(showSaleArrListData.get(position).goodsNameTextView);
        statusTextView.setText(showSaleArrListData.get(position).statusTextView);
        if (statusTextView.getText().equals("예약중")){
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setPadding(10,3,10,3);
            statusTextView.setBackground(ContextCompat.getDrawable(convertView.getContext(), R.drawable.colorprimary_light_round));
        }
        else if (statusTextView.getText().equals("판매완료")){
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setPadding(10,3,10,3);
        }
        goodsPriceTextView.setText(showSaleArrListData.get(position).goodsPriceTextView);
        dateTextView.setText(showSaleArrListData.get(position).dateTextView);
        moreImgView.setImageResource(showSaleArrListData.get(position).moreImgView);

        finalConvertView = convertView;
        moreImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] categoryItems = new String[] {"상태변경", "수정", "삭제"};
                final String[] changeStatus = new String[] {"판매중", "예약중", "판매완료"};

                AlertDialog.Builder dlg = new AlertDialog.Builder(parent.getContext());
                dlg.setItems(categoryItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goodsName = showSaleArrListData.get(position).goodsNameTextView;
                        getChatUser();

                        if (categoryItems[which].equals("상태변경")){
                            dlg.setItems(changeStatus, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String changeStateStr;
                                    salesState = showSaleArrListData.get(position).salesState;
                                    ctgry = showSaleArrListData.get(position).ctgry;

                                    if (changeStatus[which].equals("판매중")){
                                        // 예약중일때만 판매중으로 변경 가능
                                        if (salesState.equals("reservation")){
                                            salesProgressBar.setVisibility(View.VISIBLE);

                                            changeStateStr = "onSale";
                                            changeStatus(changeStateStr);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (isRegiSuccess){
                                                        Toast.makeText(v.getContext(), "변경되었습니다", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(v.getContext(), Sales.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        v.getContext().startActivity(intent);

                                                        salesProgressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                            }, 1000);
                                        } else{
                                            Toast.makeText(v.getContext(), "변경이 불가능합니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else if (changeStatus[which].equals("예약중")){
                                        // 판매중일때만 예약중으로 변경 가능
                                        if (salesState.equals("onSale")){
                                            salesProgressBar.setVisibility(View.VISIBLE);

                                            changeStateStr = "reservation";
                                            changeStatus(changeStateStr);

                                            new Handler().postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (isRegiSuccess){
                                                        Toast.makeText(v.getContext(), "변경되었습니다", Toast.LENGTH_SHORT).show();

                                                        Intent intent = new Intent(v.getContext(), Sales.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                        v.getContext().startActivity(intent);

                                                        salesProgressBar.setVisibility(View.GONE);
                                                    }
                                                }
                                            }, 1000);
                                        }else{
                                            Toast.makeText(v.getContext(), "변경이 불가능합니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    else if (changeStatus[which].equals("판매완료")){
                                        if (!salesState.equals("salesCompleted")){
                                            changeStateStr = "salesCompleted";

                                            final String[] testShopName = chatShopNameArr.toArray(new String[chatShopNameArr.size()]);
                                            final String[] testUid = chatUidArr.toArray(new String[chatUidArr.size()]);

                                            if (testShopName.length != 0){
                                                dlg.setItems(testShopName, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int whichUser) {
                                                        dlg.setTitle("판매완료로 변경하시겠습니까?");
                                                        dlg.setMessage("판매완료가 되면 더 이상 상태를 변경할 수 없습니다.");
                                                        dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                salesProgressBar.setVisibility(View.VISIBLE);

                                                                changeStatus(changeStateStr);

                                                                getGoodsInfo();
                                                                getOtherToken(testUid[whichUser]);

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        purchaseGoods(testUid[whichUser]);
                                                                    }
                                                                }, 1000);

                                                                new Handler().postDelayed(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        if (isRegiSuccess){
                                                                            Toast.makeText(v.getContext(), "변경되었습니다", Toast.LENGTH_SHORT).show();

                                                                            Intent intent = new Intent(v.getContext(), Sales.class);
                                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                            v.getContext().startActivity(intent);

                                                                            salesProgressBar.setVisibility(View.GONE);
                                                                        }
                                                                    }
                                                                }, 1000);
                                                            }
                                                        }).show();

                                                    }
                                                }).show();
                                            } else{
                                                dlg.setTitle("구매자가 없는 상태로 판매완료 하시겠습니까?");
                                                dlg.setMessage("판매완료가 되면 더 이상 상태를 변경할 수 없습니다.");
                                                dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        changeStatus(changeStateStr);

                                                        new Handler().postDelayed(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                if (isRegiSuccess){
                                                                    Toast.makeText(v.getContext(), "변경되었습니다", Toast.LENGTH_SHORT).show();

                                                                    Intent intent = new Intent(v.getContext(), Sales.class);
                                                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                                    v.getContext().startActivity(intent);

                                                                    salesProgressBar.setVisibility(View.GONE);
                                                                }
                                                            }
                                                        }, 1000);
                                                    }
                                                }).show();
                                            }
                                        }else{
                                            Toast.makeText(v.getContext(), "변경이 불가능합니다", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            dlg.show();
                        }
                        else if (categoryItems[which].equals("수정")){
                            salesState = showSaleArrListData.get(position).salesState;
                            ctgry = showSaleArrListData.get(position).ctgry;

                            if (salesState.equals("onSale")){
                                getGoodsInfo();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        intent.putExtra("showMypageList", true);

                                        intent.putExtra("uid", getUid);
                                        intent.putExtra("goodsName", goodsName);
                                        intent.putExtra("category", ctgry);
                                        intent.putExtra("imgUrlArrList", getImgUrl);
                                        intent.putExtra("regiImageUri", regiImageUri);
                                        intent.putExtra("picCount", picCount);
                                        intent.putExtra("price", price);
                                        intent.putExtra("post", post);
                                        intent.putExtra("parcel", parcel);
                                        intent.putExtra("directDealing", directDealing);
                                        intent.putExtra("condition", condition);
                                        intent.putExtra("explain", explain);
                                        intent.putExtra("regiDate", regiDate);
                                        intent.putExtra("jjimCnt", jjimCnt);
                                        intent.putExtra("salesState", salesState);

                                        v.getContext().startActivity(intent);
                                    }
                                }, 1000);
                            } else{
                                Toast.makeText(v.getContext(), "판매중에만 수정이 가능합니다", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (categoryItems[which].equals("삭제")){
                            dlg.setMessage("삭제하시겠습니까?");
                            dlg.setNegativeButton("취소", null);
                            dlg.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    salesProgressBar.setVisibility(View.VISIBLE);

                                    salesState = showSaleArrListData.get(position).salesState;
                                    ctgry = showSaleArrListData.get(position).ctgry;

                                    deleteGoods();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isRegiSuccess && isUsersSuccess){
                                                Toast.makeText(v.getContext(), "삭제되었습니다", Toast.LENGTH_SHORT).show();

                                                Intent intent = new Intent(v.getContext(), Sales.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                v.getContext().startActivity(intent);

                                                salesProgressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    }, 1000);
                                }
                            });
                            dlg.show();
                        }
                    }
                });
                dlg.show();
            }
        });  //카테고리 클릭 시 나오는 대화상자

        return convertView;
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("regiGoods");
    boolean isRegiSuccess, isUsersSuccess, finish;

    ArrayList<String> getImgUrl = new ArrayList<>();
    ArrayList<ArrayList<String>> getImgUrlArrList = new ArrayList<>();

    // 판매 상태 변경하기
    private void changeStatus(String changeStateStr){

        DatabaseReference path = databaseReference.child("regiGoods").child(getUid).child(salesState)
                .child(ctgry).child(goodsName);

        finish = false;
        isRegiSuccess = false;
        isUsersSuccess = false;
        getImgUrl = new ArrayList<>();
        getImgUrlArrList = new ArrayList<>();

        RegiGoods regiGoods = new RegiGoods();

        path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dSnapshot : snapshot.getChildren())
                {
                    if (dSnapshot.getKey().equals("price")){
                        regiGoods.setPrice(Integer.parseInt(String.valueOf(dSnapshot.getValue())));
                    }
                    else if (dSnapshot.getKey().equals("regiImageUrl")){
                        for (int i=0; i<Integer.parseInt(picCount); i++){
                            getImgUrl.add(String.valueOf(dSnapshot.child(String.valueOf(i)).getValue()));
                        }
                        regiGoods.setRegiImageUrl(getImgUrl);
                        finish = true;
                    }
                    else if (dSnapshot.getKey().equals("post")){
                        String post = String.valueOf(dSnapshot.getValue());
                        regiGoods.setPost(Boolean.parseBoolean(post));
                    }
                    else if (dSnapshot.getKey().equals("parcel")){
                        regiGoods.setParcel(Boolean.parseBoolean(String.valueOf(dSnapshot.getValue())));
                    }
                    else if (dSnapshot.getKey().equals("directDealing")){
                        regiGoods.setDirectDealing(Boolean.parseBoolean(String.valueOf(dSnapshot.getValue())));
                    }
                    else if (dSnapshot.getKey().equals("condition")){
                        regiGoods.setCondition(String.valueOf(dSnapshot.getValue()));
                    }
                    else if (dSnapshot.getKey().equals("explain")){
                        regiGoods.setExplain(String.valueOf(dSnapshot.getValue()));
                    }
                    else if (dSnapshot.getKey().equals("regiDate")){
                        regiGoods.setRegiDate(String.valueOf(dSnapshot.getValue()));
                    }
                    else if (dSnapshot.getKey().equals("jjimCnt")){
                        regiGoods.setJjimCnt(Integer.parseInt(String.valueOf(dSnapshot.getValue())));
                    }
                    else if (dSnapshot.getKey().equals("picCount")){
                        picCount = String.valueOf(dSnapshot.getValue());
                        regiGoods.setPicCount(Integer.parseInt(picCount));
                    }
                }

                if (finish){  //리얼타임 데베 값 삭제 후 새로 생성
                    path.removeValue();

                    databaseReference.child("regiGoods").child(getUid).child(changeStateStr).child(ctgry).child(goodsName).setValue(regiGoods);

                    isRegiSuccess = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    boolean equal;

    //  채팅한 사람 불러오기
    private void getChatUser(){
        chatShopNameArr = new ArrayList<>();
        chatUidArr = new ArrayList<>();

        databaseReference.child("chatrooms").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot randSnapshot, @Nullable String previousChildName) {

                databaseReference.child("chatrooms").child(randSnapshot.getKey())
                        .child("userInfo").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        equal = false;

                        for (DataSnapshot dSnapshot : snapshot.getChildren()){
                            if (dSnapshot.getKey().equals("goodsName")){
                                if (String.valueOf(dSnapshot.getValue()).equals(goodsName)){
                                    equal = true;
                                }
                            }
                            else if (equal) {
                                if (dSnapshot.getKey().equals("shopName")) {
                                    chatShopNameArr.add(String.valueOf(dSnapshot.getValue()));
                                } else if (dSnapshot.getKey().equals("uid")) {
                                    chatUidArr.add(String.valueOf(dSnapshot.getValue()));
                                }
                            }
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

    //  굿즈 정보 가져오기
    private void getGoodsInfo(){
        DatabaseReference path = databaseReference.child("regiGoods").child(getUid).child(salesState)
                .child(ctgry).child(goodsName);

        path.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dSnapshot : snapshot.getChildren())
                {
                    if (dSnapshot.getKey().equals("price")){
                        price = Integer.parseInt(String.valueOf(dSnapshot.getValue()));
                    }
                    else if (dSnapshot.getKey().equals("regiImageUrl")){
                        for (int i=0; i<Integer.parseInt(picCount); i++){
                            getImgUrl.add(String.valueOf(dSnapshot.child(String.valueOf(i)).getValue()));
                        }
                        finish = true;
                    }
                    else if (dSnapshot.getKey().equals("post")){
                        post = Boolean.parseBoolean(String.valueOf(dSnapshot.getValue()));
                    }
                    else if (dSnapshot.getKey().equals("parcel")){
                        parcel = Boolean.parseBoolean(String.valueOf(dSnapshot.getValue()));
                    }
                    else if (dSnapshot.getKey().equals("directDealing")){
                        directDealing = Boolean.parseBoolean(String.valueOf(dSnapshot.getValue()));
                    }
                    else if (dSnapshot.getKey().equals("condition")){
                        condition = String.valueOf(dSnapshot.getValue());
                    }
                    else if (dSnapshot.getKey().equals("explain")){
                        explain = String.valueOf(dSnapshot.getValue());
                    }
                    else if (dSnapshot.getKey().equals("regiDate")){
                        regiDate = String.valueOf(dSnapshot.getValue());
                    }
                    else if (dSnapshot.getKey().equals("jjimCnt")){
                        jjimCnt = String.valueOf(dSnapshot.getValue());
                    }
                    else if (dSnapshot.getKey().equals("picCount")){
                        picCount = String.valueOf(dSnapshot.getValue());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.child("users").child(getUid).child("shopName").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shopName = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //  구매자의 fcm토큰 가져오기
    private void getOtherToken(String purchaserUid){
        databaseReference.child("users").child(purchaserUid)
                .child("fcmToken").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                otherToken = String.valueOf(snapshot.getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //  리얼타임디비에 구매내역 추가
    private void purchaseGoods(String purchaserUid) {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        regiDate = simpleDateFormat.format(date);

        PurchaseInfo pInfo = new PurchaseInfo();

        pInfo.setSellerUid(getUid);
        pInfo.setShopName(shopName);
        pInfo.setPrice(price);
        pInfo.setGoodsImgUrl(getImgUrl.get(0));
        pInfo.setPurchaseDate(regiDate);

       databaseReference.child("purchase").child(purchaserUid).child(ctgry).child(goodsName).setValue(pInfo);
       databaseReference.child("notifications").child(purchaserUid).child(ctgry).child(goodsName).setValue(pInfo);

       SendNotification.sendNotification(otherToken, "후기를 작성해주세요", shopName+"님과의 거래는 어떠셨나요?");
    }

    // 리얼타임 데베에서 등록된 상품 삭제하기
    private void deleteGoods(){

        DatabaseReference pathRegi = databaseReference.child("regiGoods").child(getUid).child(salesState)
                .child(ctgry).child(goodsName);

        isRegiSuccess = false;
        isUsersSuccess = false;

        // 선택한 상품 삭제
        pathRegi.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.getKey().equals("picCount")){
                    picCount = String.valueOf(snapshot.getValue());

                    for (int i=0; i<Integer.parseInt(picCount); i++){
                        getImgName = getUid + "_" + goodsName + i;
                        storageRef.child(getImgName).delete(); // 스토리지의 굿즈 이미지 삭제
                    }
                    pathRegi.removeValue();  //리얼타임 데베 값 삭제
                    isRegiSuccess = true;
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

        // 다른 사람들의 찜 목록에 있는 나의 삭제 상품 지우기
        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot usersSnapshot, @Nullable String previousChildName) {
                databaseReference.child("users").child(usersSnapshot.getKey()).child("jjim").child(getUid)
                        .child(ctgry).child(goodsName).removeValue();

                isUsersSuccess = true;
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
}

class MypageSalesData {
    public String goodsImgView = "";
    public String goodsNameTextView = "";
    public String statusTextView = "";
    public String goodsPriceTextView = "";
    public String dateTextView = "";
    public int moreImgView = 0;

    public String salesState = "";
    public String ctgry = "";
}
