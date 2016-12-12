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
 * @author :huangxianfeng on 2016/7/28.
 * 文明评论的热门话题解析类
 */
public class CommentHotProtocol extends BaseProtocol<List<HDC_CommentDetail>> {

    private List<ImgEntity> imgUrlList;

    private ImgEntity imgEntity;

    private User launchUser;


    @Override
    protected List<HDC_CommentDetail> parseJson(String jsonStr) throws JsonParseException, ContentException {
            try {
                System.out.println("1111111111111CommentHotProtocol...jsonStr = ");
                JSONObject jsonObject = new JSONObject(jsonStr);
                System.out.println("2222222222222CommentHotProtocol...jsonStr = ");
                System.out.println("CommentHotProtocol...jsonStr = " + jsonObject.toString());
                JSONObject valuesObject = jsonObject.getJSONObject("values");
                String status=valuesObject.getString("status");
                System.out.println("CommentHotProtocol...userPermission = " + ".....status:"+status);
                if (status.equals(HDCivilizationConstants.STATUS_0)) {

                    throw new ContentException(getActionKeyName()+"!");
                } else if (status.equals(HDCivilizationConstants.STATUS_2)) {
                    //进行前后台userid不一致
                    throw new ContentException(getActionKeyName()+"!");
                } else {
                    String userPermission = valuesObject.getString("userPermission");
                    try {
                        //如果此时进行更新权限值 获取本地用户
                        User user = UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
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

                        System.out.println("commentDetail setItemIdAndType:"+(itemId+commentDetail.getItemType()));
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

                        //置顶排序:>0时需要置顶
                        int topOrder = listObject.getInt("topOrder");//
                        if(topOrder>0){
                            //如果大于0，进行置顶
                            commentDetail.setIsTopType(true);
                        }else{
                            commentDetail.setIsTopType(false);
                        }
                        commentDetail.setTopValue(topOrder);
                        System.out.println("CommentHotProtocol...........half............itemId:"+itemId);
                        String name = listObject.optString("Name");
                        String userId = listObject.getString("userId");
                        String userState=listObject.getString("userState");
                        launchUser=UserDao.getInstance().getUserId(userId);
                        String portraitUrl="";
                        JSONObject portraitUrlObj=listObject.optJSONObject("portraitObj");
                        if(portraitUrlObj!=null && !portraitUrlObj.isNull("imgUrl")){
                             portraitUrl=portraitUrlObj.getString("imgUrl");
                            System.out.println("CommentHotProtocol...........half............portraitUrl:" + portraitUrl);

                        }
                        if(launchUser==null){
                            launchUser = new User();
                            launchUser.setNickName(name);
                            launchUser.setUserId(userId);
                            launchUser.setIdentityState(userState);
                            launchUser.setPortraitUrl(portraitUrl);
                        }else{
                            launchUser.setNickName(name);
                            launchUser.setIdentityState(userState);
                            launchUser.setPortraitUrl(portraitUrl);
                        }
                        UserDao.getInstance().saveUpDate(launchUser);
                        commentDetail.setLaunchUser(launchUser);

                        //进行设置热门话题类型
                        commentDetail.setTopicType(HDCivilizationConstants.HOT_TOPIC_TYPE);
                        datas.add(commentDetail);
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
