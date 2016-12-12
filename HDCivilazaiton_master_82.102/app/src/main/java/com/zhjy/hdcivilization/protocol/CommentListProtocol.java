package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_UserCommentList;
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
 * @author :huangxianfeng on 2016/8/8.
 * 文明评论列表解析类
 */
public class CommentListProtocol extends BaseProtocol<List<HDC_UserCommentList>> {

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
    protected List<HDC_UserCommentList> parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valueJSONObject=jsonObject.getJSONObject("values");
            String status=valueJSONObject.getString("status");
            System.out.println("jsonObject==="+jsonObject.toString());
            if (status.equals(HDCivilizationConstants.STATUS_0)) {
                throw new ContentException(getActionKeyName()+"！");
            }else if (status.equals(HDCivilizationConstants.STATUS_2)){
                //进行前后台userid不一致
                throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,getActionKeyName()+"!");
            }else{
                String userPermission = valueJSONObject.getString("userPermission");
                try {
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject1=jsonObject.getJSONObject("info");
                List<HDC_UserCommentList> listDatas=new ArrayList<HDC_UserCommentList>();
                JSONArray listJSON = jsonObject1.getJSONArray("list");

                for (int i=0;i<listJSON.length();++i){
                    JSONObject jsonObject2 = new JSONObject(listJSON.get(i).toString());
                    //进行循环列表
                    userCommentList=new HDC_UserCommentList();
                    String imgUrl = jsonObject2.getString("imgUrl");
                    String userId = jsonObject2.getString("userId");
                    String nickName = jsonObject2.optString("nickName");
                    String userState = jsonObject2.optString("userState");
                    launchUser = UserDao.getInstance().getUserId(userId);
                    if (launchUser==null){
                        launchUser = new User();
                        launchUser.setUserId(userId);
                        launchUser.setNickName(nickName);
                        launchUser.setPortraitUrl(imgUrl);
                        launchUser.setIdentityState(userState);
                        UserDao.getInstance().saveAll(launchUser);
                        userCommentList.setUser(launchUser);
                    }else{
                        launchUser.setUserId(userId);
                        launchUser.setNickName(nickName);
                        launchUser.setIdentityState(userState);
                        launchUser.setPortraitUrl(imgUrl);
                        userCommentList.setUser(launchUser);
                    }
                    userCommentList.setContent(jsonObject2.getString("content"));
                    userCommentList.setItemId(jsonObject2.getString("itemId"));
                    userCommentList.setPublishTime(jsonObject2.getLong("time"));
                    userCommentList.setCount(jsonObject2.getInt("totalCount"));
                    userCommentList.setTopicItemIdAndType(itemIdAndType);
                    userCommentList.setFatherItemId(fatherItemId);
                    listDatas.add(userCommentList);
                }
                return  listDatas;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new JsonParseException(getActionKeyName()+"！");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
