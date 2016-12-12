package com.zhjy.hdcivilization.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by zhangyang on 2016/7/7.
 */
public class LocationService extends Service {
    //定位数据集合
    private CopyOnWriteArrayList<LocationData> locationDatas=new CopyOnWriteArrayList<LocationData>();
    private final int datasSize=4;
    LocationClient locationClient;
    private Timer timer;
    private long locationStateTime;

    @Override
    public void onCreate() {
        super.onCreate();
        locationDatas.clear();

        //进行初始化设置 LocationClient
        locationClient = new LocationClient(UiUtils.getInstance().getContext());
        MyLocationListener locationListener=new MyLocationListener();
        locationClient.registerLocationListener(locationListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // / 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        option.setIsNeedLocationPoiList(true);
        option.setIsNeedAddress(true);
        option.setIsNeedLocationDescribe(true);
        option.setLocationMode(LocationClientOption.LocationMode.Battery_Saving);
        locationClient.setLocOption(option);
        locationClient.start();

        //初始化locationStateTime
        locationStateTime=System.currentTimeMillis();
        System.out.println("LocationService ...onCreate....");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new LocationInterfaceClass();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("LocationService ...onStartCommand....");
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("LocationService onDestroy ...");
        //位置定位停止
        locationClient.stop();
        SharedPreferencesManager.remove(UiUtils.getInstance().getContext(), HDCivilizationConstants.LOCATION_STATE);
    }


    /**
     * 定位的数据
     */
    public class LocationData{
        private String longitude;//经度
        private String latitude;//纬度
        private List<String> positoinDetail;//地址集合

        public LocationData() {
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public List<String> getPositoinDetail() {
            return positoinDetail;
        }

        public void setPositoinDetail(List<String> positoinDetail) {
            this.positoinDetail = positoinDetail;
        }
    }

    /**
     * 地址定位的监听器
     */
    public class MyLocationListener implements BDLocationListener{

        public MyLocationListener() {
            super();
        }

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;
            if (location.getLocType() == BDLocation.TypeGpsLocation) {
                System.out.println("TypeGpsLocation location.getLatitude():" + location.getLatitude() + "..location.getLongitude():" + location.getLongitude());
                SharedPreferencesManager.put(UiUtils.getInstance().getContext(), HDCivilizationConstants.LOCATION_STATE,true);
                locationStateTime=System.currentTimeMillis();
                if(locationDatas.size()<datasSize){
                    //如果小于固定的长度
                    LocationData data=new LocationData();
                    data.setLatitude(location.getLatitude() + "");
                    data.setLongitude(location.getLongitude() + "");
//                    data.setPositoinDetail(getPositionList(location,data));
                    getPositionList(location,data);
                    locationDatas.add(0, data);
                }else{
                    LocationData data=locationDatas.remove(locationDatas.size()-1);
                    data.setLongitude(location.getLatitude()+"");
                    data.setLongitude(location.getLongitude() + "");
//                    data.setPositoinDetail(location.getPoiList());
                    getPositionList(location,data);
                    locationDatas.add(0,data);
                }

//                System.out.println("detail position:"+location.getAddrStr()+"..getBuildingName:"+location.getBuildingName()+"..num:"+location.getStreetNumber()+"..street:"+location.getStreet()+"..district:"+location.getDistrict()+"..city:"+location.getCity()+"..LocationDescribe:"+location.getLocationDescribe());
//                for(Poi data:(List<Poi>)location.getPoiList()){
//                    System.out.println("Poi data:"+data.getName()+"..id:"+data.getId()+"..rank:"+data.getRank());
//                }
//                System.out.println("location.getLocType():"+location.getLocType()+"....TypeGpsLocation..size:"+locationDatas.size()+ "...pos:");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
                System.out.println("TypeNetWorkLocation location.getLatitude():"+location.getLatitude()+"..location.getLongitude():"+location.getLongitude());
                SharedPreferencesManager.put(UiUtils.getInstance().getContext(), HDCivilizationConstants.LOCATION_STATE,true);
                locationStateTime=System.currentTimeMillis();
                if(locationDatas.size()<datasSize){
                    //如果小于固定的长度
                    LocationData data=new LocationData();
                    data.setLatitude(location.getLatitude() + "");
                    data.setLongitude(location.getLongitude() + "");
//                    data.setPositoinDetail(location.getPoiList());
                    getPositionList(location,data);
                    locationDatas.add(0,data);
                }else{
                    LocationData data=locationDatas.remove(locationDatas.size()-1);
                    data.setLongitude(location.getLatitude()+"");
                    data.setLongitude(location.getLongitude() + "");
//                    data.setPositoinDetail(location.getPoiList());
                    getPositionList(location,data);
                    locationDatas.add(0,data);
                }

//                System.out.println("detail position:"+location.getAddrStr()+"..getBuildingName:"+location.getBuildingName()+"..street:"+location.getStreet()+"..num:"+location.getStreetNumber()+"..district:"+location.getDistrict()+"..city:"+location.getCity()+"..LocationDescribe:"+location.getLocationDescribe());
//                for(Poi data:(List<Poi>)location.getPoiList()){
//                    System.out.println("Poi data:"+data.getName()+"..id:"+data.getId()+"..rank:"+data.getRank());
//                }
//                System.out.println("location.getLocType():"+location.getLocType()+"....TypeNetWorkLocation..size:"+locationDatas.size());
            }else if(location.getLocType() == BDLocation.TypeNetWorkException ||
                    location.getLocType() == BDLocation.TypeCriteriaException ||
                    location.getLocType() == BDLocation.TypeCriteriaException ||
                    location.getLocType() == BDLocation.TypeServerError ||
                    location.getLocType() == BDLocation.TypeOffLineLocationFail ||
                    location.getLocType() == BDLocation.TypeOffLineLocationNetworkFail){
//                System.out.println("location.getLocType():"+location.getLocType()+"....exception..size:"+locationDatas.size());
                if((Boolean) SharedPreferencesManager.get(UiUtils.getInstance().getContext(),HDCivilizationConstants.LOCATION_STATE,true)){
                    //更新sp文件中的定位状态
                    locationStateTime=System.currentTimeMillis();
                    SharedPreferencesManager.put(UiUtils.getInstance().getContext(), HDCivilizationConstants.LOCATION_STATE,false);
                }else{
                    if(System.currentTimeMillis()-locationStateTime>HDCivilizationConstants.LOCATION_STATE_FAILE){
                        //如果超时,清楚集合
                        locationDatas.clear();
                    }
                }
            }
            System.out.println("thread name:"+Thread.currentThread().getName()+"...MyLocationListener");
        }
    }

    /**
     * 进行获取位置集合
     * @param location
     * @return
     */
    private List<String> getPositionList(BDLocation location,LocationData locationData){

        if(locationData.getPositoinDetail()!=null){
            //如果位置不为空
            List<String> positionList=locationData.getPositoinDetail();
            //先进行清除
            positionList.clear();
            positionList.add(location.getAddrStr());
            //需要进行循环其他地址
            if(location.getPoiList()!=null){
                for(Poi data:(List<Poi>)location.getPoiList()){
                    positionList.add(data.getName());
                }
            }
        }else{
            if(location.getPoiList()!=null){
                List<String> positionList=new ArrayList<String>();
                positionList.add(location.getAddrStr());
                //需要进行循环其他地址
                for(Poi data:(List<Poi>)location.getPoiList()){
                    positionList.add(data.getName());
                }
                locationData.setPositoinDetail(positionList);
            }
        }
        return null;
    }
    /**
     * 定义接口
     */
    interface  LocationInterface{
        LocationData getLeastLocationData();
    }

    /**
     * 定义aidl的接口继承Bind接口
     */
    public class LocationInterfaceClass extends Binder implements LocationInterface{

        @Override
        public LocationData getLeastLocationData() {
            System.out.println("getLeastLocationData size:"+locationDatas.size());
            if(locationDatas.size()<=0){
                return null;
            }else{
                System.out.println("LOCATION_STATE:"+(Boolean)SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.LOCATION_STATE,true)+"...TIME:"+(System.currentTimeMillis()-locationStateTime));
                if (!(Boolean) SharedPreferencesManager.get(UiUtils.getInstance().getContext(), HDCivilizationConstants.LOCATION_STATE, true) &&
                        (System.currentTimeMillis() - locationStateTime) > HDCivilizationConstants
                                .LOCATION_STATE_FAILE) {
                    //如果为false 定位失败 && 超时时间超过 AroundYouConstants.LOCATION_STATE_FAILE
                    locationDatas.clear();
                    return null;
                } else {
                    //如果为false 定位失败 && 超时时间超过 AroundYouConstants.LOCATION_STATE_FAILE
                    return locationDatas.get(0);
                }
            }
        }
    }
}

