package com.zhjy.hdcivilization.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;


import com.tencent.mm.sdk.platformtools.SyncTask;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.ImageUtils;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * 多图上传,动态添加图片适配器
 */
public class UploadImageAdapter extends BaseAdapter {
	
	private ArrayList<String> imagePathList;
	private Context context;
	private boolean isAddData = false;
	/**
	 * 控制最多上传的图片数量
	 */
	private int imageNumber = 3;
    private GridView gridView;
    private int imageHeight;//图片的高度

	public GridView getGridView() {
		return gridView;
	}

	public void setGridView(GridView gridView) {
		this.gridView = gridView;
	}

	public int getImageNumber() {
		return imageNumber;
	}

	public void setImageNumber(int imageNumber) {
		this.imageNumber = imageNumber;
	}

	public UploadImageAdapter(Context context, ArrayList<String> imagePath) {
		this.context = context;
		this.imagePathList = imagePath;
	}

	public ArrayList<String> getImagePathList() {
		return imagePathList;
	}

	public void setImagePathList(ArrayList<String> imagePathList) {
		this.imagePathList = imagePathList;
	}

	public void update(ArrayList<String> imagePathList){
		this.imagePathList = imagePathList;
		//这里控制选择的图片放到前面,默认的图片放到最后面,
//		if(isAddData){
			//集合中的总数量等于上传图片的数量加上默认的图片不能大于imageNumber + 1
			if(imagePathList.size() == imageNumber + 1){
				//移除默认的图片
				imagePathList.remove(imagePathList.size()-1);
//				isAddData = false;
			}else if(imagePathList.size()<imageNumber){
				imagePathList.add(null);
//				isAddData = false;
			}
//		}else{
//			//添加默认的图片
//			imagePathList.add(null);
//			isAddData = true;
//		}

		//这里判断图片的个数是不是3的倍数值需要<=3
        if(gridView!=null && imageNumber/3>1){
			updateGridViewLayout(imagePathList);
		}
		notifyDataSetChanged();
	}

	/**
	 * 进行更新gridView的布局
	 * @param imagePathList
	 */
	private void updateGridViewLayout(ArrayList<String> imagePathList){
		System.out.println("UploadImageAdapter updateGridViewLayout gridViewHeight:"+gridView.getMeasuredHeight()+"...imageHeight:"+imageHeight);
		int lineCount=imageNumber/ HDCivilizationConstants.LINE_IMG_COUNT;
		int index=1;//角标
		for(int i=1;i<=lineCount;++i){
			if(imagePathList.size()<=i* HDCivilizationConstants.LINE_IMG_COUNT){
				index=i;
				break;
			}else if(i==lineCount){
				index=lineCount;
			}
		}

		System.out.println("updateGridViewLayout index:"+index+"..imageHeight:"+imageHeight);
		gridView.getLayoutParams().height=index*imageHeight+(index-1)*UiUtils.getDimen(R.dimen.grid_horizontal_space);
		gridView.requestLayout();
	}

	/**
	 * 进行更新"长按"删除
	 * @param imagePathList
	 */
	public void updateRemove(ArrayList<String> imagePathList){
			this.imagePathList = imagePathList;
			if (getListContentSize(imagePathList)==imageNumber-1) {
				imagePathList.add(null);
			}
			if(gridView!=null && imageNumber/3>1){
				updateGridViewLayout(imagePathList);
			}
			notifyDataSetChanged();
	}

	/**
	 * 进行获取list集合的长度
	 * @param imagePathList
	 * @return
	 */
	private int getListContentSize(ArrayList<String> imagePathList) {
		int i=0;
		for (String data:imagePathList){
			if(data!=null){
				i++;
			}
		}
		return i;
	}

	@Override
	public int getCount() {
		return imagePathList == null ? 0 : imagePathList.size();
	}

	@Override
	public Object getItem(int position) {
		return imagePathList == null ? null : imagePathList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return  position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView iv_image;
		if (convertView == null) {//创建ImageView
			iv_image = new ImageView(context);
			//ImageUtils.getWidth(context) / 3 -UiUtils.getDimen(R.dimen.grid_horizontal_space)
			int width=ImageUtils.getWidth(context) / 3 - UiUtils.getDimen(R.dimen.grid_horizontal_space);
			imageHeight=(int)(width*0.7);
			iv_image.setLayoutParams(new AbsListView.LayoutParams(width,imageHeight));
			iv_image.setScaleType(ImageButton.ScaleType.CENTER_CROP);
			convertView = iv_image;
		}else{
			iv_image = (ImageView) convertView;
		}
		if(getItem(position) == null){//图片地址为空时设置默认图片
			iv_image.setImageResource(R.drawable.supervise_upload_img);
		}else{
			//获取图片缩略图，避免OOM
			//ImageUtils.getWidth(context) / 3 -UiUtils.getDimen(R.dimen.grid_horizontal_space)
			int width=ImageUtils.getWidth(context) / 3 - UiUtils.getDimen(R.dimen.grid_horizontal_space);
			iv_image.setLayoutParams(new AbsListView.LayoutParams(width,(int)(width*0.7)));
			Bitmap bitmap = ImageUtils.getImageThumbnail((String)getItem(position), width ,(int)(width*0.7));
			iv_image.setImageBitmap(bitmap);
		}
		return convertView;
	}

}
