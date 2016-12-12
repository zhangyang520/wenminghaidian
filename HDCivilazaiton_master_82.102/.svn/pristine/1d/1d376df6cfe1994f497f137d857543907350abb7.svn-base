package com.zhjy.hdcivilization.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * Created by zhangyang on 2016/8/15.
 *
 * 思路:
 *     1:获取这个CropImageView对象的宽度和高度,
 *       以及传递进来的Bitmap对应的宽度和高度
 *       对这个Bitmap对应的伸缩,绘制矩形框(背景颜色框)
 *
 *     2:下一步进行移动时,进行拖动这个移动框！
 */
public class CropImageView extends ImageView{

    private final String TAG="CropImageView";
    private float imageWidth,imageHeight;//image的外部长度和高度

    private RectF imageRect,frameRect;//图片的外部矩形 和 图形选择框frame的矩形
    private float scale;
    private PointF mCenter = new PointF();//中心点
    private Matrix matrix=new Matrix();
    private Paint mPaintBitmap,mPaintTransparent,mPaintFrame;
    private Context context;
    private TouchArea mTouchArea = TouchArea.OUT_OF_BOUNDS;//触摸的区域的类型
    private float mLastX, mLastY;

    private boolean isCircleEnable;//是否画圆

    public boolean isCircleEnable() {
        return isCircleEnable;
    }

    public void setIsCircleEnable(boolean isCircleEnable) {
        this.isCircleEnable = isCircleEnable;
    }

    public CropImageView(Context context) {
        this(context, null);
    }

    public CropImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public CropImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 进行初始化view
     */
    private void initView(){
        this.context=context;
        mPaintBitmap = new Paint();
        mPaintBitmap.setFilterBitmap(true);

        mPaintTransparent = new Paint();
        mPaintFrame = new Paint();
    }
    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);

        //进行初始化布局
    }

    /**
     * 布局调用方法
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        System.out.println(TAG + "  onLayout  (boolean changed, int left, int top, int right, int bottom...");
        int viewWidth=right-left-getPaddingLeft()-getPaddingRight();
        int viewHeight=bottom-top-getPaddingTop()-getPaddingBottom();
        initLayout(viewWidth, viewHeight);
    }

    /**
     * 绘图方法
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 不进行平移和缩放
         */
//        setMatrix();
//        Matrix matrix=new Matrix();
//        matrix.postConcat(this.matrix);
//        canvas.drawBitmap(getBitmap(),matrix,mPaintBitmap);

        //进行绘制framePaint
        drawFrame(canvas);
    }



    /**
     * 进行绘制frame层
     * @param canvas
     */
    private void drawFrame(Canvas canvas) {
        mPaintTransparent.setFilterBitmap(true);
        mPaintTransparent.setColor(Color.parseColor("#AA1C1C1C"));
        mPaintTransparent.setStyle(Paint.Style.FILL);


        mPaintFrame.setAntiAlias(true);
        mPaintFrame.setFilterBitmap(true);
        mPaintFrame.setStyle(Paint.Style.STROKE);
        mPaintFrame.setColor(Color.parseColor("#4BCBBC"));
        mPaintFrame.setStrokeWidth(UiUtils.getDimen(R.dimen.rect_line_width));

        if(isCircleEnable){
           //进行画圆
            Path path=new Path();
            path.addRect(imageRect, Path.Direction.CW);
            path.addCircle(circleRect.centerX(), circleRect.centerY(), circleRect.width()/2, Path.Direction.CCW);
            canvas.drawPath(path,mPaintTransparent);

            //画圆线:
            canvas.drawCircle(circleRect.centerX(), circleRect.centerY(),circleRect.width()/2-2,
                    mPaintFrame);
        }else{
            //进行绘制顶上的rect
            canvas.drawRect(imageRect.left, imageRect.top, imageRect.right, frameRect.top,
                    mPaintTransparent);
            //进行获取底部的rect
            canvas.drawRect(imageRect.left, frameRect.bottom, imageRect.right, imageRect.bottom,
                    mPaintTransparent);
            //左侧的rect
            canvas.drawRect(imageRect.left, frameRect.top, frameRect.left, frameRect.bottom,
                    mPaintTransparent);
            //右侧的rect
            canvas.drawRect(frameRect.right, frameRect.top, imageRect.right, frameRect.bottom,
                    mPaintTransparent);
            //进行绘制线条
            canvas.drawRect(frameRect.left, frameRect.top, frameRect.right, frameRect.bottom,
                    mPaintFrame);
        }
    }


    private Bitmap getBitmap() {
        Bitmap bm = null;
        Drawable d = getDrawable();
        if (d != null && d instanceof BitmapDrawable) bm = ((BitmapDrawable) d).getBitmap();
        return bm;
    }
    /**
     * 进行设置矩阵
     */
    private void setMatrix(){
        matrix.reset();
        System.out.println("setMatrix: (float)(mCenter.x-(imageWidth*scale*0.5):" + (float) (mCenter.x - (imageWidth * scale * 0.5)) +
                "...(float)(mCenter.y-(imageHeight*scale*0.5)):" + (float) (mCenter.y - (imageHeight * scale * 0.5)));
        matrix.preTranslate((float) (mCenter.x - (imageWidth * scale * 0.5)), (float) (mCenter.y - (imageHeight * scale * 0.5)));
        matrix.postScale(scale, scale, mCenter.x, mCenter.y);
    }
    /**
     * 进行初始化布局：
     *    1:进行比较得出缩放大小
     *       关键是:
     *    2:设置中心点和缩放值
     *    3:进行初始化imageRect和frameRect
     * @param viewWidth
     * @param viewHeight
     */
    private void initLayout(int viewWidth,int viewHeight){
        if(getDrawable()!=null){
            imageWidth=getDrawable().getIntrinsicWidth();
            imageHeight=getDrawable().getIntrinsicHeight();
            System.out.println(TAG+" initLayout getDrawable()!=null viewWidth:"+viewWidth+"..viewHeight:"+viewHeight+"...imageWidth:"+imageWidth+"...imageHeight:"+imageHeight);
        }else{
            System.out.println(TAG + "  initLayout getDrawable()==null  viewWidth:" + viewWidth + "..viewHeight:" + viewHeight);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        final int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        System.out.println(TAG+"....onMeasure viewWidth:"+viewWidth+"...viewHeight:"+viewHeight);

        //
        if(getDrawable()!=null){
            imageWidth=getDrawable().getIntrinsicWidth();
            imageHeight=getDrawable().getIntrinsicHeight();
            System.out.println(TAG+" onMeasure getDrawable()!=null viewWidth:"+viewWidth+"..viewHeight:"+viewHeight+"...imageWidth:"+imageWidth+"...imageHeight:"+imageHeight);

            if(imageWidth<0)
                imageWidth=viewWidth;

            if(imageHeight<0)
                imageHeight=viewHeight;

            //进行计算缩放比:放大:取小值, 缩小:取小值
            float w=(float)viewWidth;
            float h=(float)viewHeight;

            float viewRate=w/h;
            float imgRate=imageWidth/imageHeight;
            float scale;
            if(viewRate>=imgRate){
                scale=h/imageHeight;
            }else{
                scale=w/imageWidth;
            }

            //进行设置scale
            setScale(scale);
            //进行设置中心点
            setmCenter(new PointF((int)(viewWidth*0.5),(int)(viewHeight*0.5)));
            //进行初始化frame
            initRect(viewWidth,viewHeight);
//           setMeasuredDimension((int)(imageWidth*scale), (int) (imageHeight*scale));
//            setMeasuredDimension((int)(imageWidth*scale), viewHeight);
            setMeasuredDimension(viewWidth, viewHeight);
        }else{
            System.out.println(TAG+"  onMeasure getDrawable()==null  viewWidth:"+viewWidth+"..viewHeight:"+ viewHeight);
            setMeasuredDimension(viewWidth, viewHeight);
        }
    }

    RectF circleRect=new RectF();
    /**
     * 进行初始化rect
     * @param viewWidth
     * @param viewHeight
     */
    private void initRect(int viewWidth, int viewHeight){
        //进行初始化imageRect
        float imageLeft=viewWidth*0.5f-imageWidth*getScale()*0.5f;
        float imageTop=viewHeight*0.5f-imageHeight*getScale()*0.5f;
        float imageRight=viewWidth*0.5f+imageWidth*getScale()*0.5f;
        float imageBottom=viewHeight*0.5f+imageHeight*getScale()*0.5f;
        imageRect=new RectF(imageLeft,imageTop,imageRight,imageBottom);

        float frameRectWidth;
        float frameRectHeight;
        //进行初始化frameRect
        float  frameLeft=imageLeft;
        float  frameRight=imageRight;
        if(imageWidth*getScale()*MaskView.heghtDividWidthRate>=imageRect.height()){
            frameRectHeight=imageRect.height();
            frameRectWidth=frameRectHeight/MaskView.heghtDividWidthRate;
            frameLeft=(imageRight-frameRectWidth)/2;
            frameRight=frameRectWidth+frameLeft;
        }else{
             frameRectWidth=imageWidth*getScale();
             frameRectHeight=imageWidth*getScale()*MaskView.heghtDividWidthRate;
        }

        float  frameTop=viewHeight*0.5f-frameRectHeight*0.5f;
        float  frameBottom=viewHeight*0.5f+frameRectHeight*0.5f;
        frameRect=new RectF(frameLeft,frameTop,frameRight,frameBottom);

        //如果是圆形
        if(isCircleEnable()){
            //进行初始化圆形
            int radius=Math.round(Math.min((int)(frameRect.width()/2),(int)(frameRect.height()/2)));
            System.out.println("init frameRect toString:"+frameRect.toString()+"...radius:"+radius+"..frameRect.centerX():"+frameRect.centerX()+"...frameRect.centerY():"+frameRect.centerY());
            circleRect.set((int)(frameRect.centerX() - radius),(int)(frameRect.centerY() - radius),(int)(frameRect.centerX() + radius), (int)(frameRect.centerY() + radius));
            System.out.println("init circleRect toString:" + circleRect.toString() + "...radius:" + radius);
        }
    }

    /**
     * 进行触摸事件的进行
     * down:
     *    记录点,
     *    进行更新触摸区域的类型
     * move：
     *    进行移动:
     *       对frameRect进行移动,但是需要进行检测移动的范围
     *
     * up：
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                if (mTouchArea != TouchArea.OUT_OF_BOUNDS) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onCancel();
                return true;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                onUp(event);
                return true;
        }
        return false;
    }

    /**
     * 按下的事件
     * @param
     */
    private void onDown(MotionEvent e) {
        mLastX = e.getX();
        mLastY = e.getY();
        checkTouchArea(e.getX(), e.getY());
    }

    private void checkTouchArea(float x, float y) {
        if (isInsideFrame(x, y)){
            mTouchArea=TouchArea.CENTER;
            return;
        }
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
    }

    /**
     * 是否在frame中内
     * @param x
     * @param y
     * @return
     */
    private boolean isInsideFrame(float x, float y) {
        if (frameRect.left <= x && frameRect.right >= x) {
            if (frameRect.top <= y && frameRect.bottom >= y) {
                mTouchArea = TouchArea.CENTER;
                return true;
            }
        }
        return false;
    }

    /**
     * 移动的事件
     * @param
     */
    private void onMove(MotionEvent e) {
        float diffX = e.getX() - mLastX;
        float diffY = e.getY() - mLastY;
        switch (mTouchArea) {
            case CENTER:
                moveFrame(diffX, diffY);
                break;
            case OUT_OF_BOUNDS:
                break;
        }
        invalidate();
        mLastX = e.getX();
        mLastY = e.getY();
    }

    /**
     * 向上的事件
     * @param
     */
    private void onUp(MotionEvent e) {
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
    }

    private void moveFrame(float x, float y){
        if(isCircleEnable()){
            circleRect.left+=x;
            circleRect.right+=x;
            circleRect.top+=y;
            circleRect.bottom+=y;
        }else{
            frameRect.left += x;
            frameRect.right += x;
            frameRect.top += y;
            frameRect.bottom += y;
        }

        checkMoveBounds();
    }

    private void checkMoveBounds() {
        if(isCircleEnable()){
            float diff = circleRect.left - imageRect.left;
            if (diff < 0) {
                circleRect.left -= diff;
                circleRect.right -= diff;
            }
            diff = circleRect.right - imageRect.right;
            if (diff > 0) {
                circleRect.left -= diff;
                circleRect.right -= diff;
            }
            diff = circleRect.top - imageRect.top;
            if (diff < 0) {
                circleRect.top -= diff;
                circleRect.bottom -= diff;
            }
            diff = circleRect.bottom - imageRect.bottom;
            if (diff > 0) {
                circleRect.top -= diff;
                circleRect.bottom -= diff;
            }
        }else{
            float diff = frameRect.left - imageRect.left;
            if (diff < 0) {
                frameRect.left -= diff;
                frameRect.right -= diff;
            }
            diff = frameRect.right - imageRect.right;
            if (diff > 0) {
                frameRect.left -= diff;
                frameRect.right -= diff;
            }
            diff = frameRect.top - imageRect.top;
            if (diff < 0) {
                frameRect.top -= diff;
                frameRect.bottom -= diff;
            }
            diff = frameRect.bottom - imageRect.bottom;
            if (diff > 0) {
                frameRect.top -= diff;
                frameRect.bottom -= diff;
            }
        }
    }
    /**
     * 取消
     */
    private void onCancel(){
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
    }

    /**
     * Get cropped image bitmap
     *
     * @return cropped image bitmap
     */
    public Bitmap getCroppedBitmap() {
        Bitmap source = getBitmap();
        if (source == null) return null;

        System.out.println("getCroppedBitmap circleRect toStirng:"+circleRect.toString()+"..source width:"+source.getWidth()+"..height:"+source.getHeight()+"...scale:"+scale);
        int x, y, w, h;
        float l = (circleRect.left / scale);
        float t = (circleRect.top / scale);
        float r = (circleRect.right / scale);
        float b = (circleRect.bottom / scale);
        x = Math.round(l - (circleRect.left / scale));
        y = Math.round(t - (circleRect.top / scale));
        w = Math.round(r - l);
        h = Math.round(b - t);

        if (x + w > source.getWidth()) {
            w = source.getWidth() - x;
        }
        if (y + h > source.getHeight()) {
            h = source.getHeight() - y;
        }

        System.out.println("(int)circleRect.left:"+(int)circleRect.left+"...(int)circleRect.top:"+(int)circleRect.top+"...circleRect.right:"+((int)circleRect.left+(int)circleRect.height())+"...circleRect.bottom:"+((int)circleRect.top+(int)circleRect.height()));
        Bitmap cropped = Bitmap.createBitmap(source, (int)(circleRect.left/scale), (int)((circleRect.top-imageRect.top)/scale),(int)(circleRect.height()/scale), (int)(circleRect.height()/scale), null, false);
        if (!isCircleEnable()) return cropped;
        return getCircularBitmap(cropped);
    }

    /**
     * Crop the square image in a circular
     *
     * @param square image bitmap
     * @return circular image bitmap
     */
    public Bitmap getCircularBitmap(Bitmap square) {
        if (square == null) return null;
        System.out.println("getCircularBitmap square :width:"+square.getWidth()+"..height:"+square.getHeight()+"...scale:"+scale);
        Bitmap output = Bitmap.createBitmap(square.getWidth(), square.getHeight(),
                Bitmap.Config.ARGB_4444);

        final Rect rect = new Rect(0, 0, square.getWidth(), square.getHeight());
        Canvas canvas = new Canvas(output);

        int halfWidth = square.getWidth() / 2;
        int halfHeight = square.getHeight() / 2;
        int radius=Math.min(halfWidth, halfHeight);
//        final RectF rectDest = new RectF(0, 0,radius, radius);
        //进行绘制其他路线:
//        Path path=new Path();
//        path.addRect(rectDest,Path.Direction.CW);
//        path.addCircle(rectDest.centerX(),rectDest.centerY(),radius, Path.Direction.CCW);
//        final Paint paint1 = new Paint();
//        paint1.setAntiAlias(true);
//        paint1.setFilterBitmap(true);
//        paint1.setColor(Color.WHITE);
//        canvas.drawPath(path,paint1);

//        final Paint paint = new Paint();
//        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);
//        paint.setAlpha(255);
//        canvas.drawCircle(halfWidth, halfHeight, radius, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(square, rect, rect, paint);

        int color = 0xff424242;
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
//        canvas.drawRoundRect(rectF, radius, radius, paint);
        canvas.drawCircle(halfWidth, halfHeight, radius, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(square, rect, rect, paint);

        return output;
    }

    /**
     * Get cropped rect image bitmap
     * <p/>
     * This method always returns rect image.
     * (If you need a square image with CropMode.CIRCLE, you can use this method.)
     *
     * @return cropped image bitmap
     */
    public Bitmap getRectBitmap() {
        Bitmap source = getBitmap();
        if (source == null) return null;
        System.out.println("getCroppedBitmap circleRect toStirng:"+circleRect.toString()+"..source width:"+source.getWidth()+"..height:"+source.getHeight()+"...scale:"+scale);

        int x, y, w, h;
        float l = (frameRect.left / scale);
        float t = (frameRect.top / scale);
        float r = (frameRect.right / scale);
        float b = (frameRect.bottom / scale);
        x = Math.round(l - (imageRect.left / scale));
        y = Math.round(t - (imageRect.top / scale));
        w = Math.round(r - l);
        h = Math.round(b - t);

        if (x + w > source.getWidth()) {
            w = source.getWidth() - x;
        }
        if (y + h > source.getHeight()) {
            h = source.getHeight() - y;
        }
        return Bitmap.createBitmap(source, x, y, w, h, null, false);
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public PointF getmCenter() {
        return mCenter;
    }

    public void setmCenter(PointF mCenter) {
        this.mCenter = mCenter;
    }

    private enum TouchArea {
        OUT_OF_BOUNDS, CENTER, LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM;
    }
}
