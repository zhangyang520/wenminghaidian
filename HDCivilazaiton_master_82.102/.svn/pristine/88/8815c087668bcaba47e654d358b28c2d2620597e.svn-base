package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.inner.BaseActivity;

/**
 * @author :huangxianfeng on 2016/8/15.
 * 登录成功界面
 */
public class LoginSuccessActivity extends BaseActivity implements View.OnClickListener {


    TextView perfectInfo;
    Button i_known,applyVolunteer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout= R.layout.activity_login_success;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        perfectInfo = (TextView)findViewById(R.id.perfect);
        i_known = (Button)findViewById(R.id.btn_i_known);
        applyVolunteer = (Button)findViewById(R.id.btn_apply_volunteer);

    }

    @Override
    protected void initInitevnts() {
        perfectInfo.setOnClickListener(this);
        i_known.setOnClickListener(this);
        applyVolunteer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.perfect:
                Intent intent = new Intent(LoginSuccessActivity.this,PersonalInfoActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.btn_i_known:
                finish();
                break;
            case R.id.btn_apply_volunteer:
                Intent applyIntent = new Intent(LoginSuccessActivity.this,VolunteerSignUpFormActivity.class);
                startActivity(applyIntent);
                finish();
                break;
        }

    }
}
