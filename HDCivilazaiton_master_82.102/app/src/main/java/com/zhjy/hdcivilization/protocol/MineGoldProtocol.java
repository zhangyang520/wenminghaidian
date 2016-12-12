package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :huangxianfeng on 2016/8/12.'
 * 我的金币的协议类
 */
public class MineGoldProtocol extends BaseProtocol<String> {

    private String exchangeState;

    @Override
    protected String parseJson(String jsonStr) throws JsonParseException, ContentException {
//        System.out.println("SendCodeProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
//            System.out.println("SendCodeProtocol...jsonStr = " + jsonObject.toString());
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            String status=valuesObject.getString("status");
//            System.out.println("SendCodeProtocol parseJson  userPermission:"+"...status:"+status);
            if (status.equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if (status.equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("") ){
                    //进行前后台userid不一致
                    throw new ContentException(getActionKeyName()+"!");
                }else{
                    try {
                        //如果此时进行更新权限值 获取本地用户
                        User user = UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                    if(userPermission.equals(UserPermisson.ORDINARYSTATE.getType())){
                        throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"已经降为普通用户!");
                    }else{
                        throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,HDCivilizationConstants.FORBIDDEN_USER);
                    }
                }
            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                String userPermission = valuesObject.getString("userPermission");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    exchangeState = infoObj.getString("exchangeState");
//                    System.out.println("exchangeState user:" + exchangeState);
                    user.setIdentityState(userPermission);
                    user.setExchangeState(exchangeState);
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();
                }

                //解析数据
//                String exchangeState = infoObj.getString("exchangeState");

                return exchangeState;
            }
        } catch (JSONException e) {
//            System.out.println("e.getMessage()"+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
