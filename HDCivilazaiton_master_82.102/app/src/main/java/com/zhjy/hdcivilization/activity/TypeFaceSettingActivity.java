package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.SystemSettingDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.SystemSetting;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.WarningPopup;

/**
 * @author :huangxianfeng on 2016/8/29.
 * 设置字体大小
 *
 */
public class TypeFaceSettingActivity extends BaseActivity implements View.OnTouchListener, View.OnClickListener {

    private RelativeLayout rlLargeSize,rlSmall,rlInLarge,rl_back;
    private ImageView arrowLarge,arrowInLarge,arrowSmall;
    private ImageView btnBack;
    private String textSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView= UiUtils.getInstance().inflate(R.layout.activity_type_face_size_setting);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);
        rlLargeSize = (RelativeLayout)findViewById(R.id.rl_large);
        rlInLarge = (RelativeLayout)findViewById(R.id.rl_in_large);
        rlSmall = (RelativeLayout)findViewById(R.id.rl_small);

        arrowLarge = (ImageView)findViewById(R.id.arrow_large);
        arrowInLarge = (ImageView)findViewById(R.id.arrow_in_large);
        arrowSmall = (ImageView)findViewById(R.id.arrow_small);

        rl_back.setOnClickListener(this);
        rlLargeSize.setOnTouchListener(this);
        rlInLarge.setOnTouchListener(this);
        rlSmall.setOnTouchListener(this);

    }

    @Override
    protected void initInitevnts() {

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.rl_large:
                switch (event.getAction()){//字体大
                    case MotionEvent.ACTION_DOWN:
                        arrowLarge.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowLarge.setBackgroundResource(R.drawable.mine_arrow_right);
                        textSize=HDCivilizationConstants.LARGE;
                        setWarningPopup(textSize);
                        break;
                }

                break;
            case R.id.rl_in_large:
                switch (event.getAction()){//字体中
                    case MotionEvent.ACTION_DOWN:
                        arrowInLarge.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowInLarge.setBackgroundResource(R.drawable.mine_arrow_right);
                        textSize=HDCivilizationConstants.IN_LARGE;
                        setWarningPopup(textSize);
                        break;
                }

                break;
            case R.id.rl_small:
                switch (event.getAction()){//字体小
                    case MotionEvent.ACTION_DOWN:
                        arrowSmall.setBackgroundResource(R.drawable.mine_arrow_right_press);
                        break;
                    case MotionEvent.ACTION_UP:
                        arrowSmall.setBackgroundResource(R.drawable.mine_arrow_right);
                        textSize=HDCivilizationConstants.SMALL;
                        setWarningPopup(textSize);
                        break;
                }
                break;
        }
        return false;
    }

    //设置字体提示
    private void setWarningPopup(String textSize) {
        Intent intent = new Intent();
        intent.putExtra(HDCivilizationConstants.RESULT_CODE, textSize);
        setResult(HDCivilizationConstants.LOAD_RESULT_CODE, intent);
        finish();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.rl_back:
                finish();
                break;
        }
    }




}
