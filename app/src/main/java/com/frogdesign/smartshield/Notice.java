package com.frogdesign.smartshield;

import com.google.gson.annotations.SerializedName;

public class Notice {

    @SerializedName("name")
    private String name;
    @SerializedName("traffic")
    private String traffic;

    public Notice(String id, String title, String brief, String fileSource) {
        this.name = id;
        this.traffic = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTraffic() {
        return traffic;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }
}