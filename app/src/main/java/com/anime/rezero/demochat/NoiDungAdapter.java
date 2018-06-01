package com.anime.rezero.demochat;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by zing on 10/6/2017.
 */

public class NoiDungAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<NoiDungChat> noiDungChatList;

    public NoiDungAdapter(Context context, int layout, List<NoiDungChat> noiDungChatList) {
        this.context = context;
        this.layout = layout;
        this.noiDungChatList = noiDungChatList;
    }

    @Override
    public int getCount() {
        return noiDungChatList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.noidungchat_activity,null);

        TextView tvChat = (TextView) convertView.findViewById(R.id.tv_chat);
        ImageView imgHinh = (ImageView) convertView.findViewById(R.id.img_anh);
        ImageView imgSound = (ImageView) convertView.findViewById(R.id.img_sound);
        NoiDungChat noiDungChat = noiDungChatList.get(position);

        if (noiDungChat.getHinh().length==0) {
            imgHinh.setVisibility(View.GONE);
        }
        else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(noiDungChat.getHinh(),0,noiDungChat.getHinh().length);
            imgHinh.setImageBitmap(bitmap);
        }

        if(noiDungChat.getNoidung().equals("")){
            tvChat.setVisibility(View.GONE);
        }else{
            tvChat.setText(noiDungChat.getNoidung());
        }

        if (noiDungChat.getAmthanh().length==0) {
            imgSound.setVisibility(View.GONE);
        }
        else {
            Bitmap bitmap = BitmapFactory.decodeByteArray(noiDungChat.getAmthanh(),0,noiDungChat.getAmthanh().length);
            imgSound.setImageBitmap(bitmap);
        }
        return convertView;
    }
}
