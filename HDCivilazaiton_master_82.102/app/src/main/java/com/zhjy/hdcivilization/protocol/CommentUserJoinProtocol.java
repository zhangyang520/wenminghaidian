package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.ImgEntityDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/30.
 *         文明评论参与话题的协议类
 */
public class CommentUserJoinProtocol extends BaseProtocol<List<HDC_CommentDetail>> {

    private List<ImgEntity> imgUrlList;
    private ImgEntity imgEntity;
    private List<HDC_CommentDetail> datas;
    private User launchUser;

    private String targetUserId;
    private boolean isJoinThemeType;//是否是参与的话题

    public String getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(String targetUserId) {
        this.targetUserId = targetUserId;
    }

    public boolean isJoinThemeType() {
        return isJoinThemeType;
    }

    public void setIsJoinThemeType(boolean isJoinThemeType) {
        this.isJoinThemeType = isJoinThemeType;
    }

    @Override
    protected List<HDC_CommentDetail> parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            String status=valuesObject.getString("status");
            if (status.equals(HDCivilizationConstants.STATUS_0)) {

                throw new ContentException(getActionKeyName()+"!");
            } else if (status.equals(HDCivilizationConstants.STATUS_2)) {
                //此时这种情况；权限过低只能是 userPermissions=-1
                throw new ContentException(getActionKeyName()+"!");
            } else {
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

                //首先进行根据链接的个数进行判断iten种类
                JSONObject infoObject = jsonObject.getJSONObject("info");
                String listString = infoObject.getString("List");
                JSONArray listArray=new JSONArray(listString);
                List<HDC_CommentDetail> datas = new ArrayList<HDC_CommentDetail>();
                User targetUser=null;
                if(!isJoinThemeType){
                    //如果是发表的话题
                    targetUser=UserDao.getInstance().getUserId(targetUserId);
                    if(targetUser==null){
                        throw new ContentException("本地数据库没有发表话题用户数据!");
                    }
                }
                for (int i = 0; i < listArray.length(); ++i) {

                    //进行遍历infoArray
                    HDC_CommentDetail commentDetail = new HDC_CommentDetail();

                    JSONObject listObject= listArray.getJSONObject(i);
                    String itemId = listObject.getString("itemId");
                    commentDetail.setItemId(itemId);

                    //进行设置条目id和类型
                    commentDetail.setItemIdAndType(itemId+commentDetail.getItemType());

                    /***图片的Url***/
                    JSONArray imgUrlArray = listObject.getJSONArray("imgArray");
                    imgUrlList = new ArrayList<ImgEntity>();
                    String imgItemId="";
                    for (int j = 0; j < imgUrlArray.length(); j++) {
                        JSONObject imgUrlJSONObject =imgUrlArray.getJSONObject(j);
                        imgItemId= imgUrlJSONObject.getString("imgId");
                        imgEntity=ImgEntityDao.getInstance().getImgEntity(imgItemId);
                        if(imgEntity==null){
                            imgEntity = new ImgEntity();
                            imgEntity.setItemId(imgItemId);
                            imgEntity.setImgThumbUrl(imgUrlJSONObject.getString("thumbUrl"));
                            imgEntity.setImgUrl(imgUrlJSONObject.getString("imgUrl"));
                            imgEntity.setItemIdAndItemType(commentDetail.getItemIdAndType());
                        }else{
                            imgEntity.setItemId(imgItemId);
                            imgEntity.setImgThumbUrl(imgUrlJSONObject.getString("thumbUrl"));
                            imgEntity.setImgUrl(imgUrlJSONObject.getString("imgUrl"));
                            imgEntity.setItemIdAndItemType(commentDetail.getItemIdAndType());
                        }
                        imgUrlList.add(imgEntity);
                    }
                    ImgEntityDao.getInstance().saveAll(imgUrlList);
                    //进行设置图片的集合
                    commentDetail.setImgUrlList(imgUrlList);
                    //进行设置主题
                    String title = listObject.getString("title");
                    commentDetail.setTitle(title);

                    //消息提醒个数
                    //点赞个数
                    int count = listObject.getInt("count");
                    commentDetail.setCount(count);

                    //进行设置内容
                    long commentCount = listObject.getLong("commentCount");
                    commentDetail.setCommentCount((int)commentCount);

                    String content = listObject.getString("content");
                    commentDetail.setContent(content);

                    if(isJoinThemeType){
                        //如果是参与的话题
                        String name = listObject.optString("Name");
                        String userId = listObject.getString("userId");
                        String userState=listObject.getString("userState");
                        launchUser =UserDao.getInstance().getUserId(userId);
                        if(launchUser ==null){
                            launchUser = new User();
                            launchUser.setNickName(name);
                            launchUser.setUserId(userId);
                            launchUser.setIdentityState(userState);
                        }else{
                            launchUser.setNickName(name);
                            launchUser.setIdentityState(userState);
                        }
                        UserDao.getInstance().saveUpDate(launchUser);
                        //进行设置发表用户实体
                        commentDetail.setTopicType(HDCivilizationConstants.JOIN_TOPIC_TYPE);
                        commentDetail.setLaunchUser(launchUser);
                        User partInUser=UserDao.getInstance().getUserId(targetUserId);
                        commentDetail.setParticipate(partInUser);
                    }else{
                        int tipCount = listObject.getInt("tipCount");
                        commentDetail.setTypeCount(tipCount);
                        //那么是发表的话题 设置发表用户
                        commentDetail.setTopicType(HDCivilizationConstants.SUB_TOPIC_TYPE);
                        commentDetail.setLaunchUser(targetUser);
                    }
                    datas.add(commentDetail);
                }

                //进行获取"参与的话题"-或者-"发表的话题"
                int subThemeCount=infoObject.getInt("subThemeCount");//进行获取发布话题个数
                int partInThemeCount=infoObject.getInt("partInThemeCount");//进行获取发布话题个数
                //是否是参与的话题
                User user=UserDao.getInstance().getUserId(targetUserId);
                if(user!=null){
                    //进行设置参与话题个数
                    user.setJoinThemeCount(partInThemeCount);
                    user.setSubThemeCount(subThemeCount);
                    UserDao.getInstance().saveUpDate(user);
                }else{
                    throw new ContentException("目标用户找不到!");
                }
                return datas;
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
