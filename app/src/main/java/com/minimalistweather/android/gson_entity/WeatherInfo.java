package com.minimalistweather.android.gson_entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherInfo {

    private String status;

    private BasicInfo basic;

    private AqiInfo aqi;

    private NowInfo now;

    @SerializedName("suggestion")
    private SuggestionInfo suggestionInfo;

    @SerializedName("daily_forecast")
    private List<ForecastInfo> forecastInfoList;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BasicInfo getBasic() {
        return basic;
    }

    public void setBasic(BasicInfo basic) {
        this.basic = basic;
    }

    public AqiInfo getAqi() {
        return aqi;
    }

    public void setAqi(AqiInfo aqi) {
        this.aqi = aqi;
    }

    public NowInfo getNow() {
        return now;
    }

    public void setNow(NowInfo now) {
        this.now = now;
    }

    public SuggestionInfo getSuggestionInfo() {
        return suggestionInfo;
    }

    public void setSuggestionInfo(SuggestionInfo suggestionInfo) {
        this.suggestionInfo = suggestionInfo;
    }

    public List<ForecastInfo> getForecastInfoList() {
        return forecastInfoList;
    }

    public void setForecastInfoList(List<ForecastInfo> forecastInfoList) {
        this.forecastInfoList = forecastInfoList;
    }
}
