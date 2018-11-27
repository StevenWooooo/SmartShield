package com.frogdesign.smartshield;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {
    private List<String> mData;
    private List<String> mStatus;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    private Context context;

    public Map<String, String> name2url = new HashMap<>();

    // data is passed into the constructor
    MyRecyclerViewAdapter(Context context, List<String> data, List<String> status) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mStatus = status;
        this.context = context;

        name2url.put("MacBook Pro", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQ2mWFoSbenRKHNP8Akv75PTExe88EmDMLDuuv1HNkTION4pGadOw");
        name2url.put("Alexa", "https://images-na.ssl-images-amazon.com/images/I/51TFnR7AtGL._SY300_QL70_.jpg");
        name2url.put("WyzeCam", "https://images-na.ssl-images-amazon.com/images/I/31pBkWRliML.jpg");
        name2url.put("iPhone X", "https://static.mts.rs/GALERIJA/MOBILNI%20TELEFONI/IPHONE/IPHONE%20X/iPhone_X_1_popup_1500x1500px.jpg");

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(context)
                .asBitmap()
                .load(name2url.get(mData.get(position)))
                .into(holder.myImage);
        holder.myTextView.setText(mData.get(position));
        holder.myStatus.setText(mStatus.get(position));
        if (mStatus.get(position).equals("unsecured")) {
            holder.myStatus.setTextColor(Color.RED);
        }

        holder.myImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, mData.get(position), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        CircleImageView myImage;
        TextView myTextView;
        TextView myStatus;

        ViewHolder(View itemView) {
            super(itemView);
            myImage = itemView.findViewById(R.id.deviceImage);
            myTextView = itemView.findViewById(R.id.deviceName);
            myStatus = itemView.findViewById(R.id.status);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
