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
 * 上传文明监督中提交数据的协议
 * Created by zhangyang on 2016/7/28.
 */
public class SuperviseSubmitDataProtocol extends BaseProtocol<String>{

    private User user;

    @Override
    public String parseJson(String jsonStr) throws JsonParseException, ContentException {
        Log.d("SuperviseSubm", "SuperviseSubmitDataProtocol parseJson:" + jsonStr + "....");
        System.out.println("SuperviseSubmitDataProtocol parseJson:"+jsonStr+".......");
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject valuesObject = jsonObject.getJSONObject("values");
//            String userPermission = valuesObject.getString("userPermission");

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
                try {
                    String userPermission = valuesObject.getString("userPermission");
                    //如果此时进行更新权限值 获取本地用户
                    user=UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ContentException e) {
                    e.printStackTrace();
                }

                //其他情况应该是保存成功
                String state=infoObj.getString("state");
                if(state.equals(HDCivilizationConstants.SUCCESS)){
                    //传输成功
                    return infoObj.getString("superviseNum");
                }else if(state.equals(HDCivilizationConstants.FAILURE_NO_VOLLENTEER)){
                    throw new ContentException("尚未申请志愿者!");
                }else if(state.equals(HDCivilizationConstants.FAILURE_NO_USER)){
                    throw new ContentException("用户不存在,请重新登录!");
                }else if(state.equals(HDCivilizationConstants.FAILURE_NO_MATCH_VOLLENTEER)){
//                    throw new ContentException(HDCivilizationConstants.NO_MATCH_VOLLENTEER,"志愿者信息错误,请重新登录!");
                    throw new ContentException(HDCivilizationConstants.NO_MATCH_VOLLENTEER,getActionKeyName()+"!");
                }else if(state.equals(HDCivilizationConstants.FAILURE_NO_VOLLENTEER1)){
                    throw new ContentException("不存在该志愿者,请重新申请!");
                }else{
                    throw new ContentException(infoObj.getString("msg"));
                }
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage()==="+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
