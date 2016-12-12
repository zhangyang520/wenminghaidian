package com.zhjy.hdcivilization.utils;

import android.content.Context;
import android.graphics.Point;
import android.util.DisplayMetrics;

import com.zhjy.hdcivilization.R;


/**
 * Created by zhangyang on 2016/8/4.
 */
public class DisplayUtil {

    public static  Point getScreenPoint(Context context){
        DisplayMetrics displayMetrics=context.getResources().getDisplayMetrics();
        Point point=new Point();
        point.x=displayMetrics.widthPixels;
        point.y=displayMetrics.heightPixels;
        return  point;
    }

    /**
     * 进行获取屏幕的高和宽的比例
     * @return
     */
    public static float getScreenRate(Context context){
        Point point=getScreenPoint(context);
        System.out.println("getScreenPoint point.x:"+point.x+"...point.y:"+point.y);
        float rate=(float)point.y/(float)point.x;
        return  rate;
    }

    public static float getSmallScreenRate(Context context){
        Point point=getScreenPoint(context);
        float rate=point.x/context.getResources().getDimension(R.dimen.camera_rect_height);
        return rate;
    }
}
