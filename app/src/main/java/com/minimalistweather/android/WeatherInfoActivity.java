package com.minimalistweather.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.minimalistweather.android.gson_entity.ForecastInfo;
import com.minimalistweather.android.gson_entity.WeatherInfo;
import com.minimalistweather.android.utils.HttpUtil;
import com.minimalistweather.android.utils.JsonUtil;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherInfoActivity extends AppCompatActivity {

    public DrawerLayout drawerLayout;

    public SwipeRefreshLayout swipeRefreshLayout; // 用于下拉刷新

    private Button navButton; // 用于切换城市

    private ScrollView weatherInfoLayout;

    private TextView weatherInfoTitleCity; // 天气布局头部城市名

    private TextView weatherInfoUpdateTime; // 天气布局头部天气更新时间

    private TextView weatherInfoDegree; // 当前温度

    private TextView weatherInfoSurvey; // 天气概况

    private LinearLayout weatherForecastLayout; // 天气预报布局

    private TextView weatherInfoAriQuality; // AQI指数

    private TextView weatherInfoPM25; // PM2.5指数

    private TextView weatherInfoSuggestionComfort; // 舒适度

    private TextView weatherInfoSuggestionWashCar; // 洗车指数

    private TextView weatherInfoSuggestionSport; // 运动指数

    private ImageView bingImg; // 背景

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_info);

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.drawer_container);
        if (fragment == null) {
            fragment = new ChooseAreaFragment();
            fragmentManager.beginTransaction().add(R.id.drawer_container, fragment).commit();
        }

        // 将背景图和状态栏融合到一起
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        // 初始化
        weatherInfoLayout = (ScrollView) findViewById(R.id.weather_info_layout);
        weatherInfoTitleCity = (TextView) findViewById(R.id.weather_info_title_city);
        weatherInfoUpdateTime = (TextView) findViewById(R.id.weather_info_update_time);
        weatherInfoDegree = (TextView) findViewById(R.id.weather_info_degree);
        weatherInfoSurvey = (TextView) findViewById(R.id.weather_info_survey);
        weatherForecastLayout = (LinearLayout) findViewById(R.id.weather_info_weather_forecast_layout);
        weatherInfoAriQuality = (TextView) findViewById(R.id.weather_info_air_quality);
        weatherInfoPM25 = (TextView) findViewById(R.id.weather_info_air_pm25);
        weatherInfoSuggestionComfort = (TextView) findViewById(R.id.weather_info_suggestion_comfort);
        weatherInfoSuggestionWashCar = (TextView) findViewById(R.id.weather_info_suggestion_wash_car);
        weatherInfoSuggestionSport = (TextView) findViewById(R.id.weather_info_suggestion_sport);
        bingImg = (ImageView) findViewById(R.id.bing_img);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        navButton = (Button) findViewById(R.id.nav_button);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary); // 设置下拉进度条的颜色

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherInfoContent = preferences.getString("weather", null);
        final String weatherId;
        if (weatherInfoContent != null) {
            WeatherInfo weatherInfo = JsonUtil.weatherInfoHandler(weatherInfoContent);
            weatherId = weatherInfo.getBasic().getWeatherId();
            // 展示天气信息
            showWeatherInfoContent(weatherInfo);
        } else {
            // 本地没有缓存
            weatherId = getIntent().getStringExtra("weather_id");
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            // 请求天气信息
            requestWeatherInfo(weatherId);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() { // 下拉请求天气数据
                requestWeatherInfo(weatherId);
            }
        });
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // 加载背景
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(bingImg);
        } else {
            loadBingImg();
        }
    }

    /**
     * 请求天气信息
     */
    public void requestWeatherInfo(final String weatherId) {
        String requestUrl = "http://guolin.tech/api/weather?cityid=" +
                weatherId + "&key=1f973beb7602432bb31cdceb9da27525";
        HttpUtil.sendHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherInfoActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false); // 刷新事件结束
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String repinseStr = response.body().string();
                final WeatherInfo weatherInfo = JsonUtil.weatherInfoHandler(repinseStr);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weatherInfo != null && "ok".equals(weatherInfo.getStatus())) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherInfoActivity.this)
                                    .edit();
                            editor.putString("weather", repinseStr);
                            editor.apply();
                            // 展示天气信息
                            showWeatherInfoContent(weatherInfo);
                        } else {
                            Toast.makeText(WeatherInfoActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefreshLayout.setRefreshing(false); // 刷新事件结束
                    }
                });
            }
        });
        loadBingImg();
    }

    /**
     * 加载背景图片
     */
    private void loadBingImg() {
        String requestUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherInfoActivity.this)
                        .edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherInfoActivity.this)
                                .load(bingPic)
                                .into(bingImg);
                    }
                });
            }
        });
    }

    /**
     * 展示天气信息
     */
    private void showWeatherInfoContent(WeatherInfo weatherInfo) {
        String cityName = weatherInfo.getBasic().getCityName();
        String updateTime = weatherInfo.getBasic().getUpdate().getUpdateTime().split(" ")[1];
        String degree = weatherInfo.getNow().getTemperature() + "℃";
        String weatherSurvey = weatherInfo.getNow().getCond().getInfo();

        weatherInfoTitleCity.setText(cityName); // 设置城市名
        weatherInfoUpdateTime.setText(updateTime); // 设置更新时间
        weatherInfoDegree.setText(degree); // 设置温度
        weatherInfoSurvey.setText(weatherSurvey); // 设置天气概况

        // 设置天气预报子项
        weatherForecastLayout.removeAllViews();
        for (ForecastInfo forecastInfo : weatherInfo.getForecastInfoList()) {
            View view = LayoutInflater.from(this)
                    .inflate(R.layout.weather_info_weather_forecast_item, weatherForecastLayout, false);
            TextView forecast_date = (TextView) view.findViewById(R.id.weather_info_weather_forecast_date);
            TextView forecast_survey = (TextView) view.findViewById(R.id.weather_info_weather_forecast_survey);
            TextView forecast_max_tmp = (TextView) view.findViewById(R.id.weather_info_weather_forecast_max_tmp);
            TextView forecast_min_tmp = (TextView) view.findViewById(R.id.weather_info_weather_forecast_min_tmp);
            forecast_date.setText(forecastInfo.getDate());
            forecast_survey.setText(forecastInfo.getCond().getInfo());
            forecast_max_tmp.setText(forecastInfo.getTemperatureRange().getMax());
            forecast_min_tmp.setText(forecastInfo.getTemperatureRange().getMin());
            weatherForecastLayout.addView(view);
        }
        if (weatherInfo.getAqi() != null) {
            weatherInfoAriQuality.setText(weatherInfo.getAqi().getCity().getAqi());
            weatherInfoPM25.setText(weatherInfo.getAqi().getCity().getPm25());
        }
        String comfort = "舒适度：" + weatherInfo.getSuggestionInfo().getComf().getComfInfo();
        String washCar = "洗车指数：" + weatherInfo.getSuggestionInfo().getCarWash().getCarWashInfo();
        String sport = "运动建议：" + weatherInfo.getSuggestionInfo().getSport().getSportInfo();

        weatherInfoSuggestionComfort.setText(comfort);
        weatherInfoSuggestionWashCar.setText(washCar);
        weatherInfoSuggestionSport.setText(sport);

        weatherInfoLayout.setVisibility(View.VISIBLE);
    }
}
