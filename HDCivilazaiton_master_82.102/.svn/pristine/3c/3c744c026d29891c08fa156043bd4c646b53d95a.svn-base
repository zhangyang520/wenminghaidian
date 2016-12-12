package com.zhjy.hdcivilization.protocol;

import com.zhjy.hdcivilization.entity.HDC_CommentDetail;
import com.zhjy.hdcivilization.entity.ImgEntity;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.inner.BaseProtocol;
import com.zhjy.hdcivilization.utils.DateUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author :huangxianfeng on 2016/7/29.
 */
public class CommentDetailProtocol extends BaseProtocol<HDC_CommentDetail> {

    private HDC_CommentDetail commentDetail;

    private ImgEntity imgEntity;

    @Override
    protected HDC_CommentDetail parseJson(String jsonStr) throws JsonParseException, ContentException {
        System.out.println("CommentDetailProtocol...jsonStr = " + jsonStr);
        try {
            JSONObject jsonObject=new JSONObject(jsonStr);
            JSONObject valuesObject=jsonObject.getJSONObject("values");
            if(valuesObject.getString("status").equals("1")){
                //首先进行根据链接的个数进行判断iten种类
                JSONObject infoObj = jsonObject.getJSONObject("info");

                String title = infoObj.getString("title");
                String publishTime = infoObj.getString("publishTime");
                int count = infoObj.getInt("count");

                JSONArray imgUrlArray = infoObj.getJSONArray("imgUrl");
                List<ImgEntity> imgList = new ArrayList<ImgEntity>();
                for (int i = 0; i < imgUrlArray.length() ; i++) {
                    String imgUrl = (String)imgUrlArray.get(i);
                    imgEntity = new ImgEntity();
                    imgEntity.setImgThumbUrl(imgUrl);
                    imgList.add(imgEntity);
                }

                String content = infoObj.getString("content");

                commentDetail = new HDC_CommentDetail();
                commentDetail.setTitle(title);
                commentDetail.setPublishTime(DateUtil.getInstance().getMinuteOrSeconds(System.currentTimeMillis()));
                commentDetail.setCount(count);
                commentDetail.setImgUrlList(imgList);
                commentDetail.setContent(content);
                return commentDetail;
            }else{
                throw new ContentException(valuesObject.getString("msg"));
            }
        } catch (JSONException e) {
            System.out.println("e.getMessage() = " + e.getMessage());
            throw new JsonParseException("数据格式传输错误!");
        }
    }

    @Override
    public String getkey() {
        return null;
    }
}
