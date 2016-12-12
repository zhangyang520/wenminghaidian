package com.zhjy.hdcivilization.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.inner.BaseActivity;

/**
 * @author :huangxianfeng on 2016/8/4.
 * 发布者信息管理类
 */
public class SubInfoActivity extends BaseActivity {

    private ImageView btnBack;
    private RelativeLayout rl_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_sub_info;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack = (ImageView)findViewById(R.id.btn_back);
        rl_back = (RelativeLayout)findViewById(R.id.rl_back);

    }

    @Override
    protected void initInitevnts() {
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
