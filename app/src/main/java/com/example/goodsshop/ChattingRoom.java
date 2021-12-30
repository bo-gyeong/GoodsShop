package com.example.goodsshop;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ChattingRoom extends AppCompatActivity {

    Intent getSellerInfoIntent;
    String otherUid, otherShopName, otherProfileImgUrl, targetSellerUid, goodsName;

    private String uid;
    private String sellerUid;
    private ImageButton imageButton;
    private EditText editText;
    private String chatRoomUid;
    private String myShopName;


    private UserInfo sellerInfo;

    private RecyclerView recyclerView;
    // 시간 변환을 위한 변수 선언
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy.MM.dd.hh.mm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatting_room);

        getSellerInfoIntent = getIntent();
        sellerUid = getSellerInfoIntent.getStringExtra("getOtherUid");  //리얼타임데베에 넣는다면 필요할것 같아서 추가 >> 채팅 당하는 아이디
        otherShopName = getSellerInfoIntent.getStringExtra("otherShopName");  //판매자 샵이름
        otherProfileImgUrl = getSellerInfoIntent.getStringExtra("otherProfileImg");  //프로필이미지 링크
        myShopName = getSellerInfoIntent.getStringExtra("myShopName");
        goodsName = getSellerInfoIntent.getStringExtra("getGoodsName");

        getSupportActionBar().setTitle(otherShopName);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  //back 버튼

        /*****
         * ChattingRoom.java 관련 만들어 놓은 layout:
         *
         * chatting_room_date_line
         * chatting_room
         * chatting_mine
         * chatting_other******/

        // 채팅을 요구하는 아이디. >> 단말기에 로그인된 Uid
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        imageButton = findViewById(R.id.sendMsgImgBtn);
        editText = findViewById(R.id.writeMsgEditText);
        recyclerView = findViewById(R.id.chattingRoom_recyclerview);


        // 버튼을 누르면, 채팅방이 만들어진다. 만들어져있으면 메시지만 전송하고, 없으면 채팅방의 데이터베이스 구조까지 만든다.
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid, true);
                chatModel.users.put(sellerUid, true);

                chatModel.userInfo.put("goodsName", goodsName);
                chatModel.userInfo.put("uid", uid);
                chatModel.userInfo.put("shopName", myShopName);


                // 채팅방이 없을 경우, 데이터베이스에 채팅방을 만들어줌
                if (chatRoomUid == null) {
                    // 광클 방지용
                    imageButton.setEnabled(false);
                    // push()는 이름을 임의적으로 만들어주는 것, 기본키 정도로 생각하자.
                    FirebaseDatabase.getInstance().getReference().child("chatrooms")
                            .push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        // 만약 만들어진 대화방을 삭제하는 경우가 발생한다면, checkChatRoom은 맨 밑에 한 번 호출하기 때문에 버그가 발생한다. 그것을 방지하기 위해서 이 코드가 실행될 때마다 콜백하는 것.
                        @Override
                        public void onSuccess(Void unused) {
                            checkChatRoom();
                        }
                    });

                    // 채팅방이 있을 경우
                } else {
                    ChatModel.Comment comment = new ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();
                    // 현재 날짜의 1970년 1월 1일 시간을 뺀 초의 값을 주기 때문에, 맞게 다시 만들어줘야한다.
                    comment.timestamp = ServerValue.TIMESTAMP;
                    FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid)
                            .child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            // 채팅 보내는 버튼 누르면 메시지 입력창을 초기화 시켜주는 코드
                            editText.setText("");
                        }
                    });
                }
            }
        });
        checkChatRoom();
    }

    // 채팅방의 중복을 체크해주는 코드. 전송을 누를 때마다, 데이터베이스를 만들면 안되니까, 중복되면 메시지만 가도록 해줌
    void checkChatRoom() {
        FirebaseDatabase.getInstance().getReference().child("chatrooms").orderByChild("users/" + uid)
                .equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot item : snapshot.getChildren()) {
                    // 파이어베이스 데이터베이스 안에 chatrooms의 users가 갖는 uid와 sellerUid를 받아옴.
                    ChatModel chatModel = item.getValue(ChatModel.class);
                    String seller = item.getKey();

                    // 원하는 상대방 Uid가 있으면, chatRoomUid에 chatrooms의 push값(item.getKey)을 넣어줌
                    if (chatModel.users.containsKey(sellerUid)) {
                        chatModel.users.put(uid, true);

                        chatRoomUid = item.getKey(); // user 상위의 해쉬값 즉, 방아이디에 해당되는 qn분
                        imageButton.setEnabled(true); // 광클방지용으로 꺼준 버튼을 다시 살림..
                        recyclerView.setLayoutManager(new LinearLayoutManager(ChattingRoom.this));
                        recyclerView.setAdapter(new RecyclerViewAdapter());
                    } else {

//                        chatModel.users.put(uid, true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        List<ChatModel.Comment> comments;

        // 생성자 >> comment를 담아오는 코드
        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            // 유저에 대한 정보를 담아오고, 메시지를 출력하는 getMessageList()를 사용
            FirebaseDatabase.getInstance().getReference().child("users").child(sellerUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    sellerInfo = snapshot.getValue(UserInfo.class);
                    getMessageList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        void getMessageList() {
            /*
           파이어베이스데이터베이스에서 comment로 접근하는 것.
           chatrooms 중 특정 chatRoomUid를 갖고 있는 comment를 addValueEventListener로 읽어들인다.
             */
            FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    comments.clear(); // 데이터가 추가될때마다 서버에서 모든 내용을 다 넣어주기 때문에 클리어를 해줘서 쌓이는 녀석을 없애줘야함

                    // 데이터베이스에서 읽어온 정보를 객체에 추가하는 코드
                    for (DataSnapshot item : snapshot.getChildren()) {
                        comments.add(item.getValue(ChatModel.Comment.class));
                    }
                    notifyDataSetChanged(); // 데이터 갱신
                    // 메시지가 보내지면 최근 보낸 메시지가 보이도록 밑으로 내려가게 하는 코드
                    recyclerView.scrollToPosition(comments.size() - 1);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);

            // view를 재사용할 때 사용하는 클래스이다.
            return new ChattingViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ChattingViewHolder chattingViewHolder = ((ChattingViewHolder) holder);

            // 내가 대화할 때의 내용 >> 코맨트의 uid와 uid를 비교해서 같을 경우를 뜻함
            if (comments.get(position).uid.equals(uid)) {
                chattingViewHolder.textView_message.setText(comments.get(position).message);
                chattingViewHolder.textView_message.setBackgroundResource(R.drawable.chatting_mine_shape);
                chattingViewHolder.linearLayout_seller.setVisibility(View.INVISIBLE);
                chattingViewHolder.textView_message.setTextSize(20);
                chattingViewHolder.linearlayout_main.setGravity(Gravity.RIGHT);
            }
            // 상대방의 내용
            else {
                Glide.with(holder.itemView.getContext())
                        .load(sellerInfo.profileImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .placeholder(R.drawable.ic_baseline_person_24)
                        .into(chattingViewHolder.imageView_profile);
                chattingViewHolder.linearLayout_seller.setVisibility(View.VISIBLE);
                chattingViewHolder.textView_message.setText(comments.get(position).message);
                chattingViewHolder.textView_message.setBackgroundResource(R.drawable.chatting_other_shape);
                chattingViewHolder.textView_message.setTextSize(20);
                chattingViewHolder.linearlayout_main.setGravity(Gravity.LEFT);
            }
            long unixTime = (long) comments.get(position).timestamp;
            Date date = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(date);
            chattingViewHolder.textView_timeStamp.setText(time);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }

        // view를 재사용할 때 사용하는 클래스
        private class ChattingViewHolder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_seller;
            public LinearLayout linearlayout_main;
            public TextView textView_timeStamp;

            public ChattingViewHolder(View view) {
                super(view);
                textView_message = view.findViewById(R.id.messageItem_textView_message);
                imageView_profile = view.findViewById(R.id.messageItem_imageview_profile);
                linearLayout_seller = view.findViewById(R.id.messageItem_linearlayout_seller);
                linearlayout_main = view.findViewById(R.id.messageItem_linearlayout_main);
                textView_timeStamp = view.findViewById(R.id.messageItem_textview_timestamp);
            }
        }
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
