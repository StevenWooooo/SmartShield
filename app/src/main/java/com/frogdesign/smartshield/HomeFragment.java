package com.frogdesign.smartshield;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.Speedometer;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private List<String> names = new ArrayList<>();
    private List<String> imageUrls = new ArrayList<>();

    public DevicesRecyclerViewAdapter adapter;
    public RecyclerView recyclerView;

    public SpeedView speedometer;

    void initDevices() {
        MainActivity mainActivity = (MainActivity) getActivity();
        for (String name : mainActivity.deviceNames) {
            names.add(name);
            imageUrls.add(mainActivity.name2url.get(name));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, null);

//        String strtext = getArguments().getString("traffic");
//        TextView test = view.findViewById(R.id.test_seg);
//        test.setText(strtext);

        speedometer = view.findViewById(R.id.speedView);
        speedometer.speedTo(80, 4000);
        speedometer.setMarkColor(Color.argb(0,0,0,0));
        speedometer.setCenterCircleColor(Color.argb(0,0,0,0));

        speedometer.setLowSpeedPercent(30);
        speedometer.setMediumSpeedPercent(70);

        speedometer.setHighSpeedColor(Color.argb(255,120,180,40));
        speedometer.setMediumSpeedColor(Color.argb(255,245,210,70));
        speedometer.setLowSpeedColor(Color.argb(255,230,80,60));
        // speedometer.setSpeedometerWidth(120);
        speedometer.setTextColor(Color.argb(0,0,0,0));
        speedometer.setSpeedTextColor(Color.argb(0,0,0,0));
        speedometer.setUnitTextColor(Color.argb(0,0,0,0));

        speedometer.setIndicator(Indicator.Indicators.KiteIndicator);
        speedometer.setIndicatorWidth(60);
        speedometer.setIndicatorColor(Color.argb(150,205,205,205));
        speedometer.setIndicatorLightColor(Color.argb(100,255,255,255));

        initDevices();
        System.out.println("devices number = " + names.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView = view.findViewById(R.id.recycler_devices);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DevicesRecyclerViewAdapter(view.getContext(), names, imageUrls);
        recyclerView.setAdapter(adapter);

        return view;
    }

    public void refreshView() {
        MainActivity mainActivity = (MainActivity) getActivity();
        names = new ArrayList<>();
        imageUrls = new ArrayList<>();
        for (String name : mainActivity.deviceNames) {
            names.add(name);
            imageUrls.add(mainActivity.name2url.get(name));
        }
        adapter = new DevicesRecyclerViewAdapter(this.getContext(), names, imageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.invalidate();
    }
}
