package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.nfc.Tag;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.DisplayUtil;
import com.zhjy.hdcivilization.utils.UiUtils;


/**
 * Created by Administrator on 2016/8/4.
 */
public class MaskView extends ImageView{
    String TAG="MaskView";
    Rect rect;
    Context context;
    public static float heghtDividWidthRate=0.7f;//宽度和高度之间的比例
    public static float leftLength;

    private boolean isCircleEnable=false;
    private float blackHeight;//黑色的高度

    public float getBlackHeight() {
        return blackHeight;
    }

    public void setBlackHeight(float blackHeight) {
        this.blackHeight = blackHeight;
    }

    public boolean isCircleEnable() {
        return isCircleEnable;
    }

    public void setIsCircleEnable(boolean isCircleEnable) {
        this.isCircleEnable = isCircleEnable;
    }

    public MaskView(Context context) {
        super(context);
        initView(context);
    }

    public MaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    Paint areaPaint;//区域的白色的背景
    Paint linePaint;//线条的背景
    Paint grayPaint;//灰色的背景
    Paint mPaintTransparent;//透明的画饼
    /**
     * 进行初始化操作
     */
    private void initView(Context context){
        this.context=context;
        areaPaint=new Paint();
        areaPaint.setColor(context.getResources().getColor(R.color.camera_backgroud));
        areaPaint.setAlpha(255);
        areaPaint.setAntiAlias(true);
        areaPaint.setStyle(Paint.Style.FILL);

        grayPaint=new Paint();
        grayPaint.setColor(Color.BLACK);
        grayPaint.setAlpha(125);
        grayPaint.setAntiAlias(true);
        grayPaint.setStyle(Paint.Style.FILL);

        linePaint=new Paint();
        linePaint.setColor(Color.BLUE);
        linePaint.setAlpha(22);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(5f);
        linePaint.setStyle(Paint.Style.STROKE);

    }

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //进行直接绘制一些的矩形区域
        if(rect!=null){
            if(rect.top==0){
                //如果本身进行置顶
                //进行绘制矩形的线
                canvas.drawRect(rect.left - 1, rect.top - 1, rect.right + 1, rect.bottom + 1, linePaint);
                //进行绘制下方的矩形
                Point point= DisplayUtil.getScreenPoint(this.context);
                canvas.drawRect(rect.left,rect.bottom,point.x,point.y,areaPaint);
                Log.d(TAG, "onDraw rect.top==0:rect.left" + rect.left + "..rect.bottom:" + rect.bottom + "..point.x:" + point.x + "..point.y:" + point.y);
            }else{
                //如果不是置顶
                Point point= DisplayUtil.getScreenPoint(this.context);
                canvas.drawRect(rect.left,rect.bottom,point.x,point.y,areaPaint);
                canvas.drawRect(rect.left-1,rect.top-1,rect.right+1,rect.bottom+1,linePaint);
                Log.d(TAG, "onDraw rect.top!=0:rect.left" + rect.left + "..rect.bottom:" + rect.bottom + "..point.x:" + point.x + "..point.y:" + point.y);
            }

            //进行判断矩形的宽度和高度是不是以固定的比例:0.7
            if(((double)rect.height()/(double)rect.width())>heghtDividWidthRate){
                //比例>0.7,进行算出剩余的部分
                leftLength=((float)rect.height()-((float)rect.width()*heghtDividWidthRate))/2.0f;
                //进行绘制出上下灰色的部分:
                Log.d(TAG,"rect.left:"+rect.left+"..rect.top:"+rect.top+"..rect.right:"+rect.right+"..rect.bottom:"+rect.bottom+"..leftLength:"+leftLength);
                canvas.drawRect(rect.left, rect.top,rect.right,rect.top + leftLength, grayPaint);
                canvas.drawRect(rect.left, rect.bottom-leftLength,rect.right,rect.bottom, grayPaint);
            }
        }

        if(isCircleEnable){
            //如果能够画图
            mPaintTransparent=new Paint();
            mPaintTransparent.setAntiAlias(true);
            mPaintTransparent.setFilterBitmap(true);
            mPaintTransparent.setColor(UiUtils.getInstance().getContext().getResources().getColor(R.color.gold_check_bg));
            mPaintTransparent.setStyle(Paint.Style.FILL);

            Path path = new Path();
            path.addRect(rect.left, rect.top, rect.right, rect.bottom,
                    Path.Direction.CW);
            path.addCircle(( rect.right+rect.left ) / 2,
                    (rect.bottom+rect.top) / 2,
                    Math.min((rect.right - rect.left) / 2,((rect.bottom - rect.top-2*blackHeight) / 2.0f)-5), Path.Direction.CCW);
            canvas.drawPath(path, mPaintTransparent);
        }
        super.onDraw(canvas);
    }
}
