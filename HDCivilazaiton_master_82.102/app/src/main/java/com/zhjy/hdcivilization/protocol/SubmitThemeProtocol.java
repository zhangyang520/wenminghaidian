package com.zhjy.hdcivilization.protocol;

import android.util.Log;

import com.zhjy.hdcivilization.dao.CommentDao;
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
 * 发布话题的协议类
 * Created by zhangyang on 2016/7/28.
 */
public class SubmitThemeProtocol extends BaseProtocol<String> {


    @Override
    public String parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            System.out.println("SubmitThemeProtocol...jsonStr = " + jsonObject.toString());
            JSONObject valuesObject = jsonObject.getJSONObject("values");
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)) {
                throw new ContentException(getActionKeyName()+"!");
            } else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)) {
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("")) {
                    //进行前后台userid不一致
                    throw new ContentException(getActionKeyName()+"!");
                } else {
                    //如果此时进行更新权限值 获取本地用户
                    try {
                        User user = UserDao.getInstance().getLocalUser();
                        user.setIdentityState(userPermission);
                        UserDao.getInstance().saveUpDate(user);
                    } catch (ContentException e) {
                        e.printStackTrace();
                    }
                }
                throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE, HDCivilizationConstants.FORBIDDEN_USER);
            } else {
                JSONObject infoObj = jsonObject.getJSONObject("info");
                try {
                    String userPermission = valuesObject.getString("userPermission");
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ContentException e) {
                    e.printStackTrace();
                }

                //其他情况应该是保存成功
                String state = infoObj.getString("state");
                if (state.equals(HDCivilizationConstants.SUCCESS)) {

                    //传输成功
                    //进行进一步解析发布的内容:
                    JSONObject themeJsonObj = infoObj.getJSONObject("theme");//进行获取话题的实体
                    System.out.println("SubmitThemeProtocol themeJsonObj:" + (themeJsonObj==null));
//                    if (!themeJsonObj.toString().equals("")){
//
//                    }else{
                        System.out.println("SubmitThemeProtocol themeObj toString:" + themeJsonObj.toString());
                        HDC_CommentDetail commentDetail = new HDC_CommentDetail();
                        //进行设置主题
                        String itemId = themeJsonObj.getString("itemId");
                        commentDetail.setItemId(itemId);
                        //进行设置条目id和类型
                        commentDetail.setItemIdAndType(itemId + commentDetail.getItemType());

                        String title = themeJsonObj.getString("title");
                        commentDetail.setTitle(title);

                        //消息提醒个数
                        int tipCount = themeJsonObj.getInt("tipCount");
                        commentDetail.setTypeCount(tipCount);

                        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
                        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("MM-dd");
                        SimpleDateFormat simpleDateFormat3 = new SimpleDateFormat("HH:mm");
                        String currentDay = simpleDateFormat1.format(Calendar.getInstance().getTime());
                        long publishTime = themeJsonObj.getLong("publishTime");
                        //进行解析时间:当天时间 设置小时, 其他:设置日期
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(publishTime);
                        String publishDay = simpleDateFormat1.format(calendar.getTime());
                        if (!currentDay.equals(publishDay)) {
                            publishDay = simpleDateFormat2.format(calendar.getTime());
                        } else {
                            publishDay = simpleDateFormat3.format(calendar.getTime());
                        }
                        commentDetail.setPublishTime(publishDay);
                        commentDetail.setOrderTime(publishTime);

                        //点赞个数
                        int count = themeJsonObj.getInt("count");
                        commentDetail.setCount(count);

                        //进行设置内容
                        String content = themeJsonObj.getString("content");
                        commentDetail.setContent(content);

                        JSONArray imgUrlArray = themeJsonObj.getJSONArray("imgArray");
                        List<ImgEntity> imgUrlList = new ArrayList<ImgEntity>();
                        String imgItemId = "";
                        ImgEntity imgEntity;
                        for (int j = 0; j < imgUrlArray.length(); j++) {
                            JSONObject imgUrlJSONObject = imgUrlArray.getJSONObject(j);
                            imgItemId = imgUrlJSONObject.optString("imgId");
                            String thumbUrl=imgUrlJSONObject.optString("thumbUrl");
                            String imgUrl=imgUrlJSONObject.optString("imgUrl");
                            if(!imgItemId.trim().equals("") && !thumbUrl.trim().equals("")  && !imgUrl.trim().equals("")){
                                imgEntity = ImgEntityDao.getInstance().getImgEntity(imgItemId);
                                if (imgEntity == null) {
                                    imgEntity = new ImgEntity();
                                    imgEntity.setItemId(imgItemId);
                                    imgEntity.setImgThumbUrl(thumbUrl);
                                    imgEntity.setImgUrl(imgUrl);
                                    imgEntity.setItemIdAndItemType(commentDetail.getItemIdAndType());
                                } else {
                                    imgEntity.setItemId(imgItemId);
                                    imgEntity.setImgThumbUrl(thumbUrl);
                                    imgEntity.setImgUrl(imgUrl);
                                    imgEntity.setItemIdAndItemType(commentDetail.getItemIdAndType());
                                }
                                imgUrlList.add(imgEntity);
                                ImgEntityDao.getInstance().saveAll(imgUrlList);
                                //进行设置图片的集合
                                commentDetail.setImgUrlList(imgUrlList);
                            }
                        }
                        //进行设置热门话题类型
                        commentDetail.setTopicType(HDCivilizationConstants.SUB_TOPIC_TYPE);
                        try {
                            System.out.println("SubmitThemeProtocol local User: 11111111111111111111");
                            User user = UserDao.getInstance().getLocalUser();
                            System.out.println("SubmitThemeProtocol local User:" + user.toString());
                            commentDetail.setLaunchUser(user);
                            CommentDao.getInstance().saveObj_1(commentDetail);
                            //再添加一个热门话题的条目
                            commentDetail.setTopicType(HDCivilizationConstants.HOT_TOPIC_TYPE);
                            CommentDao.getInstance().saveObj_1(commentDetail);
                            System.out.println("SubmitThemeProtocol commentDetail toString:" + commentDetail.toString());
                            try {
                                System.out.println("SubmitThemeProtocol local User:" + UserDao.getInstance().getAll().toString());
                            } catch (ContentException e) {
                                e.printStackTrace();
                                System.out.println("SubmitThemeProtocol local No User List......");
                            }
                        } catch (ContentException e) {
                            e.printStackTrace();
//                        throw new RuntimeException("用户未登录!");
                            System.out.println("SubmitThemeProtocol 用户未登录!");
                        }
//                    }
                    return "";

                } else if (state.equals(HDCivilizationConstants.FAILURE)) {
                    throw new ContentException(infoObj.getString("msg"));
                } else if (state.equals(HDCivilizationConstants.SENSITIVECONTENT)) {
                    throw new ContentException("内容中含有敏感词信息!");
                } else {
                    throw new ContentException("发表话题失败!");
                }
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage()===" + e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
