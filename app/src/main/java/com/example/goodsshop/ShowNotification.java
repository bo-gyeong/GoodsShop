package com.example.goodsshop;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ShowNotification extends BaseAdapter {

    LayoutInflater layoutInflater = null;
    ArrayList<NotificationData> showNotiArrListData = null;
    int count = 0;

    public ShowNotification(ArrayList<NotificationData> notiArrList)
    {
        showNotiArrListData =  notiArrList;
        count =  notiArrList.size();
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
            convertView = layoutInflater.inflate(R.layout.show_notification, parent, false);
        }

        TextView shopNameNotiTextView = convertView.findViewById(R.id.shopNameNotiTextView);
        TextView goodsNameNotiTextView = convertView.findViewById(R.id.goodsNameNotiTextView);

        shopNameNotiTextView.setText(showNotiArrListData.get(position).shopNameNotiTextView);
        goodsNameNotiTextView.setText(showNotiArrListData.get(position).goodsNameNotiTextView);

        return convertView;
    }
}

class NotificationData {
    public String shopNameNotiTextView = "";
    public String goodsNameNotiTextView = "";
}
