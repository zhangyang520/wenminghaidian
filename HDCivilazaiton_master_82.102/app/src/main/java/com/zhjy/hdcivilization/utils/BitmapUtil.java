
package com.zhjy.hdcivilization.utils;

import java.io.File;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.widget.ImageView;

import com.lidroid.xutils.BitmapUtils;
import com.zhjy.hdcivilization.R;


public class BitmapUtil {


    private static BitmapUtil instance;
    public static BitmapUtils bitmapUtils;
    BitmapUtils myInstance;
    private BitmapUtil() {

    }

    public static BitmapUtil getInstance() {
        if (instance == null) {
            synchronized (BitmapUtil.class) {
                if (instance == null) {
                    instance = new BitmapUtil();
                    bitmapUtils = new BitmapUtils(UiUtils.getInstance().getContext(), SDCardUtil.getInstance().getIconPath());
                    /***给显示图片的ImagView加载失败设置缺省图片**/
                    bitmapUtils.configDefaultLoadFailedImage(R.drawable.real_horizontal_default);
                    /***给显示图片的ImagView正在加载中设置的缺省图片**/
                    bitmapUtils.configDefaultLoadingImage(R.drawable.real_horizontal_default);
                }
            }
        }
        return instance;
    }


    /**
     * @param context
     * @param resId
     * @param width   改变后的图片像素宽度
     * @param height  改变后的图片像素高度
     * @return 返回指定大小的位图
     */


    public Bitmap getAbsImage(Context context, int resId, int width,
                              int height) {

        Options opts = new Options();
        // 不解析像素信息，只解析图片宽高，那么就不会申请内存去保存像素信息
        opts.inJustDecodeBounds = true;
        InputStream is = context.getResources().openRawResource(resId);
        // api会解析图片的所有像素信息，把像素信息保存在内存中
        BitmapFactory.decodeStream(is, null, opts);

        opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
        opts.outHeight = height;
        opts.outWidth = width;
        // opts.inSampleSize = 大的图片宽 / 小的图片宽 500 / 100 = 5 那么就是5分之一
        opts.inJustDecodeBounds = false;
        // 按照缩小后的比例来解析像素
        Bitmap bm = BitmapFactory.decodeStream(is, null, opts);

        return bm;

    }


    /**
     * 一个快速减少图片占用内存的方法~
     *
     * @param context
     * @param resId   资源id
     * @param scale   缩小为原图片的多少倍,比如该值为4 ,这该图片为原图片的1/4
     * @return
     * @author zh
     */


    public Bitmap readBitMap(Context context, int resId, int scale) {
        Options opt = new Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        opt.inSampleSize = (int) Math.sqrt(scale);
        // 获取资源图片
        InputStream is = context.getResources().openRawResource(resId);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /**
     * @param options   位图的宽高信息等
     * @param reqWidth  希望得到的位图宽
     * @param reqHeight 希望得到的位图高
     * @return
     */


    @SuppressWarnings("unused")
    public int calculateInSampleSize(Options options,
                                     int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    /**
     * 进行展示图片
     *
     * @param imageView
     * @param url
     */

    public void displayImg(ImageView imageView, String url) {
        bitmapUtils.display(imageView, url);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.real_horizontal_default);
        /***给显示图片的ImagView正在加载中设置的缺省图片**/
        bitmapUtils.configDefaultLoadingImage(R.drawable.real_horizontal_default);
    }


    /**
     *  头像
     * @param imageView
     * @param url
     */

    public void displayImgPic(ImageView imageView, String url) {
        bitmapUtils.display(imageView, url);
        bitmapUtils.configDefaultLoadFailedImage(R.drawable.user_pic_defaut);
        /***给显示图片的ImagView正在加载中设置的缺省图片**/

        bitmapUtils.configDefaultLoadingImage(R.drawable.user_pic_defaut);
    }


    public void displayUserPic(ImageView imageView, String url){
        if(myInstance==null){
            myInstance = new BitmapUtils(UiUtils.getInstance().getContext(), SDCardUtil.getInstance().getIconPath());
        }
        /***给显示图片的ImagView加载失败设置缺省图片**/
        myInstance.configDefaultLoadFailedImage(R.drawable.user_pic_defaut);
        /***给显示图片的ImagView正在加载中设置的缺省图片**/
        myInstance.configDefaultLoadingImage(R.drawable.user_pic_defaut);
        myInstance.display(imageView,url);
    }
    public void displayImg(ImageView imageView, String path, int failedImgId) {
        bitmapUtils.display(imageView, path);
        bitmapUtils.configDefaultLoadFailedImage(failedImgId);
/***给显示图片的ImagView正在加载中设置的缺省图片**/

        bitmapUtils.configDefaultLoadingImage(failedImgId);
    }

    public void clearCache(String url) {
        bitmapUtils.clearMemoryCache(url);
        bitmapUtils.clearCache(url);
        bitmapUtils.clearDiskCache(url);
    }

    /**
     * 得到本地磁盘当中缓存的图片文件（可以判断缓存的文件是否存在）
     *
     * @param url
     * @return
     */

    public File getBitmapFileCache(String url) {
        File file = bitmapUtils.getBitmapFileFromDiskCache(url);
        return file;
    }

    public static void setInstance(BitmapUtil instance) {
        BitmapUtil.instance = instance;
    }

    public BitmapUtils getBitmapUtils() {
        return bitmapUtils;
    }

    public void setBitmapUtils(BitmapUtils bitmapUtils) {
        BitmapUtil.bitmapUtils = bitmapUtils;
    }
}
