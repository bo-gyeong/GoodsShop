package com.example.goodsshop;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import static android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH;

public class Home extends Fragment {

    EditText searchEditText;
    ImageButton searchBtn;
    FloatingActionButton fab;

    Intent toAfterSearchIntent;
    String keyword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        toAfterSearchIntent = new Intent(getActivity(), AfterSearch.class);

        searchEditText = v.findViewById(R.id.searchEditText);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == IME_ACTION_SEARCH) {  //키보드 search 버튼 클릭 시
                    keyword = searchEditText.getText().toString().trim();
                    if (!keyword.equals("")) {
                        toAfterSearchIntent.putExtra("searchKeyword", keyword);
                        startActivity(toAfterSearchIntent);
                    } else {
                        Toast.makeText(getContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                    }
                }

                return false;
            }
        });  //검색EditText

        searchBtn = v.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword = searchEditText.getText().toString().trim();
                if (!keyword.equals("")){
                    toAfterSearchIntent.putExtra("searchKeyword", keyword);
                    startActivity(toAfterSearchIntent);
                } else{
                    Toast.makeText(getContext(), "검색어를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fab = v.findViewById(R.id.fab);
        fab.setOnClickListener(new FabClickListener());

        return v;
    }

}