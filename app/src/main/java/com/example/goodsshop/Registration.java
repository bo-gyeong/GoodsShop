package com.example.goodsshop;

import static android.content.Context.INPUT_METHOD_SERVICE;

import static com.example.goodsshop.MainActivity.context;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.CompoundButtonCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Html;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.Permission;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class Registration extends Fragment{

    MainActivity mainActivity;

    View v;
    ActionBar actionBar;
    ConstraintLayout addGoodsImgIcon;
    TextView picCountTV, categoryTV;
    ImageView[] getGalleryImgView;
    EditText goodsNameEdtTxt, priceEdtTxt, explainEdtTxt;
    CheckBox postChkBox, parcelChkBox, directDealingChkBox;
    RadioGroup conditionRdoGroup;
    RadioButton highRdoBtn, midRdoBtn, lowRdoBtn;

    String uid, beforeGoodsName, beforeCtgry, beforeSalesState;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference databaseReference = firebaseDatabase.getReference();

    private static final int SINGLE_PERMISSION = 1000;

    // 액션바를 위한 프래그먼트 생명주기
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) getActivity();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mainActivity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        actionBar.setTitle("");
        actionBar.hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_registration, container, false);

        //액션바 관련 코드
        actionBar = ((MainActivity) Objects.requireNonNull(getActivity())).getSupportActionBar();
        actionBar.setTitle("상품 등록");
        setHasOptionsMenu(true);

        checkUid();

        addGoodsImgIcon = v.findViewById(R.id.addGoodsImgIcon);
        addGoodsImgIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGallery();
            }
        });

        getGalleryImgView = new ImageView[10];
        int[] getGalleryImgViewId = {R.id.getGalleryImgView1, R.id.getGalleryImgView2, R.id.getGalleryImgView3
                , R.id.getGalleryImgView4, R.id.getGalleryImgView5, R.id.getGalleryImgView6, R.id.getGalleryImgView7
                , R.id.getGalleryImgView8, R.id.getGalleryImgView9, R.id.getGalleryImgView10};

        for (int i=0; i<10; i++){
            getGalleryImgView[i] = v.findViewById(getGalleryImgViewId[i]);
        }

        picCountTV = v.findViewById(R.id.picCountTextView);
        goodsNameEdtTxt = v.findViewById(R.id.goodsNameEditText);

        categoryTV = v.findViewById(R.id.categoryTextView);
        categoryTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String[] categoryItems = new String[] {"앨범", "포토카드", "콜렉트북", "스티커", "인형", "그립톡"
                , "응원봉", "슬로건", "뱃지", "엽서", "키링", "가방", "부채", "생일카페", "액세서리", "옷"
                , "신발", "텀블러", "기타 공식굿즈", "기타 비공식굿즈"};
                AlertDialog.Builder dlg = new AlertDialog.Builder(getActivity());
                dlg.setItems(categoryItems, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        categoryTV.setText(categoryItems[which]);
                    }
                });
                dlg.show();
            }
        });  //카테고리 클릭 시 나오는 대화상자

        priceEdtTxt = v.findViewById(R.id.priceEditText);

        postChkBox = v.findViewById(R.id.postCheckBox);
        getColor(postChkBox);

        parcelChkBox = v.findViewById(R.id.parcelCheckBox);
        getColor(parcelChkBox);

        directDealingChkBox = v.findViewById(R.id.directDealingCheckBox);
        getColor(directDealingChkBox);

        conditionRdoGroup = v.findViewById(R.id.conditionRadioGroup);

        highRdoBtn = v.findViewById(R.id.highRadioButton);
        getColor(highRdoBtn);

        midRdoBtn = v.findViewById(R.id.midRadioButton);
        getColor(midRdoBtn);

        lowRdoBtn = v.findViewById(R.id.lowRadioButton);
        getColor(lowRdoBtn);

        explainEdtTxt = v.findViewById(R.id.explainEditText);

        beforeGoodsName = "";
        // ShowMyPageList에서 수정을 클릭했을 경우 값들 불러와서 지정
        if (MainActivity.goodsName != null){

            // 저장소 접근 권한 허용이 안되어있으면 권한 요청 창 띄우기
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SINGLE_PERMISSION);
            }

            beforeGoodsName = MainActivity.goodsName;
            goodsNameEdtTxt.setText(beforeGoodsName);
            picCount = Integer.parseInt(MainActivity.picCount);

            for (int i=0; i<picCount; i++){
                uri[i] = Uri.parse(MainActivity.imgUrl.get(i));

                try {  //사진 가져와서 크기에 맞게 자르고 띄우기
                    Glide.with(this).load(uri[i]).centerCrop().into(getGalleryImgView[i]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                getGalleryImgView[i].setVisibility(View.VISIBLE);
            }
            picCountTV.setText(MainActivity.picCount);

            beforeCtgry = MainActivity.ctgry;
            categoryTV.setText(beforeCtgry);
            priceEdtTxt.setText(MainActivity.price);
            if(MainActivity.condition.equals("상")){
                highRdoBtn.setChecked(true);
            }
            else if (MainActivity.condition.equals("중")){
                midRdoBtn.setChecked(true);
            }
            else{
                lowRdoBtn.setChecked(true);
            }

            postChkBox.setChecked(false);
            parcelChkBox.setChecked(false);
            directDealingChkBox.setChecked(false);

            if (MainActivity.post){
                postChkBox.setChecked(true);
            }
            if (MainActivity.parcel){
                parcelChkBox.setChecked(true);
            }
            if (MainActivity.directDealing){
                directDealingChkBox.setChecked(true);
            }
            explainEdtTxt.setText(MainActivity.explain);
            jjimCnt = Integer.parseInt(MainActivity.jjimCnt);
            beforeSalesState = MainActivity.salesState;
        }

        return v;
    }

    // 로그인 되었는지 확인
    private void checkUid(){
        uid = MainActivity.sharedUid();

        if(uid == null) {  //uid가 없다면(로그인 상태가 아니라면) 로그인 화면으로 이동
            MypageLogin mypageLogin = new MypageLogin();

            Bundle bundle = new Bundle();
            bundle.putString("whatFragment", "Regi");
            mypageLogin.setArguments(bundle);

            Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                    .beginTransaction().replace(R.id.regiCLayout, mypageLogin).commit();
        }
        else{
            actionBar.show();
        }  //uid 있으면 액션바 보여주기
    }

    private final int REQ_CODE_SELECT_IMAGE = 1000;
    int picCount = 0;
    Uri[] uri = new Uri[10];

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                ClipData clipData = data.getClipData();

                for (int i=0; i<10; i++){
                    getGalleryImgView[i].setImageResource(0);
                    getGalleryImgView[i].setVisibility(View.GONE);
                }  //초기화

                if (clipData != null){
                    if (clipData.getItemCount() > 10) {
                        Toast.makeText(getContext(), "최대 10장까지 선택가능합니다.", Toast.LENGTH_LONG).show();
                    }
                    else if (clipData.getItemCount() < 3) {
                        Toast.makeText(getContext(), "최소 3장의 사진을 선택해주세요", Toast.LENGTH_LONG).show();
                    }
                    else {
                        uri = new Uri[10];

                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            uri[i] = clipData.getItemAt(i).getUri();

                            try {  //사진 가져와서 크기에 맞게 자르고 띄우기
                                Glide.with(this).load(uri[i]).centerCrop().into(getGalleryImgView[i]);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            getGalleryImgView[i].setVisibility(View.VISIBLE);
                        }
                        picCountTV.setText(String.valueOf(clipData.getItemCount()));
                        picCount = Integer.parseInt(picCountTV.getText().toString());
                    }
                }
            }
        }
    }

    private void getGallery(){
        Intent intent;

        if (Build.VERSION.SDK_INT >= 19){
            intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        }
        else{
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            //API 18이하에서 사진 여러장 불러오는 코드,,,,찾기
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

        Cursor cursor = v.getContext().getContentResolver().query(data, proj, null, null, null);
        cursor.moveToFirst();

        int column_data = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        String imgPath = cursor.getString(column_data);

        cursor.close();

        return imgPath;
    }  //이미지 경로 가져오기

    private ArrayList<String> regiImageUrl;
    Uri imgUri = null;

    // 스토리지&리얼타임 데베에 상품 올리기
    private void uploadFile() {
        regiImageUrl = new ArrayList<>();

        for (int i=0; i<picCount; i++){
            int finalI = i;
            String filename = uid + "_" + goodsName + i;

            String[] tmp = String.valueOf(uri[i]).split(":");

            if (tmp[0].equals("https")){
                StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                        .child("regiGoods").child(uid + "_" + beforeGoodsName + i);

                storageRef.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                    @Override
                    public void onSuccess(StorageMetadata storageMetadata) {
                        imgUri = Uri.parse(storageMetadata.getCustomMetadata("path"));

                        File file = new File(String.valueOf(imgUri));

                        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg")  //.setCustomMetadata("uri", String.valueOf(imgUri))
                                .setCustomMetadata("path", String.valueOf(imgUri)).build();

                        try{
                            UploadTask uploadTask = FirebaseStorage.getInstance().getReference()
                                    .child("regiGoods/" + filename).putFile(Uri.parse("file://" + file.getAbsolutePath()), metadata);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    final Task<Uri> imageUrl = taskSnapshot.getStorage().getDownloadUrl();
                                    while (!imageUrl.isComplete());

                                    imageUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            regiImageUrl.add(Objects.requireNonNull(imageUrl.getResult()).toString());
                                        }
                                    });

                                    if (finalI == picCount-1){
                                        if (!beforeGoodsName.equals(goodsName)){  //이전 상품과 이름이 같지 않으면 디비에서 지우기
                                            deleteGoods();
                                        }

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                regiNewGoods();
                                            }
                                        }, 3000);
                                    }
                                }
                            });
                        }catch (Exception e){  //저장소 접근 권한이 거부 된 경우
                            AlertDialog.Builder dlg = new AlertDialog.Builder(v.getContext());
                            dlg.setTitle("앱 권한");
                            dlg.setMessage("상품이 등록되지 않았습니다. 상품 등록 관련 기능을 이용하시려면 애플리케이션 정보>권한 에서 모든 권한을 허용해 주세요");
                            dlg.setPositiveButton("확인", null);
                            dlg.show();
                        }
                    }
                });

            } else{
                StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg")
                        .setCustomMetadata("path", getImageNameToUri(uri[i])).build();

                UploadTask uploadTask = FirebaseStorage.getInstance().getReference()
                        .child("regiGoods/" + filename).putFile(uri[i], metadata);

                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final Task<Uri> imageUrl = taskSnapshot.getStorage().getDownloadUrl();
                        while (!imageUrl.isComplete());

                        regiImageUrl.add(Objects.requireNonNull(imageUrl.getResult()).toString());

                        if (finalI == picCount-1){
                            if (!beforeGoodsName.equals("")){  //수정을 한거라면 이전 상품 지우기
                                if (!beforeGoodsName.equals(goodsName)){  //이전 상품과 이름이 같지 않으면 디비에서 지우기
                                    deleteGoods();
                                }
                            }

                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    regiNewGoods();
                                }
                            }, 2500);
                        }
                    }
                });
            }
        }

    } //파베 클라우드 스토리지에 업로드(같은 굿즈상품명으로 여러개의 상품 등록 시 안에 사진만 바뀜)

    private String goodsName;
    private String category;
    private int price;
    private boolean post, parcel, directDealing;
    private String condition;
    private String explain;
    private String regiDate;
    private int jjimCnt = 0;

    //액션바 동작 코드
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.actionbar_registration, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.completeBtn:
                goodsName = goodsNameEdtTxt.getText().toString();
                category = categoryTV.getText().toString();
                price = 0;
                post = postChkBox.isChecked();
                parcel = parcelChkBox.isChecked();
                directDealing = directDealingChkBox.isChecked();
                int getChkRdoId = conditionRdoGroup.getCheckedRadioButtonId();
                RadioButton rdoBtn = v.findViewById(getChkRdoId);
                condition = rdoBtn.getText().toString();
                explain = explainEdtTxt.getText().toString();

                Date date = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                regiDate = simpleDateFormat.format(date);

                String priceTemp = priceEdtTxt.getText().toString();
                if (chkContents(priceTemp)){
                    price = Integer.parseInt(priceEdtTxt.getText().toString());

                    //9999같은 연속된 숫자로 판매할 경우(시간나면 추가)
                }

                if (!chkContents(goodsName) || category.equals("카테고리") || price==0 || !chkContents(condition) || !chkContents(explain)){
                    Toast.makeText(getContext(), "빈 칸을 모두 채워주세요", Toast.LENGTH_SHORT).show();
                }
                else if (!post && !parcel && !directDealing){
                    Toast.makeText(getContext(), "배송방법을 선택해주세요", Toast.LENGTH_SHORT).show();
                }
                else {
                    if (uri != null){  // 입력에 문제가 없고 사진도 첨부되어있으면 스토리지에 저장
                        uploadFile();

                        InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(explainEdtTxt.getWindowToken(), 0);

                        mainActivity.onBackPressed();
                        actionBar.hide();
                    } else{
                        Toast.makeText(getContext(), "최소 3장의 사진을 첨부해주세요", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // 입력한 값이 공백인지 확인
    private boolean chkContents(String content){
        boolean isOk = false;

        if (!content.trim().isEmpty()){
            isOk = true;
        }

        return isOk;
    }

    private void regiNewGoods(){
        RegiGoods regiGoods = new RegiGoods();

        //regiGoods.setGoodsName(goodsName);
        regiGoods.setRegiImageUrl(regiImageUrl);
        regiGoods.setPicCount(picCount);
        //regiGoods.setCategory(category);
        regiGoods.setPrice(price);
        regiGoods.setPost(post);
        regiGoods.setParcel(parcel);
        regiGoods.setDirectDealing(directDealing);
        regiGoods.setCondition(condition);
        regiGoods.setExplain(explain);
        regiGoods.setRegiDate(regiDate);
        regiGoods.setJjimCnt(jjimCnt);

        databaseReference.child("regiGoods").child(uid).child("onSale").child(category)
                .child(goodsName).setValue(regiGoods).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(v.getContext(), "등록이 완료되었습니다", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
    } //굿즈 정보 가져와서 데베에 넣기

    // 리얼타임 데베에서 등록된 상품 삭제하기
    private void deleteGoods(){

        DatabaseReference pathRegi = databaseReference.child("regiGoods").child(uid).child(beforeSalesState)
                .child(beforeCtgry).child(beforeGoodsName);

        // 선택한 상품 삭제
        pathRegi.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if (snapshot.getKey().equals("picCount")){
                    String picCnt = String.valueOf(snapshot.getValue());

                    for (int i=0; i<Integer.parseInt(picCnt); i++){
                        String getImgName = uid + "_" + beforeGoodsName + i;
                        FirebaseStorage.getInstance().getReference().child("regiGoods").child(getImgName).delete(); // 스토리지의 굿즈 이미지 삭제
                    }
                    pathRegi.removeValue();  //리얼타임 데베 값 삭제
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

        // 다른 사람들의 찜 목록에 있는 나의 수정된 상품 지우기
        databaseReference.child("users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot usersSnapshot, @Nullable String previousChildName) {
                databaseReference.child("users").child(usersSnapshot.getKey()).child("jjim").child(uid)
                        .child(beforeCtgry).child(beforeGoodsName).removeValue();
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

    //체크박스, 라디오버튼 색상 변경
    public void getColor(CompoundButton compoundBtn){
        if (Build.VERSION.SDK_INT < 21) {
            CompoundButtonCompat.setButtonTintList(compoundBtn, ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));//Use android.support.v4.widget.CompoundButtonCompat when necessary else
        } else {
            compoundBtn.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimaryLight)));//setButtonTintList is accessible directly on API>19
        }
    }
}