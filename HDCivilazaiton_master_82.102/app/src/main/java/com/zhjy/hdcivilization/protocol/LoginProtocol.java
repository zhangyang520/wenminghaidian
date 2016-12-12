package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.SystemSettingDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.SystemSetting;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :huangxianfeng on 2016/8/2.
 *         登录界面的协议类
 */
public class LoginProtocol extends BaseProtocol<User> {
    private String number;//手机号--昵称默认值
    private String sendCode;//验证码
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    @Override
    protected User parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {

            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0) ||
                                valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                throw new ContentException(getActionKeyName()+"!");
            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                //解析数据
                String state = infoObj.getString("loginFlag");
                String msg = infoObj.getString("loginMsg");
                if (HDCivilizationConstants.SUCCESS.equals(state)){
                    String userId = infoObj.getString("userId");
                    String volunteerId = infoObj.optString("volunteerId");
                    String userPermission2=infoObj.getString("userPermission");
                    String lastLoginTime = infoObj.getString("lastLoginTime");
                    System.out.println("LoginProtocol...lastLoginTime1111="+lastLoginTime+"..volunteerId:"+volunteerId+"...userId:"+userId);
                    SharedPreferencesManager.put(UiUtils.getInstance().getContext(), HDCivilizationConstants.LASTLOGINTIME, lastLoginTime);
                    User daoUser = UserDao.getInstance().getUserId(userId);
                    if (daoUser!=null){
                        if(Integer.parseInt(userPermission2)<Integer.parseInt(UserPermisson.ORDINARYSTATE.getType())){
                            //如果小于普通用户
                            throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE, HDCivilizationConstants.FORBIDDEN_USER);
                        }else{
                            //如果有对应的用户:除了本地用户将其它的用户的本地用户状态改为false
                            UserDao.getInstance().updateExceptUserLocalState(daoUser.getUserId(), true);
                            //进行保存对应的userId,volunteerId,身份权限值
                            daoUser.setIsLocalUser(true);
                            daoUser.setVolunteerId(volunteerId);

                            daoUser.setIdentityState(userPermission2);
                            daoUser.setLastLoginTime(System.currentTimeMillis());
                            daoUser.setAccountNumber(number);
                            daoUser.setSendCode(sendCode);
                            UserDao.getInstance().saveUpDate(daoUser);
                            return null;
                        }
                    }else{
                        //如果为空 新增最新的用户 本地用户状态
                        UserDao.getInstance().updateAllUserLocalState(false);
                        User user=new User();
                        user.setIdentityState(userPermission2);
                        user.setUserId(userId);
                        //设置默认的昵称为手机号
//                        System.out.println("numberfsfdsfsadfsdaf:.."+number);
                        user.setAccountNumber(number);
                        user.setSendCode(sendCode);
                        user.setLastLoginTime(System.currentTimeMillis());
                        user.setVolunteerId(volunteerId);
                        user.setIsLocalUser(true);
                        UserDao.getInstance().saveUpDate(user);
                        //进行初始化设置字体:
                        initUserSystemSetting(userId);
                        //进行初始化消息提醒数字
                        initUserMainNumber(user);
                        if(Integer.parseInt(userPermission2)<Integer.parseInt(UserPermisson.ORDINARYSTATE.getType())){
                            //如果小于普通用户
                            throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE, HDCivilizationConstants.FORBIDDEN_USER);
                        }else{
                            return null;
                        }
                    }
                }else{
                    throw new ContentException(msg);
                }
            }
        } catch (JSONException e) {
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    /**
     * 进行初始化消息提醒个数
     * @param user
     */
    private void initUserMainNumber(User user) {
        //进行初始化该用户的消息提醒数
        try {
            //如果数据库有对应的数据--进行获取
            MainNumberDao.getInstance().getNumberBy(user.getUserId());
        } catch (ContentException e) {
            e.printStackTrace();
            //如果数据库没有对应的数据--进行新建
            HDC_MainNumber mainNumber = new HDC_MainNumber();
            mainNumber.setUser(user.getUserId());
            MainNumberDao.getInstance().saveNumber(mainNumber);
        }
    }

    /**
     * 进行初始化系统字体
     * @param userId
     */
    private void initUserSystemSetting(String userId) {
        try {
            SystemSetting setting= SystemSettingDao.getInstance().getSystemSetting(userId);
        } catch (ContentException e) {
            e.printStackTrace();
            SystemSetting setting=new SystemSetting();
            setting.setIsPush(true);
            setting.setFontSize(HDCivilizationConstants.IN_LARGE);
            setting.setUserId(userId);
            SystemSettingDao.getInstance().saveObj(setting);
            //进行设置加载模式!
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
