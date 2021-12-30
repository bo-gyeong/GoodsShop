package com.example.goodsshop;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    // 채팅방 유저들  , sellerUid와 Uid를 둘 다 갖고 있는 객체이다.
    public Map<String, Boolean> users = new HashMap<>();

    // 채팅 메시지에 대한 객체이며, 채팅방의 내용을 담는다.
    public Map<String, Comment> comments = new HashMap<>();

    public Map<String, String> userInfo = new HashMap<>();

    // 이너 클래스이며, 채팅 대화에 대한 객체
    public static class Comment {
        public String uid;
        public String message;
        public Object timestamp;
    }
}
