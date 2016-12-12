package com.zhjy.hdcivilization.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.zhjy.hdcivilization.application.MyApplication;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;


/**
 * 描 述 ： 进行创建UI的相关的工具方法
 **/
public class UiUtils {

    private static UiUtils instance;

    private UiUtils() {

    }

    public static UiUtils getInstance() {
        if (instance == null) {
            synchronized (UiUtils.class) {
                if (instance == null) {
                    instance = new UiUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 进行获取资源类
     *
     * @return
     */
    private Resources getResources() {

        return MyApplication.getContextObject().getResources();
    }


    /**
     * 进行获取图片的Drawable
     *
     * @param
     * @return
     */
    public Drawable getDrawable(int drawableId) {

        return getResources().getDrawable(drawableId);
    }

    public static int getDimen(int id) {

        return (int) (UiUtils.getInstance().getResources().getDimension(id) + 0.5f);
    }


    /**
     * 进行填充view对象
     *
     * @param viewId
     * @return
     */
    public View inflate(int viewId) {
        System.out.println("UiUtils...getContext() = " + (getContext() == null));
        return View.inflate(UiUtils.getInstance().getContext(), viewId, null);
    }


    /**
     * 进行实现runOnUiThread
     *
     * @param runnable
     */
    public void runOnUiThread(Runnable runnable) {

        // 首先进行判断是不是主线程
        if (android.os.Process.myTid() == //
                MyApplication.getMainId()) {
            // 如果是主线程,进行运行runnable
            runnable.run();
        } else {
            // 如果不是主线程,进行将方法进行提交都主线程
            MyApplication.getHandler().post(runnable);
        }
    }

    /**
     * 进行提供获取Context
     *
     * @return
     */
    public Context getContext() {
        return MyApplication.getContextObject();
    }

    /**
     * 进行获取Handler
     */
    public Handler getHandler() {
        return MyApplication.getHandler();
    }

    /**
     * 进行显示信息
     *
     * @param msg
     */
    public void showToast(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MyApplication.getContextObject(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * 进行获取屏幕的宽度
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public int getDefaultWidth() {
        WindowManager windowManager = (WindowManager) UiUtils.getInstance().getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getWidth();
    }

    /**
     * 进行获取屏幕的高度
     *
     * @return
     */
    @SuppressWarnings("deprecation")
    public int getDefaultHeight() {
        WindowManager windowManager = (WindowManager) UiUtils.getInstance().getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getHeight();
    }
}
