package com.zhjy.hdcivilization.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @项目名：AroundYou
 * @类名称：NetUtils
 * @类描述： 判断各种网络是否可用
 * @创建人：HXF
 * @修改人：
 * @创建时间：2015-8-31 下午3:17:51
 */
public class NetUtils {

    private static NetUtils instance;

    private NetUtils() {

    }

    public static NetUtils getInstance() {
        if (instance == null) {
            synchronized (NetUtils.class) {
                if (instance == null) {
                    instance = new NetUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 检查网络是否正常
     */
    public boolean checkNetwork(Context conetxt) {
        ConnectivityManager manager = (ConnectivityManager) conetxt
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        /** 获得当前网络信息 */
        NetworkInfo info = manager.getActiveNetworkInfo();
        /** 判断是否连接 */
        if (info == null || !info.isConnected()) {
            return false;
        }
        return true;
    }

    /**
     * 判断当前使用的是否是WiFi网络
     */
    public boolean isWifiActive(Context mcontext) {
        Context context = mcontext.getApplicationContext();
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info;
        if (connectivity != null) {
            info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getTypeName().equals("WIFI")
                            && info[i].isConnected()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断Mobile是否可用
     */
    public boolean isMobileAvailable(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        /** 得到与手机网络相关的网络信息 */
        NetworkInfo networkInfo = manager
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 判断当前的网络类型--->WiFi还是mobile？
     */
    public String getNetworkName(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        /** 如果网络可用并已连接 */
        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            /** 得到当前的网络类型.0代表mobile;1代表WiFi */
            /** int type = activeNetworkInfo.getType(); */
            /** 得到网络类型名称 */
            return activeNetworkInfo.getTypeName();
        }
        return null;
    }

    /**
     * 判断网络连接状态：1:WiFi 2:流量 3:无网络
     */
    public int getNetworkIsConn(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) {
            return 3;
        } else if (info != null
                && info.getType() == ConnectivityManager.TYPE_WIFI) {
            return 1;
        } else if (info != null
                && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            return 2;
        }
        return 4;
    }

    /**
     * 判断接入点类型
     *
     * @return
     */
    public String isNetType(Context context) {
        String nettype = null;
        if (context == null) {
            return null;
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobNetInfo = connectivityManager.getActiveNetworkInfo();
        if (mobNetInfo != null) {
            if (mobNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                nettype = mobNetInfo.getTypeName(); // 当前联网类型是WIFI
            } else {
                nettype = mobNetInfo.getExtraInfo();// 当前联网类型是cmnet/cmwap
            }
        }
        return nettype;
    }

}
