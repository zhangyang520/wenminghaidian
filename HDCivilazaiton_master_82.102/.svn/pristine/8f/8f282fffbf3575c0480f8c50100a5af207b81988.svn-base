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
 * 点赞解析的协议类
 * Created by zhangyang on 2016/8/23.
 */
public class ClickLikesProtocol extends BaseProtocol<String> {
    @Override
    protected String parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {

            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                /*****userId传入的不为空，但出现-1值，说明前后台userId不一致，或者不存在此ID****///// TODO: 2016/9/19
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
                    throw new ContentException(getActionKeyName()+"!");
                }
            }else{
                String userPermission = valuesObject.getString("userPermission");
                JSONObject infosObject=jsonObject.getJSONObject("info");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();
                }

                //进行获取状态
                String state=infosObject.getString("state");
                if(state.trim().equals(HDCivilizationConstants.STATE_SUCCESS)){
                    //成功状态
                    return "点赞成功";
                }else if(state.trim().equals(HDCivilizationConstants.STATE_FAILURE)){
                   //失败：
                    throw new ContentException(HDCivilizationConstants.MESSAGE_DIAN_ZAN_NOT,infosObject.getString("msg"));
                }else{
                    throw new ContentException(getActionKeyName()+"!");
                }
            }
        } catch (JSONException e) {
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
