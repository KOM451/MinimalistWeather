package com.minimalistweather.android.db_entity;

import org.litepal.crud.LitePalSupport;

public class Province extends LitePalSupport {

    private int id;

    private String provinceName; // 省份名称

    private int provinceId; // 省份代号

    public Province() {
    }

    public Province(String provinceName, int provinceId) {
        this.provinceName = provinceName;
        this.provinceId = provinceId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(int provinceId) {
        this.provinceId = provinceId;
    }
}
