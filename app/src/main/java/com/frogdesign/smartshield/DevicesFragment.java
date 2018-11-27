package com.frogdesign.smartshield;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class DevicesFragment extends Fragment implements MyRecyclerViewAdapter.ItemClickListener {

    public MyRecyclerViewAdapter adapter;
    public RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_devices, container, false);

        super.onCreate(savedInstanceState);

        MainActivity mainActivity = (MainActivity) getActivity();
        List<String> deviceNames = mainActivity.deviceNames;

                // set up the RecyclerView
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        adapter = new MyRecyclerViewAdapter(this.getContext(), deviceNames);
        adapter.setClickListener(this);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void refreshView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        adapter = new MyRecyclerViewAdapter(this.getContext(), mainActivity.deviceNames);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
    }

    @Override
    public void onItemClick(View view, int position) {
        // Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
