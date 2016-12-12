package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.ImgEntityDao;
import com.zhjy.hdcivilization.dao.SuperviseMySubListDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_SuperviseMySubList;
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

import java.net.ConnectException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by zhangyang on 2016/7/29.
 */
public class SuperviseMineListProtocol extends BaseProtocol<List<HDC_SuperviseMySubList>>{

    private ImgEntity imgEntity;

    public SuperviseMineListProtocol() {
    }

    @Override
    protected List<HDC_SuperviseMySubList> parseJson(String jsonStr) throws JsonParseException, ContentException {
            try {

                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONObject valuesObject = jsonObject.getJSONObject("values");
//                String userPermission = valuesObject.getString("userPermission");
                System.out.println("SuperviseMineListProtocol running jsonObject = "+ jsonObject.toString());
                String status=valuesObject.getString("status");
                System.out.println("SuperviseMineListProtocol...userPermission = "  + ".....status:"+status+"..valuesTostinrg:"+valuesObject.toString());
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
                        } catch (ContentException e) {
                            e.printStackTrace();
                        }
                        if(userPermission.equals(UserPermisson.ORDINARYSTATE.getType())){
                            throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"已经降为普通用户!");
                        }else{
                            throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,HDCivilizationConstants.FORBIDDEN_USER);
                        }
                    }
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

                    JSONObject infoObj = jsonObject.getJSONObject("info");
                    System.out.println("infoObj toString:"+infoObj.toString());
                    String listString = infoObj.getString("list");
                    JSONArray jsonObject1=new JSONArray(listString);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm");
                    List<HDC_SuperviseMySubList> listDatas = new ArrayList<HDC_SuperviseMySubList>();
                    for (int i = 0; i < jsonObject1.length(); ++i) {
                        JSONObject jsonObject2 = jsonObject1.getJSONObject(i);
                        //进行循环列表
                        HDC_SuperviseMySubList hdc_superviseMySubList = new HDC_SuperviseMySubList();
                        hdc_superviseMySubList.setAddress(jsonObject2.getString("address"));
                        hdc_superviseMySubList.setDescription(jsonObject2.getString("description"));
                        String itemId=jsonObject2.getString("itemId");
                        hdc_superviseMySubList.setItemIdAndType(hdc_superviseMySubList.getItemType() + itemId);
                        String imgArrays = jsonObject2.getString("imgArray");
                        JSONArray imgArray=new JSONArray(imgArrays);
                        //进行取一个图片:
                        List<ImgEntity> imageEntityList=null;
                        if(imgArray.length()>0){
                            imageEntityList=new ArrayList<ImgEntity>();
                            for(int j=0;j<imgArray.length();++j){
                                JSONObject imgJsonObject=imgArray.getJSONObject(j);
                                String imgId=imgJsonObject.getString("imgId");
                                imgEntity= ImgEntityDao.getInstance().getImgEntity(imgId);
                                if(imgEntity==null){
                                    imgEntity = new ImgEntity();
                                    imgEntity.setImgThumbUrl(imgJsonObject.getString("thumbUrl"));
                                    imgEntity.setImgUrl(imgJsonObject.getString("imgUrl"));
                                    imgEntity.setItemId(imgJsonObject.getString("imgId"));
                                    imgEntity.setItemIdAndItemType(hdc_superviseMySubList.getItemIdAndType());
                                }else{
                                    imgEntity.setImgThumbUrl(imgJsonObject.getString("thumbUrl"));
                                    imgEntity.setImgUrl(imgJsonObject.getString("imgUrl"));
                                    imgEntity.setItemId(imgJsonObject.getString("imgId"));
                                    imgEntity.setItemIdAndItemType(hdc_superviseMySubList.getItemIdAndType());
                                }
                                imageEntityList.add(imgEntity);
                            }
                        }
                        ImgEntityDao.getInstance().saveAll(imageEntityList);
                        hdc_superviseMySubList.setImgEntity(imageEntityList);
                        //进行设置类型和itemId
                        hdc_superviseMySubList.setItemId(itemId);
                        String processState=jsonObject2.getString("processState");
                        try {
                            int processInteger=Integer.parseInt(processState);
                            if(HDCivilizationConstants.SUBMIT_TASK_STATUS_0<=processInteger &&
                                                processInteger<=HDCivilizationConstants.SUBMIT_TASK_STATUS_5){

                                hdc_superviseMySubList.setProcessState(processInteger+"");
                            }else{
                                //先查询数据库中有无该条目
                                try {
                                    HDC_SuperviseMySubList data=SuperviseMySubListDao.getInstance().getItemBy(itemId);
                                    processInteger=Integer.parseInt(data.getProcessState());
                                    if(HDCivilizationConstants.SUBMIT_TASK_STATUS_0<=processInteger &&
                                            processInteger<=HDCivilizationConstants.SUBMIT_TASK_STATUS_5){
                                        hdc_superviseMySubList.setProcessState(processInteger+"");
                                    }else{
                                        //以前的数字就是数字,不在范围之内
                                        hdc_superviseMySubList.setProcessState(HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT + "");
                                    }
                                } catch (ConnectException e) {
                                    e.printStackTrace();
                                    //没有就设置缺省值
                                    hdc_superviseMySubList.setProcessState(HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT + "");
                                }catch (NumberFormatException e){
                                    //设置缺省值
                                    hdc_superviseMySubList.setProcessState(HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT + "");
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            //非数字
                            hdc_superviseMySubList.setProcessState(HDCivilizationConstants.SUBMIT_TASK_STATUS_DEFAULT + "");
                        }
//                     hdc_superviseMySubList.setProcessState(jsonObject2.getString("processState"));
                        //设置事件类型
                        hdc_superviseMySubList.setFirstEventType(jsonObject2.getString("firstEventType"));
                        hdc_superviseMySubList.setSecondEventType(jsonObject2.getString("secondEventType"));
                        hdc_superviseMySubList.setStreetBelong(jsonObject2.getString("streetBelong"));
                        //进行获取失败原因:
                        hdc_superviseMySubList.setUnPassReason(jsonObject2.getString("unPassReason"));
                        hdc_superviseMySubList.setUserId(getUserId());
                        long dateTime=jsonObject2.getLong("date");
                        Calendar calendar=Calendar.getInstance();
                        calendar.setTimeInMillis(dateTime);
                        hdc_superviseMySubList.setPublishTime(simpleDateFormat.format(calendar.getTime()));
                        listDatas.add(hdc_superviseMySubList);
                    }
                    return listDatas;
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
