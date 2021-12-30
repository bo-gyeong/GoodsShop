package com.example.goodsshop;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import retrofit2.http.Url;

//찜 관련 코드
public class ShowJjimGoods extends BaseAdapter {

    LayoutInflater layoutInflater = null;
    ArrayList<JjimGoodsData> showJjimGoodsArrListData = null;
    int count = 0;

    ArrayList<JjimGoodsInfo> showJjimGoodsInfoArrList = null;
    boolean click = true;
    String getMyUid, sellerUid, category, goodsName;
    int jjimCnt, price;
    ArrayList<String> jjimCntArr;

    public ShowJjimGoods(ArrayList<JjimGoodsData> showJjimGoodsArrList, ArrayList<JjimGoodsInfo> showJjimGoodsInfoArrList)
    {
        showJjimGoodsArrListData = showJjimGoodsArrList;
        this.showJjimGoodsInfoArrList = showJjimGoodsInfoArrList;
        count = showJjimGoodsArrList.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
        {
            Context context = parent.getContext();
            if (layoutInflater == null)
            {
                layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            convertView = layoutInflater.inflate(R.layout.show_jjim_goods, parent, false);
        }

        ImageView goodsImgView = convertView.findViewById(R.id.goodsImgView);
        TextView goodsTitleTextView = convertView.findViewById(R.id.goodsTitleTextView);
        TextView priceTextView = convertView.findViewById(R.id.priceTextView);
        ImageView jjimImgView = convertView.findViewById(R.id.jjimImgView);

        Glide.with(convertView).load(showJjimGoodsArrListData.get(position).goodsImgView).centerCrop().into(goodsImgView);
        goodsTitleTextView.setText(showJjimGoodsArrListData.get(position).goodsTitleTextView);
        priceTextView.setText(String.valueOf(showJjimGoodsArrListData.get(position).priceTextView));
        jjimImgView.setImageResource(showJjimGoodsArrListData.get(position).jjimImgView);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // 찜버튼 이미지 변환, 리얼타임 데베의 찜 카운팅 수 갱신
        jjimImgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMyUid = showJjimGoodsInfoArrList.get(position).getMyUid;
                jjimCntArr = showJjimGoodsInfoArrList.get(position).jjimCnt;

                sellerUid = showJjimGoodsInfoArrList.get(position).sellerUid.get(position);
                category = showJjimGoodsInfoArrList.get(position).category.get(position);
                goodsName = showJjimGoodsInfoArrList.get(position).goodsName.get(position);
                price = Integer.parseInt(showJjimGoodsInfoArrList.get(position).price.get(position));
                jjimCnt = Integer.parseInt(jjimCntArr.get(position));

                Intent intent = new Intent(v.getContext(), Jjim.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                DatabaseReference path = databaseReference.child("users").child(getMyUid).child("jjim")
                        .child(sellerUid).child(category).child(goodsName);

                if(click){
                    jjimCnt--;
                    if (price!=0){
                        changeJjimCnt(jjimCnt);
                    }

                    path.removeValue();

                    jjimCntArr.set(position, String.valueOf(jjimCnt));

                    jjimImgView.setImageResource(R.drawable.ic_baseline_favorite_border_24);
                    click=false;

                    v.getContext().startActivity(intent);
                }
            }
        });

        return convertView;
    }

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // 리얼타임데베에 있는 찜 카운팅 갱신
    private void changeJjimCnt(int intJjimCnt){
        DatabaseReference path = databaseReference.child("regiGoods").child(sellerUid).child("onSale").child(category).child(goodsName).child("jjimCnt");
        path.setValue(intJjimCnt);
    }
}

class JjimGoodsData {
    public String goodsImgView = "";
    public String goodsTitleTextView = "";
    public int priceTextView = 0;
    public int jjimImgView = 0;
}

class JjimGoodsInfo{
    public ArrayList<String> jjimCnt;
    public String getMyUid;
    public ArrayList<String>  sellerUid;
    public ArrayList<String>  category;
    public ArrayList<String>  goodsName;
    public ArrayList<String>  price;
    public ArrayList<ArrayList<String>>  imgUrlArrList;
    public ArrayList<Integer>  picCount;
}

