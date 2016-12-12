package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.ApplyCheckDao;
import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_ApplyCheck;
import com.zhjy.hdcivilization.entity.User;
import com.zhjy.hdcivilization.entity.UserPermisson;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.DateUtil;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :huangxianfeng on 2016/8/11.
 * 申请查询协议类
 */
public class ApplyCheckProtocol extends BaseProtocol<HDC_ApplyCheck> {

    private HDC_ApplyCheck applyCheck;
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    protected HDC_ApplyCheck parseJson(String jsonStr) throws JsonParseException, ContentException {
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            System.out.println("ApplyCheckProtocol  valuesObject="+jsonObject.toString());
            String status=valuesObject.getString("status");
            if (status.equals(HDCivilizationConstants.STATUS_0)){
                throw new ContentException("申请信息查询失败！");
            }else if (status.equals(HDCivilizationConstants.STATUS_2)){
                String userPermission = valuesObject.getString("userPermission");
                if (userPermission.equals(UserPermisson.UNKNOW_VALUE.getType()) && !getUserId().equals("") ){
                    //进行前后台userid不一致
                    throw new ContentException("申请信息查询失败！");
                }else{
                    user.setIdentityState(userPermission);
                    UserDao.getInstance().saveUpDate(user);
                }
                throw new ContentException(HDCivilizationConstants.LOW_PERMISSION_ERROR_CODE,"您的账户权限过低！");
            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                String userPermission = valuesObject.getString("userPermission");

                user.setIdentityState(userPermission);
                //解析数据
                String approvalState = infoObj.getString("approvalState");
                long applyTime = infoObj.getLong("applyTime");//申请时间
                long modifyTime = infoObj.optLong("modifyTime");//修改时间
                String determinereason = infoObj.getString("determinereason");
                applyCheck=ApplyCheckDao.getInstance().getApprovalState(user.getUserId());
                if(applyCheck==null){
                    //如果为空
                    applyCheck = new HDC_ApplyCheck();
                    applyCheck.setUserId(user.getUserId());
                }
                applyCheck.setApprovalState(approvalState);//申请状态
//                applyCheck.setApprovalState("4");//申请状态
                applyCheck.setApplyDate(DateUtil.getInstance().getDayOrMonthOrYear2(applyTime));//申请的日期如：7月5日
                applyCheck.setApplyTime(DateUtil.getInstance().getMinuteOrSeconds(applyTime) + " " + DateUtil.getInstance().getTimeDifference(applyTime));//申请的时间如：14:50 PM
                applyCheck.setModifyDate(DateUtil.getInstance().getDayOrMonthOrYear2(modifyTime));//修改的日期：7月5日
                applyCheck.setModifyTime(DateUtil.getInstance().getMinuteOrSeconds(modifyTime) + " " + DateUtil.getInstance().getTimeDifference(applyTime));//修改的时间如：14:50 PM
                applyCheck.setDeterminereason(determinereason);
                UserDao.getInstance().saveUpDate(user);
                ApplyCheckDao.getInstance().saveObj(applyCheck);
                return applyCheck;
            }
        } catch (JSONException e) {
            System.out.println("ApplyCheckProtocol e.getMessage() = "+ e.getMessage());
            throw new JsonParseException("申请信息查询失败！");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
