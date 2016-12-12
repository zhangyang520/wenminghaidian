package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.ImgEntityDao;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zhangyang on 2016/8/1.
 */
public class CiviStateListProtocol extends BaseProtocol<List<HDC_CiviState>> {
    private ImgEntity imgEntity;
    private HDC_CiviState civiState;
    private List<HDC_CiviState> datas;

    public CiviStateListProtocol() {
    }

    @Override
    protected List<HDC_CiviState> parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            System.out.println("CiviStateListProtocol parseJson toString:"+jsonObject.toString());
            JSONObject valueJSONObject=jsonObject.getJSONObject("values");
            if (valueJSONObject.getString("status").equals(HDCivilizationConstants.STATUS_0) ||
                            valueJSONObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                throw new ContentException(getActionKeyName()+"!");
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
                //解析数据
                datas = new ArrayList<HDC_CiviState>();
                String infoString=jsonObject.getString("info");
                if(infoString.trim().equals("")){
                    return datas;
                }
                JSONObject infoObj = new JSONObject(infoString);
                JSONArray list = infoObj.getJSONArray("list");
                SimpleDateFormat simpleDateFormat1=new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat simpleDateFormat2=new SimpleDateFormat("MM-dd");
                SimpleDateFormat simpleDateFormat3=new SimpleDateFormat("HH:mm");
                String currentDay= simpleDateFormat1.format(Calendar.getInstance().getTime());
                for (int i = 0; i < list.length(); i++) {
                    JSONObject listObj = list.getJSONObject(i);
                    System.out.println("listObj =" + listObj.toString());
                    String itemId = listObj.getString("itemId");
                    civiState = new HDC_CiviState();
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
                    String imgId = imgObj.optString("imgId");
                    String imgUrl = imgObj.optString("imgUrl");
                    String thumbUrl = imgObj.optString("thumbUrl");
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
                        civiState.setImgEntity(imgEntity);
                    }
                    civiState.setPublishTimeLong(publishTime);
                    civiState.setTitle(title);
                    civiState.setItemId(itemId);
                    civiState.setDes(des);
                    civiState.setTipCount(tipCount);
                    civiState.setPublishTime(publishDay);
                    civiState.setDianZanCount(count);
                    civiState.setItemIdAndType(itemId+civiState.getItemType());
                    datas.add(civiState);
                }
                return datas;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
