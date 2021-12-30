package com.example.goodsshop;

import android.content.Intent;
import android.view.View;

public class FabClickListener implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(v.getContext(), Jjim.class);
        v.getContext().startActivity(intent);
    } // FAB Click 이벤트 처리
}
