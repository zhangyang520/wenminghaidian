package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :huangxianfeng on 2016/8/16.
 * 申请志愿者提交的Info信息数据协议类
 */
public class VolunteerApplyFormProtocol extends BaseProtocol<String> {

    @Override
    public String parseJson(String jsonStr) throws JsonParseException, ContentException {
//        System.out.println("VolunteerApplyFormProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            System.out.println("VolunteerApplyFormProtocol...jsonStr = " + jsonObject.toString());
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("")){
                    throw new ContentException(getActionKeyName()+"");
                }else{
                    try {
                        User user= UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                    if(userPermission.equals(UserPermisson.ORDINARYSTATE.getType())){
                        throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"降为普通用户！");
                    }else{
                        throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,HDCivilizationConstants.FORBIDDEN_USER);
                    }
                }

            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                String userPermission = infoObj.getString("userPermission");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                    if (userPermission.equals(UserPermisson.VOLUNTEER) && //
                                                    user.getVolunteerId().trim().equals("")){
                        //更新状态的机制
                        System.out.println("VolunteerApplyFormProtocol 1111更新状态的机制");
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                }
                //解析数据
                String state = infoObj.getString("state");
                String msg = infoObj.getString("msg");
                if (HDCivilizationConstants.SUCCESS.equals(state)){
                    //申请成功
                    return "";
                }else if(HDCivilizationConstants.BUSINESS_ERROR.equals(state)){
                    /*
                     * 此时业务上的失败
                     */
                    throw new ContentException(msg);
                }else{
                    //正常的一些的Failure失败
                    throw new ContentException(msg);
                }
            }
        } catch (JSONException e) {
//            System.out.println("e.getMessage()"+e.getMessage());
//            System.out.println("jsonStr = "+jsonStr);
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
