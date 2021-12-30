package com.example.goodsshop;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import java.util.TreeMap;

public class Chatting extends Fragment {
    View v;
    RecyclerView recyclerView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.hh.mm");
    String otherShopName;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_chatting, container, false);
        checkUid();

        recyclerView = v.findViewById(R.id.chattingRecyclerListView);
        // 리사이클러뷰와 어댑터를 바인딩하는 부분.
        recyclerView.setAdapter(new ChatRecyclerViewAdapter());
        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));

        return v;
    }

    class ChatRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private String uid;
        private List<ChatModel> chatModels = new ArrayList<>();
        // 대화할 사람들의 데이터가 담김
        private ArrayList<String> sellerUsers = new ArrayList<>();
        private ArrayList<String> sellerUsersShopName = new ArrayList<>();

        // 생성자
        public ChatRecyclerViewAdapter() {
            // 본인의 uid를 받아와서 선언한 변수에 담는 코드.
            uid = MainActivity.sharedUid();

            // 채팅에 대한 정보를 가져오는 부분.
            FirebaseDatabase.getInstance().getReference().child("chatrooms")
                    .orderByChild("users/" + uid).equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    chatModels.clear();
                    // 반복문을 통해 chatModels 객체에 채팅의 정보를 담음.
                    for (DataSnapshot item : snapshot.getChildren()) {
                        chatModels.add(item.getValue(ChatModel.class));
                    }
                    // 새로고침
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_chatting_list, parent, false);

            return new CustomViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

            // 데이터를 뷰에 바인딩 하는 부분
            CustomViewHolder customViewHolder = (CustomViewHolder) holder;
            String sellerUid = null;

            // 채팅방에 있는 유저를 일일히 체크하는 부분
            for (String user : chatModels.get(position).users.keySet()) {
                // user가 내가 아닐 경우의 조건문
                /************
                 * 이 부분에 수정이 좀 필요함.
                 ********** */
                if (!user.equals(uid) && !user.equals("goods")) {
                    sellerUid = user;
                    sellerUsers.add(sellerUid);
                }
            }


            FirebaseDatabase.getInstance().getReference().child("users").child(sellerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    UserInfo userInfo = snapshot.getValue(UserInfo.class);

                    Glide.with(customViewHolder.itemView.getContext())
                            .load(userInfo.profileImageUrl)
                            .apply(new RequestOptions().circleCrop())
                            .placeholder(R.drawable.ic_baseline_person_24)
                            .into(customViewHolder.imageView); //.fallback(R.drawable.ic_launcher_foreground)

                    customViewHolder.textView_name.setText(userInfo.shopName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            // 채팅 목록에 채팅방의 마지막 메시지를 띄워주는 코드
            Map<String, ChatModel.Comment> commentMap = new TreeMap<>(Collections.reverseOrder());
            commentMap.putAll(chatModels.get(position).comments);
            String lastMessageKey = (String) commentMap.keySet().toArray()[0];
            customViewHolder.textView_lastMessage.setText(chatModels.get(position).comments.get(lastMessageKey).message);

            // intent 부분 작성
            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 상대방 샵 이름 가져오기
                    FirebaseDatabase.getInstance().getReference().child("users").child(sellerUsers.get(position))
                            .child("shopName").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    otherShopName = snapshot.getValue(String.class);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                    Intent intent = new Intent(v.getContext(), ChattingRoom.class);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            intent.putExtra("getOtherUid", sellerUsers.get(position));
                            intent.putExtra("otherShopName", otherShopName);

                            startActivity(intent);
                        }
                    }, 500);
                }
            });
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/seoul"));
            long unixTime = (long) chatModels.get(position).comments.get(lastMessageKey).timestamp;
            Date date = new Date(unixTime);
            customViewHolder.textView_timestamp.setText(simpleDateFormat.format(date));
        }

        @Override
        public int getItemCount() {
            return chatModels.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView_name;
            public TextView textView_lastMessage;
            public TextView textView_timestamp;

            public CustomViewHolder(View v) {
                super(v);

                imageView = v.findViewById(R.id.shopImgView);
                textView_name = v.findViewById(R.id.shopNameTextView);
                textView_lastMessage = v.findViewById(R.id.chattingTextView);
                textView_timestamp = v.findViewById(R.id.dateTextView);
            }
        }
    }
    // 로그인 되었는지 확인
    private void checkUid(){
        String getUid = MainActivity.sharedUid();

        if(getUid == null) {  //uid가 없다면(로그인 상태가 아니라면) 로그인 화면으로 이동
            MypageLogin mypageLogin = new MypageLogin();

            Bundle bundle = new Bundle();
            bundle.putString("whatFragment", "Chatting");
            mypageLogin.setArguments(bundle);

            Objects.requireNonNull(getActivity()).getSupportFragmentManager()
                    .beginTransaction().replace(R.id.chattingCLayout, mypageLogin).commit();
        }
    }
}
