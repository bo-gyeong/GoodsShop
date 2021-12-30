package com.example.goodsshop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.Objects;

public class Category extends Fragment {

    View v;
    TextView album, photocard, collectbook, sticker, doll, gtok, cheerstick, slogan
            , badge, postcard, keyring, bag, buchae, birthday, acc, shirts
            , shoes, tumbler, etcOfficialgoods, etcNonOfficialgoods;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_category, container, false);

        //카테고리 내역 클릭 시 화면 전환
        album = v.findViewById(R.id.album);
        album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "앨범");
                startActivity(intent);
            }
        });

        photocard = v.findViewById(R.id.photocard);
        photocard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "포토카드");
                startActivity(intent);
            }
        });

        collectbook = v.findViewById(R.id.collectbook);
        collectbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "콜렉트북");
                startActivity(intent);
            }
        });

        sticker = v.findViewById(R.id.sticker);
        sticker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "스티커");
                startActivity(intent);
            }
        });

        doll = v.findViewById(R.id.doll);
        doll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "인형");
                startActivity(intent);
            }
        });

        gtok = v.findViewById(R.id.gtok);
        gtok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "그립톡");
                startActivity(intent);
            }
        });

        cheerstick = v.findViewById(R.id.cheerstick);
        cheerstick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "응원봉");
                startActivity(intent);
            }
        });

        slogan = v.findViewById(R.id.slogan);
        slogan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "슬로건");
                startActivity(intent);
            }
        });

        badge = v.findViewById(R.id.badge);
        badge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "뱃지");
                startActivity(intent);
            }
        });

        postcard = v.findViewById(R.id.postcard);
        postcard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "엽서");
                startActivity(intent);
            }
        });

        keyring = v.findViewById(R.id.keyring);
        keyring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "키링");
                startActivity(intent);
            }
        });

        bag = v.findViewById(R.id.bag);
        bag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "가방");
                startActivity(intent);
            }
        });

        buchae = v.findViewById(R.id.buchae);
        buchae.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "부채");
                startActivity(intent);
            }
        });

        birthday = v.findViewById(R.id.birthday);
        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "생일카페");
                startActivity(intent);
            }
        });

        acc = v.findViewById(R.id.acc);
        acc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "액세서리");
                startActivity(intent);
            }
        });

        shirts = v.findViewById(R.id.shirts);
        shirts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "옷");
                startActivity(intent);
            }
        });

        shoes = v.findViewById(R.id.shoes);
        shoes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "신발");
                startActivity(intent);
            }
        });

        tumbler = v.findViewById(R.id.tumbler);
        tumbler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "텀블러");
                startActivity(intent);
            }
        });

        etcOfficialgoods = v.findViewById(R.id.etcOfficialgoods);
        etcOfficialgoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "기타 공식굿즈");
                startActivity(intent);
            }
        });

        etcNonOfficialgoods = v.findViewById(R.id.etcNonOfficialgoods);
        etcNonOfficialgoods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AfterSearch.class);
                intent.putExtra("category", "기타 비공식굿즈");
                startActivity(intent);
            }
        });

        return v;
    }
}