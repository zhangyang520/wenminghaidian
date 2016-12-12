package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SysControl;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/26.
 * 首页数字请求协议类
 */
public class MainNumberProtocol extends BaseProtocol<HDC_MainNumber> {
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected HDC_MainNumber parseJson(String jsonStr) throws JsonParseException, ContentException {
        System.out.println("MainNumberProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valuesObject= jsonObject.getJSONObject("values");
            System.out.println("valuesObject.toString = " + valuesObject.toString());
            if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if (valuesObject.getString("status").equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("")){
                    //进行前后台userid不一致
                    throw new ContentException(getActionKeyName()+"!");
                }else{
                        //如果此时进行更新权限值 获取本地用户
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                    UserDao.getInstance().updateAllUserLocalState(false);
                }
                throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"权限过低！");
            }else{
                    String userPermission = valuesObject.getString("userPermission");
                    //如果此时进行更新权限值 获取本地用户
                    user.setIdentityState(userPermission);
                    System.out.println("save user:" + user.toString());
                    UserDao.getInstance().saveUpDate(user);
                    //解析数据
                    JSONObject infoObj = jsonObject.getJSONObject("info");
                    JSONArray list = infoObj.getJSONArray("list");
                    HDC_MainNumber mainNumber;
                    int allNumber=0;
                    int superviseCount=0;
                    try {
                        //如果数据库有对应的数据--进行获取
                        mainNumber=MainNumberDao.getInstance().getNumberBy(user.getUserId());
                    } catch (ContentException e) {
                        e.printStackTrace();
                        //如果数据库没有对应的数据--进行新建
                        mainNumber = new HDC_MainNumber();
                        mainNumber.setUser(user.getUserId());
                    }
                    for (int i = 0; i < list.length(); i++) {
                            JSONObject listObj = list.getJSONObject(i);
                            System.out.println("listObj =" + listObj.toString());

                            String type  = listObj.getString("type");
                            String tipCount  = listObj.getString("tipCount");

                            if(HDCivilizationConstants.NUMBER_NOTIFY_KEY.equals(type)){//服务器对应的是"我的模块的消息提醒个数"
                                //如果是类型4所有的部分 服务器端传递"我的模块"中的消息提醒个数
                                allNumber=Integer.parseInt(tipCount);
                            }else if(HDCivilizationConstants.NUMBER_SUPERVISE_KEY.equals(type)){
                                //文明监督的个数:
                                superviseCount=Integer.parseInt(tipCount);
                                mainNumber.setSuperviseCount(superviseCount);
                            }else if(HDCivilizationConstants.NUMBER_COMMENT_KEY.equals(type)){
                                //文明评论的个数
                                mainNumber.setCommentCount(Integer.parseInt(tipCount));
                            }else if(HDCivilizationConstants.NUMBER_STATE_KEY.equals(type)){
                                //文明动态的个数
                                mainNumber.setStateCount(Integer.parseInt(tipCount));
                            }else{
                                System.out.println("MainNumberProtocol eles other type");
                            }
                     }
                     //最后进行添加通知公告的个数:
                     mainNumber.setNotifyCount(allNumber - superviseCount);
                     //最后保存数据
                     MainNumberDao.getInstance().saveNumber(mainNumber);
                     return mainNumber;
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage() = "+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
