package com.example.goodsshop;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;


//구매내역관련 코드
public class ShowPurchaseList extends BaseAdapter {

    LayoutInflater layoutInflater = null;
    ArrayList<PurchaseData> showPurchaseArrListData = null;
    int count = 0;

    public ShowPurchaseList(ArrayList<PurchaseData> purchaseArrList)
    {
        showPurchaseArrListData =  purchaseArrList;
        count =  purchaseArrList.size();
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
            convertView = layoutInflater.inflate(R.layout.show_purchase, parent, false);
        }

        ImageView goodsImgView = convertView.findViewById(R.id.goodsImgView);
        TextView goodsNameTextView = convertView.findViewById(R.id.goodsNameTextView);
        TextView goodsPriceTextView = convertView.findViewById(R.id.goodsPriceTextView);
        TextView shopNameTextView = convertView.findViewById(R.id.shopNameTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        Glide.with(convertView).load(showPurchaseArrListData.get(position).goodsImgView).centerCrop().into(goodsImgView);
        goodsNameTextView.setText(showPurchaseArrListData.get(position).goodsNameTextView);
        goodsPriceTextView.setText(showPurchaseArrListData.get(position).goodsPriceTextView);
        shopNameTextView.setText(showPurchaseArrListData.get(position).shopNameTextView);
        dateTextView.setText(showPurchaseArrListData.get(position).dateTextView);

        return convertView;
    }
}

class PurchaseData {
    public String goodsImgView = "";
    public String goodsNameTextView = "";
    public String goodsPriceTextView = "";
    public String shopNameTextView="";
    public String dateTextView = "";
}
