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
 * @author :huangxianfeng on 2016/7/29.
 */
public class CommentMineProtocol extends BaseProtocol<List<HDC_CommentDetail>>{

    private List<ImgEntity> imgUrlList;
    private ImgEntity imgEntity;

    @Override
    protected List<HDC_CommentDetail> parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            System.out.println("CommentMineProtocol...jsonStr = " + jsonObject.toString());
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            String status=valuesObject.getString("status");
            System.out.println("CommentMineProtocol...userPermission = " + ".....status:"+status);
            if (status.equals(HDCivilizationConstants.STATUS_0)) {

                throw new ContentException(getActionKeyName()+"!");
            } else if (status.equals(HDCivilizationConstants.STATUS_2)) {
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("")) {
                    //进行前后台userid不一致
                    throw new ContentException(getActionKeyName()+"!");
                }else{
                    try {
                        //如果此时进行更新权限值 获取本地用户
                        User user = UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                        System.out.println("save local user："+user.toString());
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                }
                throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,HDCivilizationConstants.FORBIDDEN_USER);
            } else {
                String userPermission = valuesObject.getString("userPermission");
                User user=null;
                try {
                    //如果此时进行更新权限值 获取本地用户
                    user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    System.out.println("save local user:" + user.toString());
                    UserDao.getInstance().saveUpDate(user);

                    if (userPermission.equals(UserPermisson.VOLUNTEER.getType()) && user.getVolunteerId().equals("")){
                        //更新状态的机制
                        System.out.println("CommentMineProtocol 1111更新状态的机制");
                    }
                } catch (ContentException e) {
                    e.printStackTrace();
                }
//                    //首先进行根据链接的个数进行判断iten种类
                JSONObject infoObject = jsonObject.getJSONObject("info");
                String listString = infoObject.getString("List");
                JSONArray listArray=new JSONArray(listString);
                List<HDC_CommentDetail> datas = new ArrayList<HDC_CommentDetail>();
                SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("MM-dd");
                SimpleDateFormat simpleDateFormat3=new SimpleDateFormat("HH:mm");
                String currentDay= simpleDateFormat1.format(Calendar.getInstance().getTime());
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
                    int tipCount = listObject.getInt("tipCount");
                    commentDetail.setTypeCount(tipCount);

                    int commentCount = listObject.getInt("commentCount");
                    commentDetail.setCommentCount(commentCount);

                    long publishTime = listObject.getLong("publishTime");
                    //进行解析时间:当天时间 设置小时, 其他:设置日期
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTimeInMillis(publishTime);
                    String publishDay= simpleDateFormat1.format(calendar.getTime());
                    if(!currentDay.equals(publishDay)){
                        publishDay=simpleDateFormat2.format(calendar.getTime());
                    }else{
                        publishDay=simpleDateFormat3.format(calendar.getTime());
                    }
                    commentDetail.setPublishTime(publishDay);
                    commentDetail.setOrderTime(publishTime);

                    //点赞个数
                    int count = listObject.getInt("count");
                    commentDetail.setCount(count);

                    //进行设置内容
                    String content = listObject.getString("content");
                    commentDetail.setContent(content);

                    //进行设置对应的发表的用户
                    System.out.println("我的话题: 发起者 userId:" + user.getUserId());
                    commentDetail.setLaunchUser(user);

                    //进行设置话题类型
                    commentDetail.setTopicType(HDCivilizationConstants.SUB_TOPIC_TYPE);
                    datas.add(commentDetail);
                }
                return datas;
            }
        } catch (JSONException e) {
            System.out.println("CommentMinePro e.getMessage() = "+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
