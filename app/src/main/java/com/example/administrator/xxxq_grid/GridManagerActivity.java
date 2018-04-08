package com.example.administrator.xxxq_grid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.example.administrator.common.BaseActivity;
import com.example.administrator.common.BaseUtils;
import com.example.administrator.common.CallBackSuccess;
import com.example.administrator.common.HttpRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 百度地图集成
 * Created by Cary on 2018/3/6.
 */

public class GridManagerActivity extends BaseActivity {

    private MapView mMapView = null;
    private AMap aMap;
    private List<Map<String, Object>> supplierList;
    private Button goList, goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grid_manager);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        initTitle();
        init();
        initData();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void init() {
        //初始化地图控制器对象
        if (aMap == null) {
            aMap = mMapView.getMap();
        }
        UiSettings uiSettings = aMap.getUiSettings();
        //设置缩放按钮显示
        uiSettings.setZoomControlsEnabled(false);
        //设置logo位置 这里设置bottom有效，如果设置left是无效的
        uiSettings.setLogoBottomMargin(-100);
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.showMyLocation(true);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);//定位一次，且将视角移动到地图中心点。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        goList = (Button) findViewById(R.id.activity_grid_manager_go_list);
        goList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GridManagerActivity.this, SupplierListActivity.class);
                startActivity(intent);
            }
        });
        goBack = (Button) findViewById(R.id.activity_grid_manager_go_back);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void initData() {
        Map<String, Object> map = new HashMap<String, Object>();
        supplierList = new ArrayList<Map<String, Object>>();
        new HttpRequest().post(GridManagerActivity.this, "gridManager/loadSupplierList", map, new CallBackSuccess() {
            @Override
            public void onCallBackSuccess(Object data) {
                Map<String, Object> map = (Map<String, Object>) data;
                List<Map<String, Object>> ls = (List<Map<String, Object>>) map.get("data");
                for (int i = 0; i < ls.size(); i++) {
                    supplierList.add(ls.get(i));
                }
                handler.sendEmptyMessage(1);
            }
        });
    }

    /**
     * 消息处理,通知主线程更新ui
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    setData();
                    break;
            }
        }
    };

    private void setData() {
        //lng:纵坐标,lat:横坐标
        for (int i = 0; i < supplierList.size(); i++) {
            LatLng latLng = new LatLng(Double.parseDouble(BaseUtils.toString(supplierList.get(i).get("baiduLat"))), Double.parseDouble(BaseUtils.toString(supplierList.get(i).get("baiduLng"))));
            final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(BaseUtils.toString(supplierList.get(i).get("name"))).snippet("DefaultMarker"));
        }
        // 绑定 Marker 被点击事件
        aMap.setOnMarkerClickListener(markerClickListener);
    }

    // 定义 Marker 点击事件监听
    AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
        // marker 对象被点击时回调的接口
        // 返回 true 则表示接口已响应事件，否则返回false
        @Override
        public boolean onMarkerClick(Marker marker) {
            LatLng position = marker.getPosition();
            double lat = position.latitude;
            double lng = position.longitude;
            for (int i = 0; i < supplierList.size(); i++) {
                if (Double.parseDouble(BaseUtils.toString(supplierList.get(i).get("baiduLng"))) == lng
                        && Double.parseDouble(BaseUtils.toString(supplierList.get(i).get("baiduLat"))) == lat) {
                    showInfoWindow(supplierList.get(i));
                    break;
                }
            }
            return false;
        }
    };

    private void showInfoWindow(final Map<String, Object> map) {
        aMap.setInfoWindowAdapter(new AMap.InfoWindowAdapter() {
            View infoWindow = null;

            @Override
            public View getInfoWindow(final Marker marker) {
                if (infoWindow == null) {
                    infoWindow = LayoutInflater.from(GridManagerActivity.this).inflate(
                            R.layout.view_grid_manager_info_window, null);
                    TextView name = infoWindow.findViewById(R.id.view_grid_manager_info_window_name);
                    TextView address = infoWindow.findViewById(R.id.view_grid_manager_info_window_address);
                    TextView close = infoWindow.findViewById(R.id.view_grid_manager_info_window_close);
                    name.setText(BaseUtils.toString(map.get("name")));
                    address.setText(BaseUtils.toString(map.get("detailAdress")));
                    close.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            marker.hideInfoWindow();
                        }
                    });

                    Button go = (Button) infoWindow.findViewById(R.id.view_grid_manager_info_window_go);
                    go.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GridManagerActivity.this, SupplierDetailsHasListActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("id", BaseUtils.toString(map.get("id")));
                            intent.putExtras(bundle);
                            startActivity(intent);
                        }
                    });
                }
                render(marker, infoWindow);
                return infoWindow;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }

            /**
             * 自定义infowinfow窗口
             */
            public void render(Marker marker, View view) {
                //如果想修改自定义Infow中内容，请通过view找到它并修改
            }
        });//AMap类中
    }

    @Override
    protected void initTitle() {
        BaseUtils.setTransparentBar(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}
