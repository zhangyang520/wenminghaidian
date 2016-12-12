package com.zhjy.hdcivilization.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * 图片简单处理工具类
 */
public class ImageUtils {
	
	/**
	 * 屏幕宽
	 * 
	 * @param context
	 * @return
	 */
	public static int getWidth(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.widthPixels;
	}

	/**
	 * 屏幕高
	 * 
	 * @param context
	 * @return
	 */
	public static int getHeight(Context context) {
		DisplayMetrics dm = context.getResources().getDisplayMetrics();
		return dm.heightPixels;
	}
	
	/**
	 * 根据文件Uri获取路径
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
	public static String getFilePathByFileUri(Context context, Uri uri) {
		String filePath = null;
		Cursor cursor = context.getContentResolver().query(uri, null, null,
				null, null);
		if (cursor!=null && cursor.moveToFirst()) {
			//如果从相册中获取的,
			filePath = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA));
			cursor.close();
		}else{
			//如果从本地文件中获取的
			filePath=uri.getPath();
		}
		return filePath;
	}

	/**
	 * 根据图片原始路径获取图片缩略图
	 * 
	 * @param imagePath 图片原始路径
	 * @param width		缩略图宽度
	 * @param height	缩略图高度
	 * @return
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;//不加载直接获取Bitmap宽高
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		if(bitmap == null){
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;
		int beWidth = w / width;
		int beHeight = h / height;
		int rate = 1;
		if (beWidth < beHeight) {
			rate = beWidth;
		} else {
			rate = beHeight;
		}
		if (rate <= 0) {//图片实际大小小于缩略图,不缩放
			rate = 1;
		}
		options.inSampleSize = rate;
		options.inJustDecodeBounds = false; 
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		}
		return bitmap;
	}

	/**
	 * ��תBitmap
	 * @param b
	 * @param rotateDegree
	 * @return
	 */
	public static Bitmap getRotateBitmap(Bitmap b, float rotateDegree){
		Matrix matrix = new Matrix();
		matrix.postRotate((float)rotateDegree);
		Bitmap rotaBitmap = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, false);
		return rotaBitmap;
	}

	public static Bitmap decodeBitmapWithOrientationMax(String pathName, int width, int height, boolean isPortrait) {
		return decodeBitmapWithSize(pathName, width, height, true,isPortrait);
	}

	private static Bitmap decodeBitmapWithSize(String pathName, int width, int height,
											   boolean useBigger, boolean isPortrait) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		options.inInputShareable = true;
		options.inPurgeable = true;
		BitmapFactory.decodeFile(pathName, options);

//		int decodeWidth = width, decodeHeight = height;
//		final int degrees = getImageDegrees(pathName);
//		if (degrees == 90 || degrees == 270) {
//			decodeWidth = height;
//			decodeHeight = width;
//		}
//
//		if (useBigger) {
//			options.inSampleSize = (int) Math.min(((float) options.outWidth / decodeWidth),
//					((float) options.outHeight / decodeHeight));
//		} else {
//			options.inSampleSize = (int) Math.max(((float) options.outWidth / decodeWidth),
//					((float) options.outHeight / decodeHeight));
//		}
//
//		options.inJustDecodeBounds = false;
//		Bitmap sourceBm = BitmapFactory.decodeFile(pathName, options);
//		return imageWithFixedRotation(sourceBm, degrees);
		int targetDensity = UiUtils.getInstance().getContext().getResources().getDisplayMetrics().densityDpi;
		DisplayMetrics dm = new DisplayMetrics();
		WindowManager windowManager=(WindowManager)UiUtils.getInstance().//
										getContext().getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(dm);
//		int x = dm.widthPixels;
//		int y = dm.heightPixels;

		int x=600;
		int y=800;
		options.inSampleSize = calculateInSampleSize(options, x, y);
		double xSScale = ((double)options.outWidth) / ((double)x);
		double ySScale = ((double)options.outHeight) / ((double)y);
		double startScale = xSScale > ySScale ? xSScale : ySScale;

		Log.d("MainActivity", "startScale:" + startScale);
		options.inScaled = true;
//		options.inDensity = (int) (targetDensity*startScale);
		//防止分辨率过小
		if(options.outHeight*options.outWidth<x*y){
			options.inDensity = (int) (targetDensity);//乘以一个固定值
		}else{
			if(isPortrait){
				//进行判断范围 像素
				options.inDensity = (int) (targetDensity*2);//乘以一个固定值
			}else{
				options.inDensity = (int) (targetDensity*1.5);//乘以一个固定值
			}

		}
		options.inTargetDensity = targetDensity;
		options.inJustDecodeBounds = false;
		return  BitmapFactory.decodeFile(pathName, options);
	}

	/**
	 * 计算采样率的大小
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private static  int calculateInSampleSize(BitmapFactory.Options options,
									 int reqWidth, int reqHeight) {
		// Raw height and width of image
//		final int height = options.outHeight;
//		final int width = options.outWidth;
		int height;
		int width;
		if(options.outWidth>options.outHeight){
			height = options.outWidth;
			width = options.outHeight;
		}else{
			height = options.outHeight;
			width = options.outWidth;
		}
		int inSampleSize = 1;

		System.out.println("ImageUtils calculateInSampleSize height:"+height+"..width:"+width+"...reqHeight:"+reqHeight+"..reqWidth:"+reqWidth);
		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			// Calculate the largest inSampleSize value that is a power of 2 and
			// keeps both
			// height and width larger than the requested height and width.
			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}

			if(inSampleSize==1){
				inSampleSize=2;
			}
		}

		Log.d("MainActivity", "inSampleSize:" + inSampleSize);
		return inSampleSize;
	}

	public static int getImageDegrees(String pathName) {
		int degrees = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(pathName);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degrees = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degrees = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degrees = 270;
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return degrees;
	}

	public static Bitmap imageWithFixedRotation(Bitmap bm, int degrees) {
		if (bm == null || bm.isRecycled())
			return null;

		if (degrees == 0)
			return bm;

		final Matrix matrix = new Matrix();
		matrix.postRotate(degrees);
		Bitmap result = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
		if (result != bm)
			bm.recycle();
		return result;

	}
}
