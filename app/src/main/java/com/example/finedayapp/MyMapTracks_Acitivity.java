package com.example.finedayapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.example.finedayapp.DailyCount.CostBean;
import com.example.finedayapp.DailyCount.DatabaseHelper;
import com.example.finedayapp.MyMap.ClusterClickListener;
import com.example.finedayapp.MyMap.ClusterItem;
import com.example.finedayapp.MyMap.ClusterRender;
import com.example.finedayapp.MyMap.LocationBean;
import com.example.finedayapp.MyMap.MapDatabaseHelper;
import com.example.finedayapp.MyMap.RegionItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyMapTracks_Acitivity extends Activity implements ClusterRender,
        AMap.OnMapLoadedListener, ClusterClickListener {


    private MapView mMapView;
    private AMap mAMap;
    private Button cleartracks = null;
    private int clusterRadius = 100;

    private Map<Integer, Drawable> mBackDrawAbles = new HashMap<Integer, Drawable>();

    private ClusterOverlay mClusterOverlay;
    private MapDatabaseHelper mDatabaseHelper;
    private List<LocationBean> mLocationBeanList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mymap_showtracks);

        mDatabaseHelper = new MapDatabaseHelper(this);
        mLocationBeanList = new ArrayList<>();
        cleartracks = findViewById(R.id.cleartracks);

        mMapView = (MapView) findViewById(R.id.mytracks);
        mMapView.onCreate(savedInstanceState);


        initmap();
        cleartracks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyMapTracks_Acitivity.this);
                builder.setTitle("确定删除所有足迹么？");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mDatabaseHelper.deleteAllLocationData();
                        Toast toast = Toast.makeText(getApplicationContext(), "成功删除", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER, 0, 0);
                        toast.show();
                        finish();
                        Intent intent = new Intent(MyMapTracks_Acitivity.this, MyMapTracks_Acitivity.class);
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", null);
                builder.create().show();
            }
        });
    }

    private void initmap() {
        if (mAMap == null) {
            // 初始化地图
            mAMap = mMapView.getMap();
            mAMap.setOnMapLoadedListener(this);
            LatLng latLng = new LatLng(30.67, 104.07);//构造一个位置
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 4));
            //点击可以动态添加点
            mAMap.setOnMapClickListener(new AMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    double lat = Math.random() + 29.70;
                    double lon = Math.random() + 103.500;
                    LatLng latLng1 = new LatLng(lat, lon, false);
                    RegionItem regionItem = new RegionItem(latLng1,
                            "test");
                    mClusterOverlay.addClusterItem(regionItem);

                }
            });
        }
    }


    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    protected void onDestroy() {
        super.onDestroy();
        //销毁资源
        mClusterOverlay.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onMapLoaded() {
        Cursor cursor = mDatabaseHelper.getAllLocationData();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                LocationBean locationBean = new LocationBean();
                locationBean.setLatitude(cursor.getString(cursor.getColumnIndex("latitude")));
                locationBean.setLongitude("" + cursor.getString(cursor.getColumnIndex("longitude")));
                mLocationBeanList.add(locationBean);
            }
            cursor.close();
        }
        //添加测试数据
        new Thread() {
            public void run() {
                List<ClusterItem> items = new ArrayList<ClusterItem>();
                for (int i = 0; i < mLocationBeanList.size(); i++) {

                    double lat = new Double(mLocationBeanList.get(i).getLatitude());
                    double lon = new Double(mLocationBeanList.get(i).getLongitude());
                    LatLng latLng = new LatLng(lat, lon, false);
                    RegionItem regionItem = new RegionItem(latLng,
                            "test" + i);
                    items.add(regionItem);
                }
                mClusterOverlay = new ClusterOverlay(mAMap, items,
                        dp2px(getApplicationContext(), clusterRadius),
                        getApplicationContext());
                mClusterOverlay.setClusterRenderer(MyMapTracks_Acitivity.this);
                mClusterOverlay.setOnClusterClickListener(MyMapTracks_Acitivity.this);
            }
        }.start();

    }


    @Override
    public void onClick(Marker marker, List<ClusterItem> clusterItems) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ClusterItem clusterItem : clusterItems) {
            builder.include(clusterItem.getPosition());
        }
        LatLngBounds latLngBounds = builder.build();
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0)
        );
    }

    @Override
    public Drawable getDrawAble(int clusterNum) {
        int radius = dp2px(getApplicationContext(), 80);
        if (clusterNum == 1) {
            Drawable bitmapDrawable = mBackDrawAbles.get(1);
            if (bitmapDrawable == null) {
                bitmapDrawable =
                        getApplication().getResources().getDrawable(
                                R.drawable.icon_openmap_mark);
                mBackDrawAbles.put(1, bitmapDrawable);
            }

            return bitmapDrawable;
        } else if (clusterNum < 5) {

            Drawable bitmapDrawable = mBackDrawAbles.get(2);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(159, 210, 154, 6)));
                mBackDrawAbles.put(2, bitmapDrawable);
            }

            return bitmapDrawable;
        } else if (clusterNum < 10) {
            Drawable bitmapDrawable = mBackDrawAbles.get(3);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(199, 217, 114, 0)));
                mBackDrawAbles.put(3, bitmapDrawable);
            }

            return bitmapDrawable;
        } else {
            Drawable bitmapDrawable = mBackDrawAbles.get(4);
            if (bitmapDrawable == null) {
                bitmapDrawable = new BitmapDrawable(null, drawCircle(radius,
                        Color.argb(235, 215, 66, 2)));
                mBackDrawAbles.put(4, bitmapDrawable);
            }

            return bitmapDrawable;
        }
    }

    private Bitmap drawCircle(int radius, int color) {

        Bitmap bitmap = Bitmap.createBitmap(radius * 2, radius * 2,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        RectF rectF = new RectF(0, 0, radius * 2, radius * 2);
        paint.setColor(color);
        canvas.drawArc(rectF, 0, 360, true, paint);
        return bitmap;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


}
