package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author :huangxianfeng on 2016/8/2.
 * 发送手机验证码
 */
public class SendCodeProtocol extends BaseProtocol<String>{


    @Override
    protected String parseJson(String jsonStr) throws JsonParseException, ContentException {
        System.out.println("SendCodeProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            String status=valuesObject.getString("status");
            System.out.println("SendCodeProtocol parseJson  userPermission:"+jsonObject.toString()+"...status:"+status);
            if (status.equals(HDCivilizationConstants.STATUS_0) || status.equals(HDCivilizationConstants.STATUS_2)){
                throw new ContentException(getActionKeyName()+"!");
            }else{
                JSONObject infoObj = jsonObject.getJSONObject("info");
                //解析数据
                String state = infoObj.getString("state");
                String msg = infoObj.getString("msg");
                if (HDCivilizationConstants.SUCCESS.equals(state)){
                    return "";
                }else{
                    throw new ContentException(msg);
                }
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
