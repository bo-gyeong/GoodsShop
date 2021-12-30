package com.example.goodsshop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

//채팅대기방 관련 리사이클러뷰 코드
class ShowChatting extends RecyclerView.Adapter<ShowChatting.ChatRViewHolder>{
    List<ChattingUserModel> userModels;
    Context context;
    LayoutInflater layoutInflater;

    public ShowChatting(List<ChattingUserModel> userModels, Context context) {
        this.userModels = userModels;
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    // firebase db에 접속하기 위한 생성자
//    public ShowChattingList(){
//
//        // 친구목록을 쌓아주는 arraylist
//        userModels = new ArrayList<>();
//        final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
//
//        // db를 검색(읽어들이는)하는 부분
//        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) { // 서버에서 넘어오는 데이터들이며, snapshot이란 파라미터로 넘어옴
//                // 친구목록을 초기화 해주고 작업을 시작하는 부분. >> 누적 데이터를 초기화시켜줌
//                userModels.clear();
//
//                for (DataSnapshot shot : snapshot.getChildren()){
//                    ChattingUserModel chattingUserModel = shot.getValue(ChattingUserModel.class);
//
//                    if (chattingUserModel.uid.equals(myUid)){
//                        continue;
//                    }
//                    userModels.add(chattingUserModel);
//                }
//                // 새로고침을 해주는 코드
//                notifyDataSetChanged();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//    }

    @NonNull
    @Override
    // itemView를 넣어준다.
    public ChatRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_chatting_list,parent,false);

        return new ChatRViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatRViewHolder holder, @SuppressLint("RecyclerView") int position) {

        // 이미지 넣어주는 부분
        if (userModels.get(position).profileImageUrl == null){
            userModels.get(position).profileImageUrl = String.valueOf(R.drawable.ic_baseline_person_24);
        }
        else{
            Glide.with(holder.itemView.getContext())
                    .load(userModels.get(position).profileImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into((holder).imageView);
        }

        // 텍스트 넣어주는 부분
        holder.textView.setText(userModels.get(position).userName);
        holder.textView_comment.setText("마지막 채팅~~");
        holder.dateTextView.setText("2021-10-00");
        holder.chattingNewCLayout.setVisibility(View.VISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChattingRoom.class);
                intent.putExtra("getSellerUid", userModels.get(position).uid);
                intent.putExtra("sellerShopName", userModels.get(position).userName);
                intent.putExtra("sellerProfileImg", userModels.get(position).profileImageUrl);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userModels.size();
    }

    class ChatRViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public TextView textView_comment;
        public TextView dateTextView;
        public ConstraintLayout chattingNewCLayout;

        public ChatRViewHolder(View v) {
            super(v);
            imageView = (ImageView) v.findViewById(R.id.shopImgView);
            textView = (TextView) v.findViewById(R.id.shopNameTextView);
            textView_comment = (TextView) v.findViewById(R.id.chattingTextView);
            dateTextView = (TextView) v.findViewById(R.id.dateTextView);
            chattingNewCLayout = (ConstraintLayout) v.findViewById(R.id.chattingNewCLayout);
        }
    }
}

//class ChattingData {
//    public int shopImgView = 0;
//    public String shopNameTextView = "";
//    public String chattingTextView = "";
//    public String dateTextView = "";
//    public int chattingNewCLayout = 0;
//}
