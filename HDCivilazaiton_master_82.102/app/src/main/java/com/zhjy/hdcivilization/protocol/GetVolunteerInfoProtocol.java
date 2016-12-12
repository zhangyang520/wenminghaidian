package com.zhjy.hdcivilization.protocol;

import android.util.Log;

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
 * Created by zhangyang on 2016/8/26.
 */
public class GetVolunteerInfoProtocol extends BaseProtocol<String>{

    private User outUser;

    public User getOutUser() {
        return outUser;
    }

    public void setOutUser(User outUser) {
        this.outUser = outUser;
    }

    @Override
    public String parseJson(String jsonStr)
            throws JsonParseException,ContentException {
        System.out.println("GetVolunteerInfoProtocol parseJson:"+jsonStr+".......");
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            System.out.println("GetVolunteerInfoProtocol...jsonStr = " + jsonObject.toString()+".....");
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
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
                    //如果此时进行更新权限值 获取本地用户
                    outUser.setIdentityState(userPermission);
                    //其他情况应该是保存成功
                    String state=infoObj.getString("state");
                    if(state.equalsIgnoreCase(HDCivilizationConstants.SUCCESS)){
                        //传输成功volunteerId goldCoin
                        String volunteerId=infoObj.getString("volunteerId");
                        outUser.setVolunteerId(volunteerId);
//                        String goldCoin=infoObj.getString("goldCoin");
//                        outUser.setGoldCoin(goldCoin);
                        UserDao.getInstance().saveUpDate(outUser);
                        return "获取志愿者信息成功!";
                    }else if(state.equals(HDCivilizationConstants.FAILURE)){
                        throw new ContentException(infoObj.getString("msg"));
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
