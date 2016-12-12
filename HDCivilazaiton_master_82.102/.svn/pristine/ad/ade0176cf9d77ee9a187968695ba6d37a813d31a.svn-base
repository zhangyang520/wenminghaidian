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
 * @author :huangxianfeng on 2016/9/13.
 * 注销退出登录
 */
public class LoginOutProtocol extends BaseProtocol<String> {
    @Override
    protected String parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            String status=valuesObject.getString("status");
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
                        UserDao.getInstance().updateAllUserLocalState(false);
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                    return "";
                }
            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                try {
                    String userPermission = valuesObject.getString("userPermission");
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                    UserDao.getInstance().updateAllUserLocalState(false);
                } catch (ContentException e) {
                    e.printStackTrace();
                }
                //解析数据
                String state = infoObj.getString("state");
                String msg = infoObj.getString("msg");
                if (HDCivilizationConstants.SUCCESS.equals(state)){
                    return "";
                }else{
                    throw new ContentException(msg);
                }
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage()"+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
