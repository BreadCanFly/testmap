package com.example.testmap;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends ActionBarActivity {

private MapView mMapView;
private BaiduMap mbaiduMap;
    private Context context;
    //定位变量
    private LocationClient mLocationClient;
    private MyLocationListener mLocationListener;
    private  boolean isFirsetin=true;
    private double mLatitude;
    private double mLongtitude;
    //自定义方向图标
    private BitmapDescriptor mIconLocation;

    private MyOrientionListener myOrientionListener;
    private float mCurrentX;
    private MyLocationConfiguration.LocationMode mlocationMode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        //注意该方法要再setContentView方法之前实现
        SDKInitializer.initialize(getApplicationContext());

        setContentView(R.layout.activity_main);
        this.context=this;
        initView();
        //初始化定位
        initLocation();

    }
    public void onGetSearchResult(CloudSearchResult result, int error) {
        //在此处理相应的检索结果
    }
    
    public void clickButton(View v)
    {
        centerToMyLocation(mLatitude, mLongtitude);
    }

    private void centerToMyLocation(double mLatitude, double mLongtitude) {
        LatLng latLng = new LatLng(mLatitude, mLongtitude);
        MapStatusUpdate msu = MapStatusUpdateFactory.newLatLng(latLng);
        mbaiduMap.animateMapStatus(msu);
    }

    private void initLocation() {
        mlocationMode= MyLocationConfiguration.LocationMode.NORMAL;
        mLocationClient=new LocationClient(this);
        mLocationListener=new MyLocationListener();
        mLocationClient.registerLocationListener(mLocationListener);
        //locatinClient的设置
        LocationClientOption option=new LocationClientOption();
        option.setCoorType("bd09ll");
        option.setIsNeedAddress(true);
        option.setOpenGps(true);
        option.setScanSpan(1000);
        mLocationClient.setLocOption(option);
        mIconLocation= BitmapDescriptorFactory.fromResource(R.drawable.composer_place);
        myOrientionListener=new MyOrientionListener(context);
        myOrientionListener.setOnOrientionListener(new MyOrientionListener.OnOrientionListener() {
            @Override
            public void onOrientionChanged(float x) {
                mCurrentX=x;
            }
        });

    }

    private void initView() {
        mMapView= (MapView) findViewById(R.id.baimapView);
        mbaiduMap=mMapView.getMap();
        MapStatusUpdate msu=MapStatusUpdateFactory.zoomTo(15.0f);
        mbaiduMap.setMapStatus(msu);


    }
    @Override


    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mbaiduMap.setMyLocationEnabled(true);
        if(!mLocationClient.isStarted())
        mLocationClient.start();
        //开启方向传感器
        myOrientionListener.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止定位。
        mbaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        //关闭方向传感器
        myOrientionListener.stop();
    }

    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId())
        {
            case R.id.mapcommon:
                mbaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                break;
            case R.id.mapsite:
                mbaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.mapTraffic:
                if(mbaiduMap.isTrafficEnabled())
                {
                    mbaiduMap.setTrafficEnabled(false);
                    item.setTitle("实时交通（off）");
                }else
                {
                    mbaiduMap.setTrafficEnabled(true);
                    item.setTitle("实时交通（on）");
                }
                break;
            case R.id.map_common:
                mlocationMode= MyLocationConfiguration.LocationMode.NORMAL;
                break;
            case R.id.map_follow:
                mlocationMode= MyLocationConfiguration.LocationMode.FOLLOWING;
                break;
            case R.id.map_compass:
                mlocationMode= MyLocationConfiguration.LocationMode.COMPASS;
                break;
            case R.id.mserch:
                Intent intent=new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("http://www.baidu.com"));
                startActivity(intent);
                break;
            default:
                break;


        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }
    private class MyLocationListener implements BDLocationListener
    {

        @Override
        public void onReceiveLocation(BDLocation location) {
            MyLocationData data = new MyLocationData.Builder()
                    .direction(mCurrentX)//
                    .accuracy(location.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mbaiduMap.setMyLocationData(data);
            MyLocationConfiguration config =new
                    MyLocationConfiguration(mlocationMode,true,mIconLocation);

            mbaiduMap.setMyLocationConfigeration(config);
            mLatitude=location.getLatitude();
            mLongtitude=location.getLongitude();
            if(isFirsetin)
            {
                centerToMyLocation(location.getLatitude(), location.getLongitude());
                isFirsetin=false;

                Toast.makeText(context,location.getAddrStr(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
