package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.MainNumberDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_MainNumber;
import com.zhjy.hdcivilization.entity.ImgEntity;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.SharedPreferencesManager;
import com.zhjy.hdcivilization.utils.UiUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :huangxianfeng on 2016/8/23.
 * 我的主界面信息协议类
 */
public class MineProtocol extends BaseProtocol<String> {

    private ImgEntity imgEntity;

    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected String parseJson(String jsonStr) throws JsonParseException, ContentException {
        System.out.println("MineProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            System.out.println("MineProtocol...jsonStr = " + jsonObject.toString());
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            String status=valuesObject.getString("status");
            System.out.println("MineProtocol parseJson  userPermission:"+"...status:"+status);
            if (status.equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException(getActionKeyName()+"!");
            }else if (status.equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("") ){
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
                    throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,HDCivilizationConstants.FORBIDDEN_USER);
                }
            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                String userPermission = valuesObject.getString("userPermission");
                 user.setIdentityState(userPermission);
                //解析数据
                String notifiyTipCount = infoObj.getString("notifiyTipCount");//通知公告消息提醒个数
                String goldCoin = infoObj.getString("goldCoin");//金币数量
                String presentRules = infoObj.getString("presentRules");
                System.out.println("MineProtocol presentRules"+presentRules);
                //对我的金币界面的提现规则进行获取保存
                SharedPreferencesManager.put(UiUtils.getInstance().getContext(),HDCivilizationConstants.PRESENTRULES,presentRules);
                if(!infoObj.isNull("nickName")){
                    System.out.println("....nickName infoObj.isNull false");
                    String nickName=infoObj.getString("nickName");
                    if(nickName.equalsIgnoreCase("null")||nickName.equals("")){
//                        System.out.println("....nickName infoObj.isNull false nickName:"+nickName);
                    }else {
                        user.setNickName(nickName);
//                        System.out.println("....nickName infoObj.isNull false nickName:" + nickName+"...setUserNickName");
                    }
                }else{
//                    System.out.println("....nickName infoObj.isNull true");
                }
                String nickName = infoObj.getString("nickName");//昵称
                String subTipCount = infoObj.getString("subTipCount");//我的上报消息提醒个数

                JSONObject protraitObj = infoObj.getJSONObject("protraitObj");//头像的信息
                if(!protraitObj.isNull("imageId") &&
                            !protraitObj.isNull("thumbUrl") &&
                                            !protraitObj.isNull("imgUrl")){
                    String  imageId= protraitObj.getString("imageId");
                    String  thumbUrl= protraitObj.getString("thumbUrl");
                    String  imgUrl= protraitObj.getString("imgUrl");
                    user.setPortraitUrl(imgUrl);
                }
//                System.out.println("goldCoin=" + goldCoin);
                user.setGoldCoin(goldCoin);
                UserDao.getInstance().saveUpDate(user);
                HDC_MainNumber mainNumber= null;
                try {
                    mainNumber = MainNumberDao.getInstance().getNumberBy(user.getUserId());
                    mainNumber.setNotifyCount(Integer.parseInt(notifiyTipCount));
                    mainNumber.setSuperviseCount(Integer.parseInt(subTipCount));
                    MainNumberDao.getInstance().saveNumber(mainNumber);
                } catch (ContentException e) {
                    e.printStackTrace();
                    mainNumber=new HDC_MainNumber();
                    mainNumber.setNotifyCount(Integer.parseInt(notifiyTipCount));
                    mainNumber.setSuperviseCount(Integer.parseInt(subTipCount));
                    mainNumber.setUser(user.getUserId());
                    MainNumberDao.getInstance().saveNumber(mainNumber);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                return "";
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage()"+e.getMessage());
            throw new JsonParseException(getActionKeyName()+"!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
