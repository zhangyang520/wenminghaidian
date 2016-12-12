package com.zhjy.hdcivilization.utils;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * 自适应屏幕的高度
 * @author niyl
 *
 */
public class ScreenUtil {

	private static ScreenUtil instance;

	private ScreenUtil(){

	}

	public static ScreenUtil getInstance(){
		if (instance == null){
			synchronized (ScreenUtil.class){
				if (instance == null){
					instance = new ScreenUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 把图片的比例传值进来，再测量屏幕的宽度，得出高度值
	 * @param context
	 * @param width
	 * @param height
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public int getHeight(Context context,int width,int height){
		WindowManager windowManager= (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		int scressWidth = windowManager.getDefaultDisplay().getWidth();
		if (width != 0) {
			return (int) ((height*scressWidth) / (float)width);
		}
		return 0;
	}

	
	/**
	 * dp转换成像素
	 * @param context
	 * @param dp
	 * @return
	 */
	public int dp2Px(Context context, float dp) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (dp * scale + 0.5f);  
	}  

	/**
	 * 像素转换成dp
	 * @param context
	 * @param px
	 * @return
	 */
	public int px2Dp(Context context, float px) {  
	    final float scale = context.getResources().getDisplayMetrics().density;  
	    return (int) (px / scale + 0.5f);  
	}  
	
}
