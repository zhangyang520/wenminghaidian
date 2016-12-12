package com.zhjy.hdcivilization.protocol;

import android.util.Log;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangyang on 2016/8/25.
 */
public class UserInfoEditProtocol extends BaseProtocol<String>{
    private int submitType;//提交内容类型

    private User activityUser;

    public int getSubmitType() {
        return submitType;
    }

    public void setSubmitType(int submitType) {
        this.submitType = submitType;
    }

    public User getActivityUser() {
        return activityUser;
    }

    public void setActivityUser(User activityUser) {
        this.activityUser = activityUser;
    }

    @Override
    public String parseJson(String jsonStr) throws JsonParseException,ContentException {
        Log.d("SubmitThemeProtocol", "SubmitThemeProtocol parseJson:" + jsonStr);
        try {
            System.out.println("SubmitThemeProtocol...jsonStr = " + jsonStr);
            JSONObject jsonObject = new JSONObject(jsonStr);
            System.out.println("SubmitThemeProtocol parseJson:"+jsonObject.toString());
            JSONObject valuesObject = jsonObject.getJSONObject("values");

            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){

                throw new ContentException(getActionKeyName()+"!");
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("")){
                    //进行前后台userid不一致
                    throw new ContentException(getActionKeyName()+"!");
                }else{
                    //如果此时进行更新权限值 获取本地用户
                    try {
                        User user= UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                    throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,HDCivilizationConstants.FORBIDDEN_USER);
                }
            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                String userPermission = valuesObject.getString("userPermission");

                    //其他情况应该是保存成功
                    String state=infoObj.getString("state");
                    activityUser.setIdentityState(userPermission);
                    if(state.equalsIgnoreCase(HDCivilizationConstants.SUCCESS)) {
                        //保存成功
                        switch (submitType) {
                            case 0:
                                //全部提交
                                String facePhotoUrl=infoObj.getString("facePhotoUrl");
                                System.out.println("facePhotoUrl:"+(UrlParamsEntity.CURRENT_PHOTO_IP+facePhotoUrl));
                                activityUser.setPortraitUrl(UrlParamsEntity.CURRENT_PHOTO_IP+facePhotoUrl);
                                break;
                            case 1://图片上传
                                //全部提交
                                facePhotoUrl=infoObj.getString("facePhotoUrl");
                                System.out.println("facePhotoUrl:"+(UrlParamsEntity.CURRENT_PHOTO_IP+facePhotoUrl));
                                activityUser.setPortraitUrl(UrlParamsEntity.CURRENT_PHOTO_IP+facePhotoUrl);
                                activityUser.setIdentityState(userPermission);
                                break;
                            case 2://字段提交
                                break;
                        }
                        UserDao.getInstance().saveUpDate(activityUser);
                        return "";
                    }else if(state.equalsIgnoreCase(HDCivilizationConstants.SENSITIVECONTENT)){
                        //输入有敏感词汇
                        throw new ContentException(infoObj.getString("msg"));
                    }else{
                        //其他失败情况
                        throw new ContentException(infoObj.getString("msg"));
                    }

            }
        } catch (JSONException e) {
            System.out.println("e.getMessage()==="+e.getMessage());
            throw new JsonParseException("数据格式传输错误!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
