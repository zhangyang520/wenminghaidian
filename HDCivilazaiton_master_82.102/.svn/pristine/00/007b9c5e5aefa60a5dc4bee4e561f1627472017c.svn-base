package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
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
 * @author :huangxianfeng on 2016/7/25.
 * 首页图片轮播协议类
 */
public class MainViewPagerProtocol extends BaseProtocol<List<HDC_CiviState>> {

    private List<HDC_CiviState> datas;

    private ImgEntity imgEntity;

    private HDC_CiviState civiState;

    private String userId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public MainViewPagerProtocol() {

    }

    @Override
    protected List<HDC_CiviState> parseJson(String jsonStr) throws JsonParseException, ContentException {
        System.out.println("MainViewPagerProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException("获取失败！");
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                /*****userId传入的不为空，但出现-1值，说明前后台userId不一致，或者不存在此ID****/
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !userId.equals("") ){
                    //进行前后台userid不一致
                    throw new ContentException("前后台的userId不匹配！");
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
                throw new ContentException("权限过低！");
            }else{
                datas = new ArrayList<HDC_CiviState>();
                String userPermission = valuesObject.getString("userPermission");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    System.out.println("save user:" + user.toString());
                    UserDao.getInstance().saveUpDate(user);
                    if (userPermission.equals(UserPermisson.VOLUNTEER.getType()) && user.getVolunteerId().trim().equals("")){
                        //更新状态的机制
                        System.out.println("MainViewPagerProtocol 1111更新状态的机制");
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                }
                //解析数据
                JSONObject infoObj = jsonObject.getJSONObject("info");
                JSONArray list = infoObj.getJSONArray("list");
                for (int i = 0; i < list.length(); i++) {
                    JSONObject listObj = list.getJSONObject(i);
                    civiState = new HDC_CiviState();
                    imgEntity = new ImgEntity();
                    JSONObject imgObject = listObj.getJSONObject("imgObject");
                    String imgUrl = imgObject.getString("imgUrl");
                    String thumbUrl = imgObject.getString("thumbUrl");
                    String imgId = imgObject.getString("imgId");
                    imgEntity.setImgUrl(imgUrl);
                    imgEntity.setImgThumbUrl(thumbUrl);
                    imgEntity.setItemId(imgId);

                    String imgTitle = listObj.getString("imgTitle");
                    String itemId = listObj.getString("itemId");

                    civiState.setImgEntity(imgEntity);
                    civiState.setTitle(imgTitle);
                    civiState.setItemId(itemId);

                    datas.add(civiState);
                }
                return datas;
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage() = "+e.getMessage());
            throw new JsonParseException("数据格式传输错误!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
