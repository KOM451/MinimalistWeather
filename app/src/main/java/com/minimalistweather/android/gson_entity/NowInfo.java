package com.minimalistweather.android.gson_entity;

import com.google.gson.annotations.SerializedName;

public class NowInfo {

    @SerializedName("tmp")
    private String temperature;

    private Cond cond;

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public Cond getCond() {
        return cond;
    }

    public void setCond(Cond cond) {
        this.cond = cond;
    }

    public class Cond {

        @SerializedName("txt")
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}
