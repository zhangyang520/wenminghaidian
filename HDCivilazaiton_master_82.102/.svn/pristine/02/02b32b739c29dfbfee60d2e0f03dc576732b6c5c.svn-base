package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.AppInfo;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by zhangyang on 2016/8/28.
 */
public class AppCheckProtocol extends BaseProtocol<AppInfo>{
    @Override
    protected AppInfo parseJson(String jsonStr)
            throws JsonParseException, ContentException {
        try {

            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject valuesObject = jsonObject.getJSONObject("values");

            System.out.println("LoginProtocol...jsonStr =" + jsonObject.toString());
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0) ||
                                valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){

                throw new ContentException("软件版本号获取失败！");
            }else{
                String userPermission = valuesObject.getString("userPermission");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();
                }
                    JSONObject infoObj = jsonObject.getJSONObject("info");
                    AppInfo appInfo=new AppInfo();
                    String versionCode=infoObj.getString("versionCode");
                    String versionIntroduce=infoObj.getString("versionIntroduce");
                    String appUrl=infoObj.getString("appUrl");
                    appInfo.setAppVersionCode(versionCode);
                    appInfo.setAppContent(versionIntroduce);
                    appInfo.setAppUrl(appUrl);
                    return appInfo;
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage()==="+e.getMessage());
            throw new JsonParseException("软件版本号获取失败!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
