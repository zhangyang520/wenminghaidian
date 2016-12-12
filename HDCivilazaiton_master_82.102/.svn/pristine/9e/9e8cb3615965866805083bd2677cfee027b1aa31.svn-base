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
 * Created by zhangyang on 2016/7/27.
 * 上传图片的相关信息
 */
public class UploadImgProtocol extends BaseProtocol<String>{

    //此时不需要进行解析
    @Override
    public String parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            System.out.println("UploadImgProtocol .... jsonStr:"+jsonStr);
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valueJsonObject=jsonObject.getJSONObject("values");
            String status=valueJsonObject.getString("status");
            if(status.equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if(status.equals(HDCivilizationConstants.STATUS_2)){
                //权限过低
                String userPermission=valueJsonObject.getString("userPermission");
                if(!getUserId().equals("") && userPermission.equals(UserPermisson.UNKNOW_VALUE.getType())){
                    //前后台的用户的id不匹配
                    throw new ContentException(getActionKeyName()+"!");
                }else{
                    try {
                        User user=UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                    throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,HDCivilizationConstants.FORBIDDEN_USER);
                }
            }else{
                try {
                    String userPermission=valueJsonObject.getString("userPermission");
                    //這裡應該不會出現未登錄的情況
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();
                }
                //進行解析圖片的id
                JSONObject infoObjecet=jsonObject.getJSONObject("info");
                String imageId=infoObjecet.getString("imgId");
                return imageId;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new ContentException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
