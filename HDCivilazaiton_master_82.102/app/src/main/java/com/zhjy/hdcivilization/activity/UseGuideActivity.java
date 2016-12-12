package com.zhjy.hdcivilization.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.UiUtils;
import com.zhjy.hdcivilization.view.WarningPopup;

/**
 * @author :huangxianfeng on 2016/9/17.
 * 使用指南界面
 */
public class UseGuideActivity extends BaseActivity implements View.OnClickListener {

    ImageView btnBack;
    TextView yewu_dail1,yewu_dail2,yewu_dail3,jishu_dail1;
//    View  line_1,line_2,line_3,line_4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        contentView = UiUtils.getInstance().inflate(R.layout.activity_use_guide);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        btnBack =(ImageView)findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        yewu_dail1 =(TextView)findViewById(R.id.yewu_dail1);
        yewu_dail1.setOnClickListener(this);
        yewu_dail2 =(TextView)findViewById(R.id.yewu_dail2);
        yewu_dail2.setOnClickListener(this);
        yewu_dail3 =(TextView)findViewById(R.id.yewu_dail3);
        yewu_dail3.setOnClickListener(this);
        jishu_dail1 =(TextView)findViewById(R.id.jishu_dail1);
        jishu_dail1.setOnClickListener(this);

//        line_1 =findViewById(R.id.line_1);
//        line_2 =findViewById(R.id.line_2);
//        line_3 =findViewById(R.id.line_3);
//        line_4 =findViewById(R.id.line_4);
//        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                contentView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
//                line_1.getLayoutParams().width=yewu_dail1.getMeasuredWidth();
//                line_1.requestLayout();
//
//                line_2.getLayoutParams().width=yewu_dail2.getMeasuredWidth();
//                line_2.requestLayout();
//
//                line_3.getLayoutParams().width=yewu_dail3.getMeasuredWidth();
//                line_3.requestLayout();
//
//                line_4.getLayoutParams().width=jishu_dail1.getMeasuredWidth();
//                line_4.requestLayout();
//
//            }
//        });
    }

    @Override
    protected void initInitevnts() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.yewu_dail1:
                //业务电话1
                WarningPopup.getInstance().showPopup(contentView, new WarningPopup.BtnClickListener() {
                    @Override
                    public void btnOk() {
                        requestPermission(HDCivilizationConstants.CALL_PHONE_REQUEST_CODE, "android.permission.CALL_PHONE", new Runnable() {
                            @Override
                            public void run() {
                                 Intent intent=new Intent();
                                 intent.setData(Uri.parse("tel:" + yewu_dail1.getText()));
                                 intent.setAction(Intent.ACTION_CALL);
                                UseGuideActivity.this.startActivity(intent);
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.getInstance().showToast("请检测电话权限!");
                            }
                        });
                    }

                    @Override
                    public void btnCancel() {
                        WarningPopup.getInstance().dismiss();
                    }
                },true,true,"是否拨打电话:"+yewu_dail1.getText()+"?");
                break;
            case R.id.yewu_dail2:
                WarningPopup.getInstance().showPopup(contentView, new WarningPopup.BtnClickListener() {
                    @Override
                    public void btnOk() {
                        requestPermission(HDCivilizationConstants.CALL_PHONE_REQUEST_CODE, "android.permission.CALL_PHONE", new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("tel:" + yewu_dail1.getText()));
                                intent.setAction(Intent.ACTION_CALL);
                                UseGuideActivity.this.startActivity(intent);
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.getInstance().showToast("请检测电话权限!");
                            }
                        });
                    }

                    @Override
                    public void btnCancel() {
                        WarningPopup.getInstance().dismiss();
                    }
                },true,true,"是否拨打电话:"+yewu_dail2.getText()+"?");
                break;
            case R.id.yewu_dail3:
                WarningPopup.getInstance().showPopup(contentView, new WarningPopup.BtnClickListener() {
                    @Override
                    public void btnOk() {
                        requestPermission(HDCivilizationConstants.CALL_PHONE_REQUEST_CODE, "android.permission.CALL_PHONE", new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("tel:" + yewu_dail1.getText()));
                                intent.setAction(Intent.ACTION_CALL);
                                UseGuideActivity.this.startActivity(intent);
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.getInstance().showToast("请检测电话权限!");
                            }
                        });
                    }

                    @Override
                    public void btnCancel() {
                        WarningPopup.getInstance().dismiss();
                    }
                },true,true,"是否拨打电话:"+yewu_dail3.getText()+"?");
                break;
            case R.id.jishu_dail1:
                WarningPopup.getInstance().showPopup(contentView, new WarningPopup.BtnClickListener() {
                    @Override
                    public void btnOk() {
                        requestPermission(HDCivilizationConstants.CALL_PHONE_REQUEST_CODE, "android.permission.CALL_PHONE", new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setData(Uri.parse("tel:" + yewu_dail1.getText()));
                                intent.setAction(Intent.ACTION_CALL);
                                UseGuideActivity.this.startActivity(intent);
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                UiUtils.getInstance().showToast("请检测电话权限!");
                            }
                        });
                    }

                    @Override
                    public void btnCancel() {
                        WarningPopup.getInstance().dismiss();
                    }
                },true,true,"是否拨打电话:"+jishu_dail1.getText()+"?");
                break;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        WarningPopup.getInstance().dismiss();
    }
}
