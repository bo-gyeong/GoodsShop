package com.example.goodsshop;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//판매내역관련 코드
public class ShowOtherSaleList extends BaseAdapter {

    LayoutInflater layoutInflater = null;
    ArrayList<OtherSalesData> showSaleArrListData = null;
    int count = 0;
    boolean click;

    public ShowOtherSaleList(ArrayList<OtherSalesData> salesArrList)
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
            convertView = layoutInflater.inflate(R.layout.show_other_sale_list, parent, false);
        }

        ImageView goodsImgView = convertView.findViewById(R.id.goodsImgView);
        TextView goodsNameTextView = convertView.findViewById(R.id.goodsNameTextView);
        TextView statusTextView = convertView.findViewById(R.id.statusTextView);
        TextView goodsPriceTextView = convertView.findViewById(R.id.goodsPriceTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

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

        return convertView;
    }
}

class OtherSalesData {
    public String goodsImgView = "";
    public String goodsNameTextView = "";
    public String statusTextView = "";
    public String goodsPriceTextView = "";
    public String dateTextView = "";
}