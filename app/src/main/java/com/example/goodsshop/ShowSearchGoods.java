package com.example.goodsshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

// 상품 검색 또는 카테고리 개별 클릭 시 띄우는 리사이클러뷰 관련 코드 & 아이템 클릭 시 이벤트 주는 코드 (AfterSearch.class)
public class ShowSearchGoods extends RecyclerView.Adapter<ShowSearchGoods.RViewHolder> {

    ArrayList<SearchGoodsData> searchGoodsItemList;
    Context context;
    LayoutInflater layoutInflater;
    OnRItemClickListener onRItemClickListener;

    public ShowSearchGoods(ArrayList<SearchGoodsData> searchGoodsItemList, Context context) {
        this.searchGoodsItemList = searchGoodsItemList;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_search_goods, parent, false);

        return new RViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RViewHolder holder, int position) {
        Glide.with(context).load(searchGoodsItemList.get(position).getGoodsImgView()).centerCrop().into(holder.goodsImgView);
        holder.goodsTitleTextView.setText(searchGoodsItemList.get(position).getGoodsTitleTextView());
        holder.priceTextView.setText(searchGoodsItemList.get(position).getPriceTextView());

        if (searchGoodsItemList.get(position).getPriceTextView().equals("")){
            holder.priceWonTextView.setVisibility(View.GONE);
        }
        else{
            holder.priceWonTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return searchGoodsItemList.size();
    }

    public interface OnRItemClickListener {
        void onRItemClickListener(View v, int position);
    }

    public void setOnRItemClickListener(OnRItemClickListener onRItemClickListener){
        this.onRItemClickListener = onRItemClickListener;
    }

    class RViewHolder extends RecyclerView.ViewHolder{

        ImageView goodsImgView;
        TextView goodsTitleTextView, priceTextView, priceWonTextView;

        public RViewHolder(@NonNull View itemView) {
            super(itemView);

            goodsImgView = itemView.findViewById(R.id.goodsImgView);
            goodsTitleTextView = itemView.findViewById(R.id.goodsTitleTextView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            priceWonTextView = itemView.findViewById(R.id.priceWonTextView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getBindingAdapterPosition();

                    if (position != RecyclerView.NO_POSITION)
                    {
                        onRItemClickListener.onRItemClickListener(v, position);
                    }

                }
            });
        }
    }
}
