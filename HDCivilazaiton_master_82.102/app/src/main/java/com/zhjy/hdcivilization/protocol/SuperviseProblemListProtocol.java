package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.dao.UserDao;
import com.zhjy.hdcivilization.entity.HDC_SuperviseMySubList;
import com.zhjy.hdcivilization.entity.HDC_SuperviseProblemList;
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
 * Created by zhangyang on 2016/7/30.
 */
public class SuperviseProblemListProtocol extends BaseProtocol<List<HDC_SuperviseProblemList>> {

    public SuperviseProblemListProtocol() {
    }

    @Override
    protected List<HDC_SuperviseProblemList> parseJson(String jsonStr) throws JsonParseException, ContentException {

        try {
            System.out.println("SuperviseProblemListProtocol parseJson:11111111111");
            JSONObject jsonObject = new JSONObject(jsonStr);
            System.out.println("SuperviseProblemListProtocol parseJson:"+jsonObject.toString());
            JSONObject valuesObject = jsonObject.getJSONObject("values");
//            String userPermission = valuesObject.getString("userPermission");
            String status=valuesObject.getString("status");
            System.out.println("SuperviseMineListProtocol...userPermission = "+ ".....status:"+status);
            if (status.equals(HDCivilizationConstants.STATUS_0)) {
                throw new ContentException(getActionKeyName()+"!");
            } else if (status.equals(HDCivilizationConstants.STATUS_2)) {
                String userPermission = valuesObject.getString("userPermission");
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
                List<HDC_SuperviseProblemList> listDatas=new ArrayList<HDC_SuperviseProblemList>();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
                Calendar calendar=Calendar.getInstance();
                for (int i=0;i<jsonObject1.length();++i){
                    JSONObject jsonObject2=jsonObject1.getJSONObject(i);
                    HDC_SuperviseProblemList data=new HDC_SuperviseProblemList();
                    data.setProblemCount(jsonObject2.getInt("subProCountPerDay"));
                    data.setProblemCoin(jsonObject2.getInt("coinCountPerDay"));
                    data.setTotalCoin(jsonObject2.getInt("totalCoinCount"));
                    data.setVerifiedCountPerDay(jsonObject2.getInt("verifiedCountPerDay"));
                    calendar.setTimeInMillis(jsonObject2.getLong("time"));
                    data.setDate(simpleDateFormat.format(calendar.getTime()));
                    listDatas.add(data);
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
