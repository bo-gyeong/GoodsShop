package com.example.goodsshop;

import static android.widget.Toast.LENGTH_SHORT;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

public class Join extends AppCompatActivity {

    public FirebaseAuth mFirebaseAuth;     //파이어베이스 인증
    public DatabaseReference mDatabaseRef; //실시간 데이터베이스
    public EditText mEtShopName, mEtEmail, mEtPwd, mEtAgainPwd;
    public Button mBtnRegister;
    public TextView mTvCheckPwd, mTvWrongPwd;

    String token;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.join);

        getSupportActionBar().setTitle("회원가입");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//back 버튼

        mFirebaseAuth = FirebaseAuth.getInstance();

        mEtEmail = findViewById(R.id.emailEditText);
        mEtPwd = findViewById(R.id.pwEditText);
        mEtShopName = findViewById(R.id.shopNameEditText);
        mBtnRegister = findViewById(R.id.joinBtn);

        mEtAgainPwd = findViewById(R.id.pwAgainEditText);
        mTvCheckPwd = findViewById(R.id.checkPwd);
        mTvWrongPwd = findViewById(R.id.wrongPwd);

        getFCMToken();

        //비밀번호 확인
        mEtAgainPwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(mEtPwd.getText().toString().equals(mEtAgainPwd.getText().toString())) {
                    mTvCheckPwd.setVisibility(View.VISIBLE);
                    mTvWrongPwd.setVisibility(View.INVISIBLE);
                } else {
                    mTvCheckPwd.setVisibility(View.INVISIBLE);
                    mTvWrongPwd.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);

        //회원가입 클릭버튼 시작
        mBtnRegister.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //회원가입 처리 시작
                String strShopname = mEtShopName.getText().toString();
                String strEmail = mEtEmail.getText().toString();
                String strPwd = mEtPwd.getText().toString();
                int initialRBar = 3;

                DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference();


                //비밀번호확인과 회원가입 데이터 전송진행
                if(mEtPwd.getText().toString().equals(mEtAgainPwd.getText().toString()))
                {
                    // Firebase Auth 진행
                    mFirebaseAuth.createUserWithEmailAndPassword(strEmail, strPwd).addOnCompleteListener(Join.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (task.isSuccessful()) {
                                FirebaseUser firebaseUser = mFirebaseAuth.getCurrentUser();

                                UserInfo user = new UserInfo();
                                user.setShopName(strShopname);
                                user.setUid(firebaseUser.getUid());
                                user.setEmail(firebaseUser.getEmail());
                                user.setInitialRBar(initialRBar);
                                user.setFcmToken(token);

                                // setValue : database에 insert(삽입) 행위
                                mDatabaseRef.child("users").child(firebaseUser.getUid()).setValue(user);

                                Toast.makeText(Join.this, "회원가입에 성공하셨습니다.", LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                        startActivity(intent);
                                    }
                                }, 100);
                            }
                            else {
                                Toast.makeText(Join.this, "회원가입에 실패하였습니다", LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //비밀번호가 일치하지않을 때 회원가입 못하게 하기
                else
                {
                    Toast.makeText(Join.this, "회원가입에 실패하였습니다 비밀번호가 일치하지 않습니다.", LENGTH_SHORT).show();
                }

            }
        });

    }

    //  fcm 알림을 위한 토큰 받아오기
    private void getFCMToken(){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(!task.isSuccessful()){
                    return;
                }
                token = task.getResult();
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