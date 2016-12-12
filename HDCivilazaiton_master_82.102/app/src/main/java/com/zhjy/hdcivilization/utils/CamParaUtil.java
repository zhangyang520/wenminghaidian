package com.zhjy.hdcivilization.utils;

import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CamParaUtil {
	private static final String TAG = "YanZi";
	private CameraSizeComparator sizeComparator = new CameraSizeComparator();
	private static CamParaUtil myCamPara = null;
	private CamParaUtil(){

	}
	public static CamParaUtil getInstance(){
		if(myCamPara == null){
			myCamPara = new CamParaUtil();
			return myCamPara;
		}
		else{
			return myCamPara;
		}
	}

	public  Size getPropPreviewSize(List<Size> list, float th){
		Collections.sort(list, sizeComparator);

		int i = 0;
		ArrayList<Size> datas=new ArrayList<Size>();
		for(Size s:list){
			if(equalRate(s, th)){
				datas.add(s);
				Log.i(TAG, "PreviewSize:w = " + s.width + "h = " + s.height);
//				break;
			}
			i++;
		}
		if(datas.size()==0){
			i = 0;//如果没有，没有找到使用最小的
			Log.i(TAG, "getPropPreviewSize : w = " + list.get(i).width + "h = " + list.get(i).height);
			return list.get(i);
		}else{
			Log.i(TAG, "getPropPreviewSize : w = " + datas.get(datas.size() - 1).width + "h = " +datas.get(datas.size()-1).height);
			return datas.get(datas.size()-1);
		}
	}
	public Size getPropPictureSize(List<Size> list, float th){
		Collections.sort(list, sizeComparator);

		ArrayList<Size> datas=new ArrayList<Size>();
		int i = 0;
		for(Size s:list){
			if(equalRate(s, th)){
				Log.i(TAG, "PictureSize : w = " + s.width + "h = " + s.height);
//				break;
				datas.add(s);
			}
			i++;
		}
//		if(i == list.size()){
//			i = 0;//如果没有，没有找到使用最小的
//		}
//		Log.i(TAG, "getPropPictureSize : w = " + list.get(i).width + "h = " + list.get(i).height);
//		return list.get(i);

		if(datas.size()==0){
			i = 0;//如果没有，没有找到使用最小的
			Log.i(TAG, "getPropPreviewSize : w = " + list.get(i).width + "h = " + list.get(i).height);
			return list.get(i);
		}else{
			Log.i(TAG, "getPropPreviewSize : w = " + datas.get(datas.size() - 1).width + "h = " +datas.get(datas.size()-1).height);
			return datas.get(datas.size()-1);
		}
	}

	public boolean equalRate(Size s, float rate){
//		float r = (float)(s.width)/(float)(s.height);
		float r = (float)(s.height)/(float)(s.width);
		if(Math.abs(r - rate) <= 0.03)
		{
			return true;
		}
		else{
			return false;
		}
	}

	public  class CameraSizeComparator implements Comparator<Size>{
		public int compare(Size lhs, Size rhs) {
			// TODO Auto-generated method stub
			if(lhs.width == rhs.width){
				return 0;
			}
			else if(lhs.width > rhs.width){
				return 1;
			}
			else{
				return -1;
			}
		}

	}

	/**��ӡ֧�ֵ�previewSizes
	 * @param params
	 */
	public  void printSupportPreviewSize(Camera.Parameters params){
		List<Size> previewSizes = params.getSupportedPreviewSizes();
//		Collections.sort(previewSizes, sizeComparator);
		for(int i=0; i< previewSizes.size(); i++){
			Size size = previewSizes.get(i);
			Log.i(TAG, "printSupportPreviewSize:width = "+size.width+" height = "+size.height+"..rate:"+((double)size.height/(double)size.width));
		}
	
	}

	/**��ӡ֧�ֵ�pictureSizes
	 * @param params
	 */
	public  void printSupportPictureSize(Camera.Parameters params){
		List<Size> pictureSizes = params.getSupportedPictureSizes();
//		Collections.sort(pictureSizes, sizeComparator);
		for(int i=0; i< pictureSizes.size(); i++){
			Size size = pictureSizes.get(i);
			Log.i(TAG, "printSupportPictureSize:width = "+ size.width
					+" height = " + size.height+"..rate:"+((double)size.height/(double)size.width));
		}
	}
	/**��ӡ֧�ֵľ۽�ģʽ
	 * @param params
	 */
	public void printSupportFocusMode(Camera.Parameters params){
		List<String> focusModes = params.getSupportedFocusModes();
		for(String mode : focusModes){
			Log.i(TAG, "focusModes--" + mode);
		}
	}
}
