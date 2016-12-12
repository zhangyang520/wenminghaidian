package com.zhjy.hdcivilization.utils;

import android.util.Log;

/**
 * @项目名：AroundYou
 * @类名称：HLog
 * @类描述： 便于管理Log的日志显示问题。 现在LOG_LEVEL的值为6，现在所有的日志都可以显示，
 * 在发布应用的时候LOG_LEVEL的值改为0，这样就安全管理应用信息。
 * @创建人：huangxianfeng
 * @修改人：
 * @创建时间：2015-8-18 上午10:42:30
 */
public class HLog {

    private static HLog instance;

    private HLog() {

    }

    public static HLog getInstance() {
        if (instance == null) {
            synchronized (HLog.class) {
                if (instance == null) {
                    instance = new HLog();
                }
            }
        }
        return instance;
    }

    public int LOG_LEVEL = 6;
    public int ERROR = 1;
    public int WARN = 2;
    public int INFO = 3;
    public int DEBUG = 4;
    public int VERBOS = 5;

    public void e(String tag, String msg) {
        if (LOG_LEVEL > ERROR) {// 异常
            Log.e(tag, msg);
        }
    }

    public void w(String tag, String msg) {
        if (LOG_LEVEL > WARN) {// 警告
            Log.w(tag, msg);
        }
    }

    public void i(String tag, String msg) {
        if (LOG_LEVEL > INFO) {// 信息
            Log.i(tag, msg);
        }
    }

    public void d(String tag, String msg) {
        if (LOG_LEVEL > DEBUG) {// 调试
            Log.d(tag, msg);
        }
    }

    public void v(String tag, String msg) {
        if (LOG_LEVEL > VERBOS) {// 版本号
            Log.v(tag, msg);
        }
    }
}
