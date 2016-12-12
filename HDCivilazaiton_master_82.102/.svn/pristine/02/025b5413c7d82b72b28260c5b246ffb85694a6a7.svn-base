package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :zhangyang on 2016/7/26.
 * 点击详情页让后台进行记录条目的点击次数
 */
public class ClickDetailCountProtocol extends BaseProtocol<String> {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected String parseJson(String jsonStr) throws JsonParseException, ContentException {
        System.out.println("MainNumberProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valuesObject= jsonObject.getJSONObject("values");
            System.out.println("valuesObject.toString = " + valuesObject.toString());
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0) ||
                                valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                throw new ContentException(getActionKeyName()+"!");
            }else{
                    if(user!=null){
                        String userPermission = valuesObject.getString("userPermission");
                        //如果此时进行更新权限值 获取本地用户
                        user.setIdentityState(userPermission);
                        System.out.println("save user:" + user.toString());
                        UserDao.getInstance().saveUpDate(user);
                    }
                     return "";
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage() = "+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
