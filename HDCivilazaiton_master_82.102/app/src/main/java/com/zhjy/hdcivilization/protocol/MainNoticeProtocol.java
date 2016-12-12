package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNotice;
import com.zhjy.hdcivilization.entity.ImgEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/26.
 * 首页通知公告的协议类
 */
public class MainNoticeProtocol extends BaseProtocol<List<HDC_MainNotice>> {

    private HDC_MainNotice mainNotice;

    private List<HDC_MainNotice> datas;

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    protected List<HDC_MainNotice> parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            System.out.println("MainNoticeProtocol...jsonStr = " + jsonStr);
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                /*****userId传入的不为空，但出现-1值，说明前后台userId不一致，或者不存在此ID****/
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !userId.equals("") ){
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
                }
                throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"权限过低！");
            }else{
                String userPermission = valuesObject.getString("userPermission");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    System.out.println("save user:" + user.toString());
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();
                }
                //解析数据
                datas = new ArrayList<HDC_MainNotice>();
                JSONObject infoObj = jsonObject.getJSONObject("info");
                System.out.println("infoObj = "+infoObj.toString());
                JSONArray list = infoObj.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject listObj = list.getJSONObject(i);
                    System.out.println("listObj = "+listObj.toString());
                    String noticeTitle = listObj.getString("noticeTitle");
                    String itemId = listObj.getString("itemId ");
                    mainNotice = new HDC_MainNotice();
                    mainNotice.setTitle(noticeTitle);
                    mainNotice.setItemId(itemId);
                    datas.add(mainNotice);
                }
                return datas;
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage()="+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
