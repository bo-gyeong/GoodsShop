package com.example.goodsshop;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ReportFragment;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MypageLogin extends Fragment {

    View v;
    ProgressBar myPageProgressBar;
    TextView joinTextView;
    TextView findPwTextView;
    Button loginGoogleBtn, loginTwitterBtn, loginFacebookBtn;

    SharedPreferences sharedPreferences;
    boolean isUser;

    FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabaseRef;
    EditText mEtEmail, mEtPwd;
    Button btn_login;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_mypage_login, container, false);

        sharedPreferences = v.getContext().getSharedPreferences("getUid", MODE_PRIVATE);
        sharedPreferences = v.getContext().getSharedPreferences("getShopName", MODE_PRIVATE);
        sharedPreferences = v.getContext().getSharedPreferences("getShopImg", MODE_PRIVATE);

        myPageProgressBar = v.findViewById(R.id.myPageProgressBar);
        myPageProgressBar.bringToFront();

        List<AuthUI.IdpConfig> selectedProviders = new ArrayList<>();

        loginGoogleBtn = v.findViewById(R.id.loginGoogleBtn);
        loginGoogleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProviders.clear();
                selectedProviders.add(new AuthUI.IdpConfig.GoogleBuilder().build());

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(selectedProviders)
                        .setDefaultProvider(new AuthUI.IdpConfig.GoogleBuilder().build())
                        .build(), RC_SIGN_IN);
            }
        });  //구글 로그인 버튼 클릭 시

        loginTwitterBtn = v.findViewById(R.id.loginTwitterBtn);
        loginTwitterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProviders.clear();
                selectedProviders.add(new AuthUI.IdpConfig.TwitterBuilder().build());

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(selectedProviders)
                        .setDefaultProvider(new AuthUI.IdpConfig.TwitterBuilder().build()).setIsSmartLockEnabled(true)
                        .build(), RC_SIGN_IN);
            }
        });  //트위터 로그인 버튼 클릭 시

        loginFacebookBtn = v.findViewById(R.id.loginFacebookBtn);
        loginFacebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProviders.clear();
                selectedProviders.add(new AuthUI.IdpConfig.FacebookBuilder().build());

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(selectedProviders)
                        .setDefaultProvider(new AuthUI.IdpConfig.FacebookBuilder().build()).setIsSmartLockEnabled(false)
                        .build(), RC_SIGN_IN);
            }
        });  //페이스북 로그인 버튼 클릭 시

        joinTextView = v.findViewById(R.id.joinTextView);
        joinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Join.class);
                startActivity(intent);
            }
        });  //회원가입 버튼 클릭 시


        //이메일입력 로그인하기기
        /*mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        mEtEmail = v.findViewById(R.id.emailEditText);
        mEtPwd = v.findViewById(R.id.pwEditText);*/

        btn_login = v.findViewById(R.id.loginEmailBtn);
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedProviders.clear();
                selectedProviders.add(new AuthUI.IdpConfig.EmailBuilder().build());

                startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(selectedProviders)
                        .setDefaultProvider(new AuthUI.IdpConfig.EmailBuilder().build()).setIsSmartLockEnabled(false)
                        .build(), RC_SIGN_IN);
            }

        /*btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //로그인 요청
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();

                mFirebaseAuth.signInWithEmailAndPassword(strEmail,strPwd).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            //로그인 성공
                            selectedProviders.clear();
                            selectedProviders.add(new AuthUI.IdpConfig.EmailBuilder().build());

                            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                                    .setAvailableProviders(selectedProviders)
                                    .setDefaultProvider(new AuthUI.IdpConfig.EmailBuilder().build()).setIsSmartLockEnabled(true)
                                    .build(), RC_SIGN_IN);
                        }
                        else{
                            Toast.makeText(getActivity(),"로그인 실패",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }*/
        });

        return v;
    }

    private static final int RC_SIGN_IN = 1000;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    // 로그인 되어있는지 확인
    public boolean chkUser(){
//        ((MainActivity) MainActivity.context).checkLogin();  //싱글톤 패턴: mainActivity 재사용하여 메모리 낭비 방지
//
//        String getUid = sharedPreferences.getString("getUid", null); //MainActivity.sharedUid();//
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if(getUid != null) {
//                    isUser = true;
//                }
//            }
//        }, 500);  //500ms로 해놨는데 사용자 늘어났을 때 화면전환 제대로 안되면 시간 늘려보기. 그래도 안되면 스레드? 밑에꺼 시간>이 시간









//        if(user != null) {
//            ((MainActivity) MainActivity.context).checkLogin();
//            String uid = user.getUid();
//            sharedPreferences.edit().putString("getUid", uid).apply();
//            isUser = true;
//            System.out.println("!!!!!!!!!!!!???????????????????");
//        }






        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {
            ((MainActivity) MainActivity.context).checkLogin();  //싱글톤 패턴: mainActivity 재사용하여 메모리 낭비 방지
            String uid = user.getUid();

            databaseReference.child("users").child(uid).child("uid").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String userUid = snapshot.getValue(String.class);
                    if (userUid != null){   //데베에 등록된 유저라면 데베에서 정보 가져옴
                        isUser = true;

                        sharedPreferences.edit().putString("getUid", uid).apply();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        return isUser;
    }

    String whatFragment = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if(resultCode == RESULT_OK){
                myPageProgressBar.setVisibility(View.VISIBLE);  //프로그레스바 보이기
                chkUser();

                Bundle bundle = getArguments();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isUser){  //유저라면 화면 전환
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                            if(bundle != null){
                                whatFragment = bundle.getString("whatFragment");

                                if (whatFragment.equals("Chatting")){
                                    Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                                            .beginTransaction().replace(R.id.loginCLayout, new Chatting()).commit();
                                }
                                else if (whatFragment.equals("Mypage")){
                                    Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                                            .beginTransaction().replace(R.id.loginCLayout, new Mypage()).commit();
                                }
                                else if (whatFragment.equals("Regi")){
                                    startActivity(intent);
                                }
                                else{
                                    startActivity(intent);
                                }
                            }else{
                                Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                                        .beginTransaction().replace(R.id.loginCLayout, new Mypage()).commit();
                            }

                            myPageProgressBar.setVisibility(View.GONE);  //프로그레스바 없애기
                        }
                        else{  //유저가 아니라면 프로필 등록페이지로 이동
                            myPageProgressBar.setVisibility(View.GONE);

                            Intent intent = new Intent(getActivity(), SetProfile.class);
                            startActivity(intent);
                        }
                    }
                },2000); //2000ms로 해놨는데 사용자 늘어났을 때 화면전환 제대로 안되면 시간 늘려보기. 그래도 안되면 스레드?

            }
        }
    }

}