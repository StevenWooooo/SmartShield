package com.frogdesign.smartshield;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DevicesRecyclerViewAdapter extends RecyclerView.Adapter<DevicesRecyclerViewAdapter.ViewHolder> {

    private List<String> names = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();
    private Context context;

    public DevicesRecyclerViewAdapter(Context context, List<String> names, List<String> imageUrls) {
        this.names = names;
        this.imageUrls = imageUrls;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_listitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Glide.with(context)
                .asBitmap()
                .load(imageUrls.get(i))
                .into(viewHolder.image);
        viewHolder.name.setText(names.get(i));
        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, names.get(i), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.device_image_view);
            name = itemView.findViewById(R.id.device_name);
        }

    }
}
