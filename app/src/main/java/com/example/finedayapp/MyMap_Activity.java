package com.example.finedayapp;


import android.app.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.finedayapp.DailyCount.DatabaseHelper;
import com.example.finedayapp.MyMap.LocationBean;
import com.example.finedayapp.MyMap.MapDatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyMap_Activity extends Activity {

    private MapView mMapView = null;
    private AMap aMap = null;
    private Button mapButton = null;
    private Button tracksbutton = null;

    private LocationBean locationBean = new LocationBean();
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器
    public AMapLocationClientOption mLocationOption = null;
    private MapDatabaseHelper mDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymap_activity);
        mapButton = findViewById(R.id.mapbutton);
        tracksbutton = findViewById(R.id.tracksbutton);
        mDatabaseHelper = new MapDatabaseHelper(this);


        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();

        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.strokeWidth((float) 1);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_MAP_ROTATE);//连续定位、蓝点不会移动到地图中心点，地图依照设备方向旋转，并且蓝点会跟随设备移动。

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        initMapLocation();
        tracksbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getApplicationContext(), "欢迎进入我的足迹", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                toast.show();
                Intent intent = new Intent(MyMap_Activity.this, MyMapTracks_Acitivity.class);
                startActivity(intent);
            }
        });
        mapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (locationBean.getLatitude() == null || locationBean.getLongitude() == null) {
                    Toast toast = Toast.makeText(getApplicationContext(), "请打开位置信息", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), locationBean.toString(), Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                    toast.show();

                }
            }
        });
    }

    private void initMapLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(MyMap_Activity.this);
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为高精度模式，AMapLocationMode.Battery_Saving为低功耗模式，AMapLocationMode.Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setNeedAddress(true);//设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setOnceLocation(false);//设置是否只定位一次,默认为false
        mLocationOption.setWifiActiveScan(true);//设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setMockEnable(false);//设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setInterval(2000);//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setOnceLocation(false);//可选，是否设置单次定位默认为false即持续定位
        mLocationOption.setOnceLocationLatest(false); //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        mLocationOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                    Date d = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = sdf.format(d);
//                    aMapLocation.getLatitude();//获取纬度
//                    aMapLocation.getLongitude();//获取经度
//                    aMapLocation.getAccuracy();//获取精度信息
//                    aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                    aMapLocation.getCountry();//国家信息
//                    aMapLocation.getProvince();//省信息
//                    aMapLocation.getCity();//城市信息
//                    aMapLocation.getDistrict();//城区信息
//                    aMapLocation.getStreet();//街道信息
//                    aMapLocation.getStreetNum();//街道门牌号信息
//                    aMapLocation.getCityCode();//城市编码
//                    aMapLocation.getAdCode();//地区编码
                    locationBean.setLatitude(String.valueOf(aMapLocation.getLatitude()));
                    locationBean.setLongitude(String.valueOf(aMapLocation.getLongitude()));
                    TextView tv = findViewById(R.id.gpstext);
                    tv.setText(aMapLocation.getAddress());
                    if (locationBean.getLatitude() == null || locationBean.getLongitude() == null) {
                        Toast toast = Toast.makeText(getApplicationContext(), "请打开位置信息", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                        toast.show();
                    } else {
                        mDatabaseHelper.insertCost(locationBean);
                    }
                    mLocationClient.stopLocation();//停止定位
                } else {
                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("info", "location Error, ErrCode:"
                            + aMapLocation.getErrorCode() + ", errInfo:"
                            + aMapLocation.getErrorInfo());
                }
            }
        }
    };

    protected void onDestroy() {
        super.onDestroy();
        //销毁
        if (mLocationClient != null) {
            mLocationClient.onDestroy();
        }
    }
}
