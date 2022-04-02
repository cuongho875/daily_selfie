package com.example.daily_selfie;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

class CustomAdapter extends BaseAdapter {
    private Activity activity;
    ArrayList<Image> Imgs;
    public CustomAdapter(Activity activity,ArrayList<Image> Imgs){
        this.activity = activity;
        this.Imgs=Imgs;
    }
    @Override
    public int getCount() {
        return Imgs.size();
    }

    @Override
    public Object getItem(int position) {
        return Imgs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = activity.getLayoutInflater();

        view = inflater.inflate(R.layout.item_list, null);

        ImageView imgView = (ImageView) view.findViewById(R.id.image);
        TextView textView = (TextView) view.findViewById(R.id.textView);
        imgView.setImageBitmap(Imgs.get(position).getImg());
        textView.setText(Imgs.get(position).getName());
        return view;

    }
}
