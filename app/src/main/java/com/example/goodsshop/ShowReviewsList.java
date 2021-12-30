package com.example.goodsshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.constraintlayout.solver.widgets.ConstraintAnchor;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//리뷰관련 코드
public class ShowReviewsList extends BaseAdapter {

    LayoutInflater layoutInflater = null;
    ArrayList<ListData> rvArrListData = null;
    int count = 0;

    public ShowReviewsList(ArrayList<ListData> rvArrList)
    {
        rvArrListData = rvArrList;
        count = rvArrList.size();
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
            convertView = layoutInflater.inflate(R.layout.reviews_listview, parent, false);
        }

        ConstraintLayout rvImgCLayout = convertView.findViewById(R.id.rvImgCLayout);
        ImageView goodsImgView = convertView.findViewById(R.id.goodsImgView);
        TextView shopNameTextView = convertView.findViewById(R.id.shopNameTextView);
        RatingBar evaluateRBar = convertView.findViewById(R.id.evaluateRBar);
        TextView goodsContentTextView = convertView.findViewById(R.id.goodsContentTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);

        if (rvArrListData.get(position).goodsImgView.contains("https")){
            goodsImgView.setVisibility(View.VISIBLE);
            rvImgCLayout.setPadding(0,0,40,0);
            Glide.with(convertView).load(rvArrListData.get(position).goodsImgView).centerCrop().into(goodsImgView);
        } else{
            goodsImgView.setVisibility(View.GONE);
            rvImgCLayout.setPadding(0,0,0,0);
        }

        shopNameTextView.setText(rvArrListData.get(position).shopNameTextView);
        evaluateRBar.setRating(rvArrListData.get(position).evaluateRBar);
        goodsContentTextView.setText(rvArrListData.get(position).goodsContentTextView);
        dateTextView.setText(rvArrListData.get(position).dateTextView);

        return convertView;
    }
}

class ListData {
    public String goodsImgView = "";
    public String shopNameTextView = "";
    public float evaluateRBar = 0;
    public String goodsContentTextView = "";
    public String dateTextView = "";
}

