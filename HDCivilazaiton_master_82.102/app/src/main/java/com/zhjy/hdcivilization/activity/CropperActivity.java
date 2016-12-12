package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.DisplayUtil;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.ImageUtils;
import com.zhjy.hdcivilization.view.CropImageView;

/**
 * zhangyang 图片剪切的activity
 */
public class CropperActivity extends BaseActivity implements View.OnClickListener {
    CropImageView imageView;//切图的显示
    Button btn_complete,btn_cancel;
    public static String PORTRAIT_IMAGE="PORTRAIT_IMAGE";//头像

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout=R.layout.activity_cropper;
        super.onCreate(savedInstanceState);
    }

    boolean isPortrait;
    @Override
    protected void initViews() {
        isPortrait=getIntent().getBooleanExtra(PORTRAIT_IMAGE,false);
        imageView=(CropImageView)findViewById(R.id.iv_crop);
        if(isPortrait){
            imageView.setIsCircleEnable(true);
            imageView.invalidate();
        }else{
            imageView.setIsCircleEnable(false);
            imageView.invalidate();
        }
        btn_complete=(Button)findViewById(R.id.btn_complete);
        btn_cancel=(Button)findViewById(R.id.btn_cancel);
        Uri uri=getIntent().getData();
        Point point= DisplayUtil.getScreenPoint(this);
        Bitmap bitmap=ImageUtils.decodeBitmapWithOrientationMax(ImageUtils.getFilePathByFileUri(this,uri), point.x, point.y,isPortrait);

        imageView.setImageBitmap(bitmap);
        btn_complete.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
    }

    @Override
    protected void initInitevnts() {

    }

//    /**
//     * 取消按钮
//     * @param view
//     */
//    protected void cacelBtn(View view){
//
//    }

    /**
     * 图片剪切完成按钮
     * @param view
     */
    public void completeBtn(View view){
        //图片的剪切完成
        try {
            Bitmap rectBitmap=imageView.getRectBitmap();
            String filePath=FileUtils.saveBitmapPng(rectBitmap,100);
            //进行界面的消失
            Intent intent=new Intent();
            intent.getExtras().putString(MySubSuperviseActivity.FILE_PATH,filePath);
            setResult(MySubSuperviseActivity.ZOOM_RESULT_CODE, intent);
            finish();
        } catch (ContentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_complete:
                try {
                    Bitmap rectBitmap;
                    String filePath;
                    if(isPortrait){
                        rectBitmap=imageView.getCroppedBitmap();
                        if(rectBitmap!=null){
                            filePath=FileUtils.saveBitmapPng(rectBitmap, 100);
                        }else{
                            return ;
                        }
                    }else{
                         rectBitmap=imageView.getRectBitmap();
                         if(rectBitmap!=null){
                             filePath=FileUtils.saveBitmapJPG(rectBitmap,80);
                         }else{
                             return;
                         }
                    }
                    System.out.println("btn_complete onClick: rectBitmap.height:"+rectBitmap.getHeight()+"..rectBitmap.width:"+rectBitmap.getWidth());
                    //进行界面的消失
                    Intent intent=new Intent();
                    Bundle bundle=new Bundle();
                    bundle.putString(MySubSuperviseActivity.FILE_PATH, filePath);
                    intent.putExtras(bundle);
                    setResult(MySubSuperviseActivity.ZOOM_RESULT_CODE, intent);
                    finish();
                } catch (ContentException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btn_cancel:
                finish();
                break;
        }

    }
}
