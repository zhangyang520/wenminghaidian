package com.zhjy.hdcivilization.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest;
import com.zhjy.hdcivilization.R;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseActivity;
import com.zhjy.hdcivilization.progressbar.KProgressHUD;
import com.zhjy.hdcivilization.protocol.GetVolunteerInfoProtocol;
import com.zhjy.hdcivilization.protocol.MineGoldProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.ThreadManager;
import com.zhjy.hdcivilization.utils.UiUtils;

import java.util.LinkedHashMap;

/**
 * @author :huangxianfeng on 2016/8/4.
 *         我的金币
 */
public class MineGoldActivity extends BaseActivity implements View.OnClickListener {

    private ImageView btnBack;
    private RelativeLayout rl_back;
    private TextView foldNumber;
    private TextView moneyNumber;
    private EditText editMoney;
    private User user;
    private String exchangeState;
    private KProgressHUD hud;
    private Button btnJian, btnJia;
    TextView goldRulas;
    Button btnApply;
    public static final String GOLDCOIN = "goldCoin";
    private String goldCoin;//金币的数量
    final int getVolunteerInfoSuccess = 208;//进行获取志愿者信息成功码
    final int getVolunteerInfoFailure = 209;//进行获取志愿者信息失败码

    final int requestType = -1;//请求类型:0请求首页,1:刷新首页
    final int exchange_state_query = 0;//请求首页
    final int exchange_process = 1;//刷新首页
    final String REQUEST_TYPE_KEY = "REQUEST_TYPE_KEY";//键的名称
    private int index = 0;
    private int indexNumber;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case HDCivilizationConstants.REFRESH_PAGE:
                    exchangeState = (String) msg.obj;
//                    System.out.println("apply exchangeState1=" + exchangeState);
                    //保存到SP当中去
                    UiUtils.getInstance().showToast("申请金币兑换成功!");
                    UserDao.getInstance().updateExchangeState(user, user.getUserId(), exchangeState);
                    //查询成功后:进行重新显示ui
                    setButtonBg(exchangeState);
                    hud.dismiss();
                    finish();
                    break;

                case HDCivilizationConstants.APPLY_NUMBER:
                    UiUtils.getInstance().showToast("金币兑换状态查询成功!");
                    exchangeState = (String) msg.obj;
//                    System.out.println("exchangeState2=" + exchangeState);
                    setButtonBg(exchangeState);
                    hud.dismiss();
                    UserDao.getInstance().updateExchangeState(user, user.getUserId(), exchangeState);
                    break;

                case HDCivilizationConstants.ERROR_CODE:
                    hud.dismiss();
                    if (msg.getData().getInt(HDCivilizationConstants.ACTION_CODE) == HDCivilizationConstants.HEAD_DATA) {
                        UiUtils.getInstance().showToast(msg.getData().getString(HDCivilizationConstants.ERROR_CONTENT));
                    }
                    break;

                case getVolunteerInfoSuccess://请求志愿者信息成功!
                    int requestType = msg.getData().getInt(REQUEST_TYPE_KEY);
                    if (requestType == exchange_process) {
                        //请求交易数据
                        processGoldCoinExchange();
                    } else if (requestType == exchange_state_query) {
                        //刷新首页数据
                        getLatestExchangeState();
                    }
                    break;

                case getVolunteerInfoFailure://请求志愿者信息失败!
                    requestType = msg.getData().getInt(REQUEST_TYPE_KEY);
                    if (requestType == exchange_process) {
                        //请求交易数据
                        UiUtils.getInstance().showToast("金币兑换失败!");
                    } else if (requestType == exchange_state_query) {
                        //刷新首页数据
                        UiUtils.getInstance().showToast("金币兑换状态查询失败!");
                    } else {
                        UiUtils.getInstance().showToast(msg.obj + "");
                    }
                    hud.dismiss();
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        customLayout = R.layout.activity_mine_gold;
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        goldCoin = getIntent().getStringExtra(GOLDCOIN);
//        goldCoin="1200";
        try {
            user = UserDao.getInstance().getLocalUser();
            if (!user.getIdentityState().equals(UserPermisson.VOLUNTEER.getType())) {
                UiUtils.getInstance().showToast("不是志愿者权限!");
                finish();
            }
        } catch (ContentException e) {
            e.printStackTrace();
            UiUtils.getInstance().showToast(e.getMessage());
            finish();
        }
        btnBack = (ImageView) findViewById(R.id.btn_back);
        rl_back = (RelativeLayout) findViewById(R.id.rl_back);
        foldNumber = (TextView) findViewById(R.id.mine_gold_number);
        moneyNumber = (TextView) findViewById(R.id.text_money_number);
        editMoney = (EditText) findViewById(R.id.edit_money_text);
        btnApply = (Button) findViewById(R.id.apply_money);
        btnJian = (Button) findViewById(R.id.gold_jian);
        btnJia = (Button) findViewById(R.id.gold_jia);
        goldRulas = (TextView) findViewById(R.id.gold_rulas);
        //红包规则从后台获取
        String presentRules = (String) SharedPreferencesManager.
                get(UiUtils.getInstance().getContext(), HDCivilizationConstants.PRESENTRULES, "");
        goldRulas.setText("       " + presentRules);
        setButtonBg(user.getExchangeState());


        /**
         * 判断文明贡献值是否小于1000
         * 如果小于1000文明贡献值，则不能进行兑换
         */
        if (Integer.parseInt(goldCoin) < HDCivilizationConstants.INIT_GOLD_COIN_VALUE_LARGES) {
            /**
             * 如果==0，则全部显示为数字0
             */
            if (Integer.parseInt(goldCoin) == 0) {
                moneyNumber.setText("0");
                editMoney.setText("0");
            } else {
                //如果不为0则显示为可兑换的人民币值
                moneyNumber.setText(Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE + "");
                editMoney.setText(Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE + "");
                indexNumber = Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE;
            }

        } else {
            //如果文明贡献值大于1000时的时候，则直接显示可兑换的人民币值
            moneyNumber.setText(Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE + "");
            indexNumber = Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE;
            index = Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE;
            //如果人民币值大于500，则最大值只能显示为500
            if (index > 500) {
                editMoney.setText(500 + "");
                index = 500;
            } else {
                //否则直接显示可兑换的人民币值
                editMoney.setText(Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE + "");
            }
        }

        btnJia.setOnClickListener(this);
        btnJian.setOnClickListener(this);
        rl_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.gold_jia:
                //限制人民币不能超过500值
                if (index > 500) {
                } else {
                    if (index == 500) {
                    } else {
                        //在相加的时候，判断数据是否为空
                        if (!editMoney.getText().toString().equals("")) {
                            //限制在输入的时候，相加不能超过最高的人民币兑换值
                            if (Integer.parseInt(editMoney.getText().toString()) + 2 > Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE) {
                                UiUtils.getInstance().showToast("申请金额不得超过可兑换金额");
                            } else {
                                index = Integer.parseInt(editMoney.getText().toString());
                                if (index < indexNumber && (index + HDCivilizationConstants.GOLD_NEW) <= indexNumber) {
                                    if (index == 500) {
                                        editMoney.setText(index + "");
                                    } else {
                                        index += HDCivilizationConstants.GOLD_NEW;
                                        editMoney.setText(index + "");
                                    }
                                } else {
                                    UiUtils.getInstance().showToast("申请金额不得超过可兑换金额");
                                }
                            }
                        } else {
                            if (Integer.parseInt(goldCoin) != 0) {
                                editMoney.setText(Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE + "");
                            } else {
                                editMoney.setText("0");
                                UiUtils.getInstance().showToast("申请金额不得超过可兑换金额");
                            }
                        }
                    }
                }
                break;
            case R.id.gold_jian:
                if (Integer.parseInt(editMoney.getText().toString()) == 0 || Integer.parseInt(editMoney.getText().toString()) < 0) {
                    UiUtils.getInstance().showToast("申请金额不得小于0");
                } else {
                    if (!editMoney.getText().toString().equals("")) {
                        //输入的数字是否等于0或者在相减之前不能小于2
                        if (Integer.parseInt(editMoney.getText().toString()) == 0 || Integer.parseInt(editMoney.getText().toString()) <= 1) {
                            UiUtils.getInstance().showToast("申请金额不得小于0");
                        } else {
                            //判断输入的数字是否大于可兑换的金额
                            if (Integer.parseInt(editMoney.getText().toString()) > Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE) {
                                editMoney.setText(Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE + "");
                            } else {
                                index = Integer.parseInt(editMoney.getText().toString());
                                index -= HDCivilizationConstants.GOLD_NEW;
                                editMoney.setText(index + "");
                            }
                        }
                    } else {
                        if (Integer.parseInt(goldCoin) != 0) {
                            editMoney.setText(Integer.parseInt(goldCoin) / HDCivilizationConstants.GOLD_COIN_RATE - 2 + "");
                        } else {
                            editMoney.setText("0");
                            UiUtils.getInstance().showToast("申请金额不得为0");
                        }
                    }
                }
                break;
        }
    }

    private void setButtonBg(String exchangeState) {
        /***判断金币的兑换状态，当为未兑换状态时，Button显示不可点击且为灰色**/
        if (Integer.parseInt(goldCoin) / 10 < HDCivilizationConstants.MIN_COIN) {
            btnApply.setBackgroundResource(R.drawable.btn_shape_gold_press_bg);
            btnApply.setEnabled(false);
        } else {
            /**
             * 判断交易状态
             * 0：未兑换
             * 1：已兑换
             * 2：本月只能兑换一次
             * 当未兑换或者对换之后，按钮显示为灰色按钮
             */
            if (exchangeState.trim().equals(HDCivilizationConstants.EXCHANGE_STATE_0) || exchangeState.trim().equals(HDCivilizationConstants.EXCHANGE_STATE_2)) {
                btnApply.setBackgroundResource(R.drawable.btn_shape_gold_press_bg);
                btnApply.setEnabled(false);
            } else {
                btnApply.setBackgroundResource(R.drawable.btn_shape_gold_press_);
                btnApply.setEnabled(true);
            }
        }
    }

    @Override
    protected void initInitevnts() {

        foldNumber.setText(goldCoin);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //进行判断是否有志愿者的id：
                if (user.getVolunteerId().trim().equals("")) {
                    //志愿者的id为空：
                    getVolunteerInfo(user, exchange_process);
                } else {
                    //进行处理交易状态
                    processGoldCoinExchange();
                }
            }
        });

        /********查看金币兑换的状态**********/
        hud = KProgressHUD.create(MineGoldActivity.this).
                setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).
                setLabel("状态查询中").setCancellable(false);
        hud.setCancellable(false);
        hud.show();

        /**
         * 进行获取最新一笔交易状态
         */
        if (user.getVolunteerId().trim().equals("")) {
            //如果志愿者id为空
            getVolunteerInfo(user, exchange_state_query);
        } else {
            //对应的志愿者id不为空
            getLatestExchangeState();
        }
    }

    private void processGoldCoinExchange() {
        if (!editMoney.getText().toString().trim().equals("")) {
            //金币少于100金币时，不能兑换
            if (!goldCoin.trim().equals("") && Integer.parseInt(editMoney.getText().toString().trim()) >=HDCivilizationConstants.MIN_COIN) {
                if (Integer.parseInt(editMoney.getText().toString().trim()) > 500) {
                    UiUtils.getInstance().showToast("超出兑换范围！");
                } else {
                    if (Integer.parseInt(editMoney.getText().toString().trim()) < 0 || Integer.parseInt(editMoney.getText().toString().trim()) == 0 ||
                            Integer.parseInt(editMoney.getText().toString().trim()) > 500 || Integer.parseInt(editMoney.getText().toString().trim()) > indexNumber) {
                        UiUtils.getInstance().showToast("超出兑换范围！");
                    } else {
                        if (Integer.parseInt(editMoney.getText().toString().trim()) % 2 == 0) {
                            /***开启提示发送中**/
                            hud = KProgressHUD.create(MineGoldActivity.this).setStyle(KProgressHUD.Style.SPIN_INDETERMINATE).setLabel("提现申请中").setCancellable(false);
                            hud.setCancellable(false);
                            hud.show();
                            ThreadManager.getInstance().createLongPool().execute(new Runnable() {
                                @Override
                                public void run() {
                                    Message message = Message.obtain();
                                    Bundle bundle = new Bundle();
                                    try {
//                                        System.out.println("apply gold running ");
                                        UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                                        LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                                        paramsMap.put("tranCode", "AROUND0019");
                                        paramsMap.put("volunteerId", user.getVolunteerId());
//                                        System.out.println("editMoney.getText()==" + editMoney.getText().toString().trim());
                                        paramsMap.put("applyCount", editMoney.getText().toString().trim());//提现的数量
                                        paramsMap.put("userId", user.getUserId());
                                        paramsMap.put("applicationnum", String.valueOf(Integer.parseInt(editMoney.getText().toString().trim()) * 10));
                                        urlParamsEntity.setParamsHashMap(paramsMap);
                                        urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                                        MineGoldProtocol mineGoldProtocol = new MineGoldProtocol();
                                        mineGoldProtocol.setActionKeyName("提现状态获取失败");
                                        message.obj = mineGoldProtocol.loadData(urlParamsEntity);
                                        message.what = HDCivilizationConstants.REFRESH_PAGE;
                                        handler.sendMessage(message);
                                    } catch (JsonParseException e) {
                                        e.printStackTrace();
                                        message.what = HDCivilizationConstants.ERROR_CODE;
                                        bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                                        bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                                        message.setData(bundle);
                                        handler.sendMessage(message);
                                    } catch (ContentException e) {
                                        e.printStackTrace();
                                        if (e.getErrorCode() == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                                            //此时为普通用户被禁用! // TODO: 2016/9/20
                                            message.what = HDCivilizationConstants.ERROR_CODE;
                                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                                            message.setData(bundle);
                                            handler.sendMessage(message);
                                        } else {
                                            message.what = HDCivilizationConstants.ERROR_CODE;
                                            bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                                            bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                                            message.setData(bundle);
                                            handler.sendMessage(message);
                                        }
                                    }
                                }
                            });
                        } else {
                            UiUtils.getInstance().showToast("兑换金额需为偶数");
                        }
                    }
                }
            } else {
                UiUtils.getInstance().showToast("兑换金额不满足兑换条件");
            }
        } else {
            UiUtils.getInstance().showToast("兑换金额不能为空");
        }

    }

    private void getLatestExchangeState() {
        ThreadManager.getInstance().createLongPool().execute(new Runnable() {
            @Override
            public void run() {
                Message message = Message.obtain();
                Bundle bundle = new Bundle();
                try {
                    UrlParamsEntity urlParamsEntity = new UrlParamsEntity();
                    LinkedHashMap<String, String> paramsMap = new LinkedHashMap<String, String>();
                    paramsMap.put("tranCode", "AROUND0020");
                    paramsMap.put("volunteerId", user.getVolunteerId());
                    paramsMap.put("userId", user.getUserId());
                    urlParamsEntity.setParamsHashMap(paramsMap);
                    urlParamsEntity.HDCURL = UrlParamsEntity.CURRENT_ID;
                    MineGoldProtocol mineGoldProtocol = new MineGoldProtocol();
                    mineGoldProtocol.setActionKeyName("提现状态获取失败");
                    message.obj = mineGoldProtocol.loadData(urlParamsEntity);
                    message.what = HDCivilizationConstants.APPLY_NUMBER;
                    handler.sendMessage(message);
                } catch (JsonParseException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getMessage());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                    message.setData(bundle);
                    handler.sendMessage(message);
                } catch (ContentException e) {
                    e.printStackTrace();
                    message.what = HDCivilizationConstants.ERROR_CODE;
                    bundle.putString(HDCivilizationConstants.ERROR_CONTENT, e.getErrorContent());
                    bundle.putInt(HDCivilizationConstants.ACTION_CODE, HDCivilizationConstants.HEAD_DATA);
                    message.setData(bundle);
                    handler.sendMessage(message);
                }
            }
        });
    }


    /**
     * 进行获取
     */
    private void getVolunteerInfo(final User user, final int requestType) {
        RequestParams params = new RequestParams(); // 默认编码UTF-8
        params.addQueryStringParameter("tranCode", "AROUND0025");
        params.addQueryStringParameter("userId", user.getUserId());
        HttpUtils http = new HttpUtils();
        http.send(HttpRequest.HttpMethod.POST,
                UrlParamsEntity.CURRENT_ID,
                params,
                new RequestCallBack<String>() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onLoading(long total, long current, boolean isUploading) {
                        if (isUploading) {
//                            System.out.println("upload: " + current + "/" + total);
                        } else {
//                            System.out.println("reply: " + current + "/" + total);
                        }
                    }

                    @Override
                    public void onSuccess(ResponseInfo<String> responseInfo) {
//                        System.out.println("reply: " + responseInfo.result);
                        try {
                            Message message = Message.obtain();
                            Bundle bundle = new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            GetVolunteerInfoProtocol getVolunteerInfoProtocol = new GetVolunteerInfoProtocol();
                            getVolunteerInfoProtocol.setUserId(user.getUserId());
                            getVolunteerInfoProtocol.setOutUser(user);
                            message.obj = getVolunteerInfoProtocol.parseJson(responseInfo.result);
                            message.what = getVolunteerInfoSuccess;
                            handler.sendMessage(message);
                        } catch (JsonParseException e) {
                            e.printStackTrace();
                            Message message = Message.obtain();
                            message.what = getVolunteerInfoFailure;
                            message.obj = e.getMessage();
                            Bundle bundle = new Bundle();
                            bundle.putInt(REQUEST_TYPE_KEY, requestType);
                            message.setData(bundle);
                            handler.sendMessage(message);
                        } catch (ContentException e) {
                            e.printStackTrace();
                            if (e.getErrorCode() == HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE) {
                                Message message = Message.obtain();
                                message.what = getVolunteerInfoFailure;
                                message.obj = e.getErrorContent();
                                Bundle bundle = new Bundle();
                                message.setData(bundle);
                                handler.sendMessage(message);
                            } else {
                                Message message = Message.obtain();
                                message.what = getVolunteerInfoFailure;
                                message.obj = e.getErrorContent();
                                Bundle bundle = new Bundle();
                                bundle.putInt(REQUEST_TYPE_KEY, requestType);
                                message.setData(bundle);
                                handler.sendMessage(message);
                            }
                        }
                    }

                    @Override
                    public void onFailure(HttpException error, String msg) {
                        Message message = Message.obtain();
                        message.what = getVolunteerInfoFailure;
//                        message.obj = "文明监督提报失败!";
                        handler.sendMessage(message);
                        //java.net.SocketTimeoutException
//                        System.out.println("uploadImgProtocol  onFailure......" + "error:" + //
//                                error.getExceptionCode() + "..getMessage:" + error.getMessage() + "...msg:" + msg);
                    }
                });
    }


    /**
     * 取整数
     */
    public static int doubleTrans(double d) {
        if (Math.round(d) - d == 0) {
            return Integer.parseInt(String.valueOf((long) d));
        }
        return Integer.parseInt(String.valueOf(d));
    }


}
