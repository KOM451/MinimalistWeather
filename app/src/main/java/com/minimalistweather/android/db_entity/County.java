package com.minimalistweather.android.db_entity;

import org.litepal.crud.LitePalSupport;

public class County extends LitePalSupport {

    private int id;

    private String countyName; // 区县名

    private int countyId; // 区县代号

    private String weatherId; // 天气id

    private int cityId; // 所属市级规划

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public int getCountyId() {
        return countyId;
    }

    public void setCountyId(int countyId) {
        this.countyId = countyId;
    }

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
