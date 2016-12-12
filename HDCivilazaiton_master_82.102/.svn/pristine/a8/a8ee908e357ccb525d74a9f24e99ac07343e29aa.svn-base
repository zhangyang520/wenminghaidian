package com.zhjy.hdcivilization.utils;

import com.zhjy.hdcivilization.exception.ContentException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * huangxianfeng
 */

public class HttpUtil {

    private static HttpUtil instance;

    private HttpUtil() {

    }

    public static HttpUtil getInstance() {
        if (instance == null) {
            synchronized (HttpUtil.class) {
                if (instance == null) {
                    instance = new HttpUtil();
                }
            }
        }
        return instance;
    }


    /**
     * http://127.0.0.1:8080/FileLoadData/servlet/FileLoadServlet
     * http://192.168.80.148:8080/FileLoadData/servlet/FileLoadServlet
     * http://10.0.2.2:8080/FileLoadData/servlet/FileLoadServlet
     */


    // 超时时间
    public int CONNTIOMEOUT = 10 * 1000;

    public String userAgent = "http://192.168.80.148:8080/FileLoadData/servlet/FileLoadServlet";

    /***
     * 登录界面
     */
    public String LOGINURL = "http://192.168.80.148:8080/AroundServer/servlet/AroundServlet";

    public String COLLECTION = "http://192.168.80.148:8080/AroundServer/servlet/AroundServlet";

    public static String NEAR = "http://192.168.80.112:8080/AroundServer/servlet/AroundServlet?";


    public static String HDCivilication = "http://127.0.0.1:8090/";

    //http://127.0.0.1:8090/img?name=autopage_lv_1.jpg


    /**
     * 描述：从服务器获取数据
     */
    public String readMyInputStream(InputStream is) {
        byte[] result;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            is.close();
            baos.close();
            result = baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            String errorStr = "获取数据失败。";
            return errorStr;
        }
        return new String(result);
    }


    /**
     * 把UniCode转换成中文
     */
    public String ascii2native(String ascii) {
        int n = ascii.length() / 6;
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0, j = 2; i < n; i++, j += 6) {
            String code = ascii.substring(j, j + 4);
            char ch = (char) Integer.parseInt(code, 16);
            sb.append(ch);
        }
        return sb.toString();
    }

    public String readString(InputStream inputStream) throws ContentException {

        BufferedInputStream bis = null;
        ByteArrayOutputStream baos = null;
        try {
            bis = new BufferedInputStream(inputStream);
            int len = -1;
            byte[] buffers = new byte[1024];
            baos = new ByteArrayOutputStream();
            while ((len = bis.read(buffers)) != -1) {
                baos.write(buffers, 0, len);
            }
            return baos.toString();
        } catch (Exception e) {
            e.printStackTrace();
            ContentException contentException=new ContentException("读取数据失败!");
            FileUtils.getInstance().saveCrashInfo2File(contentException);
            throw contentException;
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
