package com.zhjy.hdcivilization.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @项目名：AroundYou
 * @类名称：SPUtil
 * @类描述： 存储数据
 * @创建人：HXF
 * @修改人：
 * @创建时间：2015-12-9 下午2:50:51
 */
public class SPUtil {

    private static SPUtil instance;

    private SPUtil() {

    }

    public static SPUtil getInstance() {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil(UiUtils.getInstance().getContext());
                }
            }
        }
        return instance;
    }

    public final String PREF_NAME = "config";
    public final String VISIT_KEY = "visit_key";
    public final String LOCATION_VISIT_KEY = "location_visit_key";
    public final String LOACTION_KEY = "location";
    public final String CHANGENICK = "chang_nick";
    public final String MD5NUMBER = "md5_number";
    public final String MD5PASSWORD = "md5_password";
    public final String SIGNNAME = "sign_name";

    private Context context;

    private SPUtil(Context context) {
        this.context = context;
    }

    /**
     * get splashActivity  has visited(取)
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public boolean hasVisited(String key, boolean defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultValue);
    }

    /**
     * set the splashActivity is visited or not(存)
     *
     * @param key
     * @param value
     */
    public void setVisited(String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(PREF_NAME,
                Context.MODE_PRIVATE);
        sp.edit().putBoolean(key, value).commit();
    }

    /**
     * 存储到SP中的String类型数据
     */
    public void setString(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(LOACTION_KEY,
                Context.MODE_PRIVATE);
        sp.edit().putString(key, value).commit();
    }


    /**
     * 取出到SP中的String类型数据
     */
    public String hasString(String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(LOACTION_KEY,
                Context.MODE_PRIVATE);
        return sp.getString(key, value);
    }
}
