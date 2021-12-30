package com.example.goodsshop;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

//이 프로젝트의 전체적인 동작을 담당
public class MainActivity extends AppCompatActivity {

    BottomNavigationView navView;

    public static MainActivity context;
    public static SharedPreferences sharedPreferences;
    Intent getShopIntent, showMpListIntent;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    boolean isUser;

    public static String getUid, goodsName, picCount, ctgry, price, condition, explain, jjimCnt, salesState;
    public static ArrayList<String> imgUrl;
    public static boolean post, parcel, directDealing;

    public static String sharedUid() {
        getUid = sharedPreferences.getString("getUid", null);

        return getUid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);

        context = this;

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_registration, R.id.navigation_category, R.id.navigation_home,
                R.id.navigation_chatting, R.id.navigation_mypage)
                .build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        sharedPreferences = getSharedPreferences("getUid", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("getShopName", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("getShopImgUrl", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("getRBarScore", MODE_PRIVATE);

        getShopIntent = getIntent();
        String getShopImgUrl = getShopIntent.getStringExtra("getShopImgUrl");
        sharedPreferences.edit().putString("getShopImgUrl", getShopImgUrl).apply();

        checkLogin();

        showMpListIntent = getIntent();
        if (showMpListIntent.getBooleanExtra("showMypageList", false)){
            goodsName = showMpListIntent.getStringExtra("goodsName");
            ctgry = showMpListIntent.getStringExtra("category");
            price = String.valueOf(showMpListIntent.getIntExtra("price", 0));
            post = showMpListIntent.getBooleanExtra("post", false);
            parcel = showMpListIntent.getBooleanExtra("parcel", false);
            directDealing = showMpListIntent.getBooleanExtra("directDealing", false);
            condition = showMpListIntent.getStringExtra("condition");
            explain = showMpListIntent.getStringExtra("explain");
            picCount = showMpListIntent.getStringExtra("picCount");
            imgUrl = showMpListIntent.getStringArrayListExtra("imgUrlArrList");
            jjimCnt = showMpListIntent.getStringExtra("jjimCnt");
            salesState = showMpListIntent.getStringExtra("salesState");

            navView.setSelectedItemId(R.id.navigation_registration);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goodsName = null;
                }
            }, 1000);
        }
    }

    public void checkLogin(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            String uid = user.getUid();

            databaseReference.child("users").child(uid).child("uid").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userUid = snapshot.getValue(String.class);
                    if (userUid != null){   //데베에 등록된 유저라면 데베에서 정보 가져옴
                        isUser = true;

                        sharedPreferences.edit().putString("getUid", uid).apply();

                        getProfileImg(userUid);
                        getShopName(userUid);
                        getRBarScore(userUid);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // 데베에 저장된 샵 이름 가져오기
    public void getShopName(String uid){

        if (isUser) {
            databaseReference.child("users").child(uid).child("shopName").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String shopName = snapshot.getValue(String.class);

                    sharedPreferences.edit().putString("getShopName", shopName).apply();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // 데베에 저장된 프사 링크 불러오기
    public void getProfileImg(String uid){

        if (isUser) {
            databaseReference.child("users").child(uid).child("profileImageUrl").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String profileImageUrl = snapshot.getValue(String.class);

                    if (profileImageUrl != null){  // 프로필 사진을 등록했다면 링크 저장
                        sharedPreferences.edit().putString("getShopImgUrl", profileImageUrl).apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // 데베에 저장된 별점 가져오기
    public void getRBarScore(String uid){
        if (isUser) {
            databaseReference.child("users").child(uid).child("initialRBar").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        float rBarScore = snapshot.getValue(float.class);

                        sharedPreferences.edit().putString("getRBarScore", String.valueOf(rBarScore)).apply();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    // 상품등록 페이지에서 완료 누르면 home으로 감
    @Override
    public void onBackPressed() {
        if (navView.getSelectedItemId() == R.id.navigation_registration) {
            navView.setSelectedItemId(R.id.navigation_home);
        }
        else {
            super.onBackPressed();
        }
    }
}