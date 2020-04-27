package com.minimalistweather.android.utils;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.minimalistweather.android.db_entity.City;
import com.minimalistweather.android.db_entity.County;
import com.minimalistweather.android.db_entity.Province;
import com.minimalistweather.android.gson_entity.WeatherInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {

    /**
     * 处理服务器响应的省级数据
     * @param response
     * @return
     */
    public static boolean provinceResponseHandler(String response) {
        if (!TextUtils.isEmpty(response)) { // 判断是否有响应数据
            try {
                JSONArray provinceArr = new JSONArray(response);
                for (int i = 0; i < provinceArr.length(); i++) {
                    JSONObject provinceObject = provinceArr.getJSONObject(i);
                    Province province = new Province(provinceObject.getString("name"),
                            provinceObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理服务器响应的市级数据
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean cityResponseHandler(String response, int provinceId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray cityArr = new JSONArray(response);
                for (int i = 0; i < cityArr.length(); i++ ) {
                    JSONObject cityObject = cityArr.getJSONObject(i);
                    City city = new City(cityObject.getString("name"),
                            cityObject.getInt("id"), provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 处理服务器响应的区县数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean countyResponseHandler(String response, int cityId) {
        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray countyArr = new JSONArray(response);
                for (int i = 0; i < countyArr.length(); i++) {
                    JSONObject countyObject = countyArr.getJSONObject(i);
                    County county = new County(countyObject.getString("name"),
                            countyObject.getInt("id"),
                            countyObject.getString("weather_id"), cityId);
                    county.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析天气数据
     */
    public static WeatherInfo weatherInfoHandler(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherInfoContent = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weatherInfoContent, WeatherInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
