package com.example.goodsshop;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Objects;
import java.util.regex.Pattern;

public class SetProfile extends AppCompatActivity {

    EditText nicknameEditText;
    Button completeBtn;
    ConstraintLayout setProfileImg;
    ImageView personImgView;

    Intent getShopIntent;
    Uri uri;
    String imgPath, token, email;
    private String shopName = null;
    private String profileImageUrl = null;
    private String uid = null;
    float rBarNum;

    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();
    SharedPreferences sharedPreferences;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_profile);

        getSupportActionBar().setTitle("나의샵 설정");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        sharedPreferences = getSharedPreferences("getShopImgUrl", MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("getShopImgName", MODE_PRIVATE);

        getShopIntent = getIntent();

        String getShopName = getShopIntent.getStringExtra("getShopName");
        nicknameEditText = findViewById(R.id.nicknameEditText);
        nicknameEditText.setText(getShopName);  //기존의 샵 이름 불러온 후 표시

        personImgView = findViewById(R.id.personImgView);
        String getShopImgUrl = getShopIntent.getStringExtra("getShopImgUrl");
        if (getShopImgUrl != null){ // 기존 이미지가 있었다면 원형크롭해서 띄우기
            Glide.with(this).load(getShopImgUrl).circleCrop().into(personImgView);
        } else {
            personImgView.setImageResource(R.drawable.ic_baseline_person_24);
        }

        rBarNum = getShopIntent.getFloatExtra("otherRBar", 3);
        email = null;

        getFCMToken();

        completeBtn = findViewById(R.id.completeBtn);
        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shopName = nicknameEditText.getText().toString();

                if (chkContents(shopName)){  //샵이름이 한 글자라도 입력되어있다면
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);

                    uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    getEmail();

                    try {
                        deleteFile();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if (uri != null){
                        uploadFile();
                    } else{
                        if (getShopImgUrl != null){
                            profileImageUrl = getShopImgUrl;
                        } else{
                            profileImageUrl = null;
                            sharedPreferences.edit().remove("getShopImgUrl").apply();
                        }

                        intent.putExtra("getShopImgUrl", (String)null);

                        writeNewUser();
                    }  //프로필 이미지 없으면 기존 이미지로, 그것도 없으면 sharedPreferences에 저장된 기존 url 삭제


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "완료되었습니다.", Toast.LENGTH_LONG).show();
                            finish();
                            startActivity(intent);
                        }
                    }, 500);
                }
                else{
                    Toast.makeText(getApplicationContext(), "영어, 한글, 숫자만 이용하여\n 샵 이름을 입력해 주세요.", Toast.LENGTH_LONG).show();
                }
            }
        });

        setProfileImg = findViewById(R.id.setProfileImg);
        setProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] items = new String[] {"갤러리로 이동", "기본프로필 이미지"};
                AlertDialog.Builder dlg = new AlertDialog.Builder(SetProfile.this);
                dlg.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                getGallery();
                                break;
                            case 1:
                                personImgView.setImageResource(R.drawable.ic_baseline_person_24);
                                break;
                        }
                    }
                });
                dlg.show();
            }
        }); // 프로필 이미지 클릭 시
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

    //샵 이름에 한글,숫자,영어 허용
    private boolean chkContents(String content){
        String pattern = "^[a-zA-Z0-9ㄱ-ㅎ가-힣]+$";
        boolean isOk = false;

        if (!content.trim().isEmpty() && Pattern.matches(pattern, content)){
            isOk = true;
        }

        return isOk;
    }

    private final int REQ_CODE_SELECT_IMAGE = 1000;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getData();

                if (uri != null){
                    getImageNameToUri(uri);

                    try {  //사진 가져와서 원형으로 자르고 프로필 안에 넣기
                        Glide.with(this).load(uri).circleCrop().into(personImgView);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void getGallery(){
        Intent intent;

        if (Build.VERSION.SDK_INT >= 19){
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        else{
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        }
        intent.setType("image/*");
        startActivityForResult(intent, REQ_CODE_SELECT_IMAGE);
    }  //갤러리 열어서 사진 선택

    private String getImageNameToUri(Uri data){
        String[] proj = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.ORIENTATION
        };

        Cursor cursor = this.getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        imgPath = cursor.getString(column_data);

        cursor.close();

        return imgPath;
    }  //이미지 경로 가져오기

    private void uploadFile() {
        final Uri file = Uri.fromFile(new File(getImageNameToUri(uri)));
        sharedPreferences.edit().putString("getShopImgName", file.getLastPathSegment()).apply();

        UploadTask uploadTask = FirebaseStorage.getInstance().getReference()
                .child("users/" + uid + "/profileImg/" + file.getLastPathSegment()).putFile(uri);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                final Task<Uri> imageUrl = taskSnapshot.getStorage().getDownloadUrl();
                while (!imageUrl.isComplete());
                profileImageUrl = Objects.requireNonNull(imageUrl.getResult()).toString();

                writeNewUser();
            }
        });
    } //파베 클라우드 스토리지에 업로드

    private void deleteFile(){
        String getShopImgName = sharedPreferences.getString("getShopImgName", null);
        sharedPreferences.edit().remove("getShopImgUrl").apply();

        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("users/" + uid + "/profileImg/" + getShopImgName);

        storageRef.delete();
    } //파베 클라우드 스토리지에 있는 프로필 이미지 삭제

    private void getEmail(){
        databaseReference.child("users").child(uid).child("email").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void writeNewUser(){
        UserInfo userInfo = new UserInfo();
        userInfo.shopName = shopName;
        userInfo.profileImageUrl = profileImageUrl;
        userInfo.uid = uid;
        userInfo.initialRBar = rBarNum;
        userInfo.fcmToken = token;

        if (email != null){
            userInfo.email = email;
        }

        databaseReference.child("users").child(uid).setValue(userInfo);
    } //유저 정보 가져와서 데베에 넣기

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
