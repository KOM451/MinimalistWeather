package com.minimalistweather.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.minimalistweather.android.db_entity.City;
import com.minimalistweather.android.db_entity.County;
import com.minimalistweather.android.db_entity.Province;
import com.minimalistweather.android.utils.HttpUtil;
import com.minimalistweather.android.utils.JsonUtil;

import org.jetbrains.annotations.NotNull;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    public static final int PROVINCE = 0;

    public static final int CITY = 1;

    public static final int COUNTY = 2;

    private ProgressDialog progressDialog; // 显示进度（API26过时）

    private TextView areaTitleText; // 区域标题

    private Button backButton; // 返回按钮

    private RecyclerView recyclerView;

    private AreaAdapter adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Province> provinceList; // 省级列表

    private List<City> cityList; // 市级列表

    private List<County> countyList; // 区县级列表

    private Province currentProvince; // 当前省

    private City currentCity; // 当前市

    private int currentSelectedLevel; // 当前选中级别

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        areaTitleText = (TextView) view.findViewById(R.id.area_title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        adapter = new AreaAdapter(dataList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentSelectedLevel == COUNTY) {
                    queryCityList();
                } else if (currentSelectedLevel == CITY) {
                    queryProvinceList();
                }
            }
        });
        queryProvinceList();
    }

    private class AreaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private String areaName;

        private TextView areaItemText;

        public AreaHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_area, parent, false));

            itemView.setOnClickListener(this);
            areaItemText = (TextView) itemView.findViewById(R.id.area_item_text);
        }

        public void bind(String areaName) {
            this.areaName = areaName;
            areaItemText.setText(this.areaName);
        }

        @Override
        public void onClick(View v) {
            if (currentSelectedLevel == PROVINCE) {
                int position = getAdapterPosition();
                currentProvince = provinceList.get(position);
                queryCityList();
            } else if (currentSelectedLevel == CITY) {
                int position = getAdapterPosition();
                currentCity = cityList.get(position);
                queryCountyList();
            } else if (currentSelectedLevel == COUNTY) {
                int position = getAdapterPosition();
                String weatherId = countyList.get(position).getWeatherId();
                if (getActivity() instanceof MainActivity) {
                    Intent intent = new Intent(getActivity(), WeatherInfoActivity.class);
                    intent.putExtra("weather_id", weatherId);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof WeatherInfoActivity) {
                    WeatherInfoActivity activity = (WeatherInfoActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.swipeRefreshLayout.setRefreshing(true);
                    activity.requestWeatherInfo(weatherId);
                    activity.refreshWeatherId = weatherId; // 修复刷新bug
                }

            }
        }
    }

    private class AreaAdapter extends RecyclerView.Adapter<AreaHolder> {

        private List<String> areas;

        public AreaAdapter(List<String> areas) {
            this.areas = areas;
        }

        @NonNull
        @Override
        public AreaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new AreaHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull AreaHolder holder, int position) {
            String areaName = areas.get(position);
            holder.bind(areaName);
        }

        @Override
        public int getItemCount() {
            return areas.size();
        }
    }

    /**
     * 查询省级数据
     */
    private void queryProvinceList() {
        areaTitleText.setText("中国");
        backButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            dataList.clear();
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            currentSelectedLevel = PROVINCE;
        } else {
            String requestUrl = "http://guolin.tech/api/china/";
            accessServer(requestUrl, "province");
        }
    }

    /**
     * 查询市级数据
     */
    private void queryCityList() {
        areaTitleText.setText(currentProvince.getProvinceName());
        backButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceid = ?",
                String.valueOf(currentProvince.getProvinceId())).find(City.class);
        if (cityList.size() > 0) {
            dataList.clear();
            for (City city : cityList) {
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            currentSelectedLevel = CITY;
        } else {
            int provinceId = currentProvince.getProvinceId();
            String requestUrl = "http://guolin.tech/api/china/" + provinceId;
            accessServer(requestUrl, "city");
        }
    }

    /**
     * 查询区县数据
     */
    private void queryCountyList() {
        areaTitleText.setText(currentCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityid = ?",
                String .valueOf(currentCity.getCityId())).find(County.class);
        if (countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            currentSelectedLevel = COUNTY;
        } else {
            int provinceId = currentProvince.getProvinceId();
            int cityId = currentCity.getCityId();
            String requestUrl = "http://guolin.tech/api/china/" + provinceId + "/" + cityId;
            accessServer(requestUrl, "county");
        }
    }

    /**
     * 访问服务器获取数据
     * @param requestUrl
     * @param selectedType
     */
    private void accessServer(String requestUrl, final String selectedType) {
        showProgressDialog();
        HttpUtil.sendHttpRequest(requestUrl, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String responseStr = response.body().string();
                boolean result = false;
                switch (selectedType) {
                    case "province":
                        result = JsonUtil.provinceResponseHandler(responseStr);
                        break;
                    case "city":
                        result = JsonUtil.cityResponseHandler(responseStr, currentProvince.getProvinceId());
                        break;
                    case "county":
                        result = JsonUtil.countyResponseHandler(responseStr, currentCity.getCityId());
                        break;
                    default:
                        break;
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (selectedType) {
                                case "province":
                                    // 查询省级数据
                                    queryProvinceList();
                                    break;
                                case "city":
                                    // 查询市级数据
                                    queryCityList();
                                    break;
                                case "county":
                                    // 查询区县数据
                                    queryCountyList();
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
