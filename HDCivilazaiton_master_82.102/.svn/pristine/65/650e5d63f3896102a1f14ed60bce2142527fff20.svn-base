package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_UserCommentList;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :huangxianfeng on 2016/8/26.
 * 所有评论的内容发送接口
 */
public class ContentSendProtocol extends BaseProtocol<HDC_UserCommentList> {

    private User launchUser;

    HDC_UserCommentList userCommentList;

    private String itemIdAndType="";

    private String fatherItemId="";

    public String getFatherItemId() {
        return fatherItemId;
    }

    public void setFatherItemId(String fatherItemId) {
        this.fatherItemId = fatherItemId;
    }

    public String getItemIdAndType() {
        return itemIdAndType;
    }

    public void setItemIdAndType(String itemIdAndType) {
        this.itemIdAndType = itemIdAndType;
    }


    @Override
    public HDC_UserCommentList parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName());
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("")){
                    //
                    throw new ContentException(getActionKeyName());
                }else{
                    try {
                        //如果此时进行更新权限值 获取本地用户
                        User user = UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                    }catch (ContentException e) {
                        e.printStackTrace();
                    }
                    throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"您的账号已被禁用！");
                }
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
                //解析数据
                String state = infoObj.getString("state");

                userCommentList=new HDC_UserCommentList();
                if (HDCivilizationConstants.SUCCESS.equals(state)){
                    JSONObject obj = infoObj.getJSONObject("obj");
                    int totalCount = obj.getInt("totalCount");
                    String userId = obj.getString("userId");
                    String itemId = obj.getString("itemId");
                    String imgUrl = obj.getString("imgUrl");
                    long  time = obj.getLong("time");
                    String content = obj.getString("content");
                    String nickName = obj.optString("nickName");

                    launchUser = UserDao.getInstance().getUserId(userId);
                    if (launchUser==null){
                        launchUser = new User();
                        launchUser.setUserId(userId);
                        launchUser.setNickName(nickName);
                        launchUser.setPortraitUrl(imgUrl);
                        UserDao.getInstance().saveUpDate(launchUser);
                    }else{
                        launchUser.setUserId(userId);
                        launchUser.setNickName(nickName);
                        launchUser.setPortraitUrl(imgUrl);
                        UserDao.getInstance().saveUpDate(launchUser);
                    }
                    userCommentList.setUser(launchUser);
                    userCommentList.setContent(content);
                    userCommentList.setItemId(itemId);
                    userCommentList.setPublishTime(time);
                    userCommentList.setCount(totalCount);
                    userCommentList.setTopicItemIdAndType(itemIdAndType);
                    userCommentList.setFatherItemId(fatherItemId);
                    return userCommentList;
                }else if(state.equals(HDCivilizationConstants.SENSITIVECONTENT)){
                    throw new ContentException(getKeyName()+"中含有敏感词汇!");
                }else if(state.equals(HDCivilizationConstants.FAILURE)){
                    throw new ContentException(getActionKeyName());
                }else if(state.equals(HDCivilizationConstants.SHIELD)){
//                    throw new ContentException("话题被屏蔽!");
                    throw new ContentException(getActionKeyName());
                }else{
                    throw new ContentException(getActionKeyName());
                }
            }
        } catch (JSONException e) {
            throw new JsonParseException(getActionKeyName());
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
