package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.DisplayUtil;


/**
 * Created by Administrator on 2016/8/4.
 */
public class PreviewSurfaceView extends SurfaceView{

    private String Tag="PreviewSurfaceView";
    private Context context;
    public PreviewSurfaceView(Context context) {
        super(context);
        this.context=context;
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public PreviewSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width=MeasureSpec.getSize(widthMeasureSpec);
        int height=MeasureSpec.getSize(heightMeasureSpec);

        Point point= DisplayUtil.getScreenPoint(context);
        if(point.x>=context.getResources().getDimension(R.dimen.camera_rect_height)){
            //如果长度大于250dp
            height=(int)(width/DisplayUtil.getSmallScreenRate(context));
        }else{
            height=(int)(context.getResources().getDimension(R.dimen.camera_rect_height)/width);
        }
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(Tag,"PreviewSurfaceView onMeasure width:"+width+"...height:"+height);
        setMeasuredDimension(width,height);
    }
}
