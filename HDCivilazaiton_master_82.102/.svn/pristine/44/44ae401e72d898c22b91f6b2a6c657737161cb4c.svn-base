package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.ImgEntityDao;
import com.zhjy.hdcivilization.dao.NoticeDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_CiviState;
import com.zhjy.hdcivilization.entity.HDC_Notice;
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
 * @author :huangxianfeng on 2016/8/22.
 * 通知公告的协议类
 */
public class NoticeProtocol extends BaseProtocol<List<HDC_Notice>> {


    private ImgEntity imgEntity;
    private HDC_Notice notice;
    private List<HDC_Notice> datas;

    public NoticeProtocol() {
    }

    @Override
    protected List<HDC_Notice> parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            System.out.println("jsonObject======="+jsonObject.toString());
            JSONObject valueJSONObject=jsonObject.getJSONObject("values");
            if (valueJSONObject.getString("status").equals(HDCivilizationConstants.STATUS_0) ||
                                    valueJSONObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                throw new ContentException(getActionKeyName()+"!");
            }else{
                try {
                    String userPermission = valueJSONObject.getString("userPermission");
                    //如果此时进行更新权限值 获取本地用户
                    User user = UserDao.getInstance().getLocalUser();
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                } catch (ContentException e) {
                    e.printStackTrace();
                }

                //解析数据
                datas = new ArrayList<HDC_Notice>();
                String infoString=jsonObject.getString("info");
                if(infoString.trim().equals("")){
                    return datas;
                }
                JSONObject infoObj = new JSONObject(infoString);
                System.out.println("infoObj======="+infoObj.toString());
                JSONArray list = infoObj.getJSONArray("list");
                SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("MM-dd");
                SimpleDateFormat simpleDateFormat3=new SimpleDateFormat("HH:mm");
                String currentDay= simpleDateFormat1.format(Calendar.getInstance().getTime());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject listObj = list.getJSONObject(i);
                    System.out.println("listObj =" + listObj.toString());
                    String itemId = listObj.getString("itemId");
                    notice = new HDC_Notice();
                    notice.setUserId(getUserId());
                    String title = listObj.getString("title");
                    int tipCount = listObj.getInt("tipCount");
                    long publishTime = listObj.getLong("publishTime");
                    int count = listObj.getInt("count");
                    String des = listObj.getString("des");

                    //进行解析时间:当天时间 设置小时, 其他:设置日期
                    Calendar calendar=Calendar.getInstance();
                    calendar.setTimeInMillis(publishTime);
                    String publishDay= simpleDateFormat1.format(calendar.getTime());
                    if(!currentDay.equals(publishDay)){
                        publishDay=simpleDateFormat2.format(calendar.getTime());
                    }else{
                        publishDay=simpleDateFormat3.format(calendar.getTime());
                    }
                    //图片
                    JSONObject imgObj = listObj.getJSONObject("imgObj");
                    System.out.println("imgObj="+imgObj.toString());
                    String imgUrl = imgObj.optString("imgUrl");
                    String thumbUrl = imgObj.optString("thumbUrl");
                    String imgId = imgObj.optString("imgId");
                    if(!imgUrl.trim().equals("") && !thumbUrl.trim().equals("") && !imgId.trim().equals("")){
                        imgEntity= ImgEntityDao.getInstance().getImgEntity(imgId);
                        if(imgEntity==null){
                            imgEntity = new ImgEntity();
                            imgEntity.setImgUrl(imgUrl);
                            imgEntity.setImgThumbUrl(thumbUrl);
                            imgEntity.setItemId(imgId);
                        }else{
                            imgEntity.setImgUrl(imgUrl);
                            imgEntity.setImgThumbUrl(thumbUrl);
                            imgEntity.setItemId(imgId);
                        }
                        ImgEntityDao.getInstance().saveImgObj(imgEntity);
                        notice.setImgEntity(imgEntity);
                    }
                    notice.setPublishTimeLong(publishTime);
                    notice.setTitle(title);
                    notice.setItemId(itemId);
                    notice.setDes(des);
                    notice.setTipCount(tipCount);
                    notice.setPublishTime(publishDay);
                    notice.setDianZanCount(count);

                    //进行设置条目类型和id
                    notice.setItemIdAndType(itemId+notice.getItemType());
                    datas.add(notice);
                }
                return datas;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("e.getMessage()="+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
