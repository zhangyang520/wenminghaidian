package com.zhjy.hdcivilization.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * @author :huangxianfeng on 2016/7/6.
 */
public class FileImageUpload {

    private static FileImageUpload instance;

    private FileImageUpload() {
    }

    public static FileImageUpload getInstance() {
        if (instance == null) {
            synchronized (FileImageUpload.class) {
                if (instance == null) {
                    instance = new FileImageUpload();
                }
            }
        }
        return instance;
    }

    private final String TAG = "uploadFile";
    private final int TIME_OUT = 10 * 10000000; //超时时间
    private final String CHARSET = "utf-8"; //设置编码
    public final String SUCCESS = "SUCCESS";
    public final String FAILURE = "FAILURE";

    /**
     * android上传文件到服务器
     *
     * @param RequestURL 请求的rul
     * @return 返回响应的内容
     */
    public String uploadFile(ByteArrayInputStream inputStream, String RequestURL) {
        System.out.println("FileImageUpload 1");
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
        String PREFIX = "--",
                LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型
        try {
            System.out.println("FileImageUpload 2");
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);//设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            System.out.println("FileImageUpload 3");
            /** * 当文件不为空，把文件包装并且上传 */
            OutputStream outputSteam = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputSteam);
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意：
             * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + "type.jpg" + "\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
            sb.append(LINE_END);
//            dos.write(sb.toString().getBytes());
//                InputStream is = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            System.out.println("FileImageUpload 4");
            while ((len = inputStream.read(bytes)) != -1) {
                System.out.println("FileImageUpload 5");
                dos.write(bytes, 0, len);
                System.out.println("FileImageUpload len = " + len);
            }
            System.out.println("FileImageUpload 6");
            inputStream.close();
//            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
//            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功
             * 当响应成功，获取响应的流
             */
            conn.connect();
            System.out.println("FileImageUpload 7");
            int res = conn.getResponseCode();
            Log.e(TAG, "response code:" + res);
            if (res == 200) {
                System.out.println("FileImageUpload 8");
                return SUCCESS;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("FileImageUpload 9");
        } catch (IOException e) {
            System.out.println("FileImageUpload 10");
            e.printStackTrace();
        }
        return FAILURE;
    }


    public String uploadFileFor(String RequestURL,String filePath) {

        System.out.println("FileImageUpload 1");
        String BOUNDARY = UUID.randomUUID().toString(); //边界标识 随机生成
        String PREFIX = "--",
                LINE_END = "\r\n";
        String CONTENT_TYPE = "multipart/form-data"; //内容类型

        try {
            System.out.println("FileImageUpload 2");
            URL url = new URL(RequestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(TIME_OUT);
            conn.setConnectTimeout(TIME_OUT);
            conn.setDoInput(true); //允许输入流
            conn.setDoOutput(true); //允许输出流
            conn.setUseCaches(false); //不允许使用缓存
            conn.setRequestMethod("POST"); //请求方式
            conn.setRequestProperty("Charset", CHARSET);//设置编码
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
            System.out.println("FileImageUpload 3");
            /** * 当文件不为空，把文件包装并且上传 */
            OutputStream outputSteam = conn.getOutputStream();
            DataOutputStream dos = new DataOutputStream(outputSteam);
            StringBuffer sb = new StringBuffer();
            sb.append(PREFIX);
            sb.append(BOUNDARY);
            sb.append(LINE_END);
            /**
             * 这里重点注意：
             * name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
             * filename是文件的名字，包含后缀名的 比如:abc.png
             */
            sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + "type.jpg" + "\"" + LINE_END);
            sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
            sb.append(LINE_END);
//            dos.write(sb.toString().getBytes());
//                InputStream is = new FileInputStream(file);

            String picPath = SDCardUtil.getInstance().getIconPath();
            File file = new File(picPath,filePath);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = new byte[1024];
            int len = 0;
            System.out.println("FileImageUpload 4");
            while ((len = inputStream.read(bytes)) != -1) {
                System.out.println("FileImageUpload 5");
                dos.write(bytes, 0, len);
                System.out.println("FileImageUpload len = " + len);
            }
            System.out.println("FileImageUpload 6");
            inputStream.close();
//            dos.write(LINE_END.getBytes());
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
//            dos.write(end_data);
            dos.flush();
            /**
             * 获取响应码 200=成功
             * 当响应成功，获取响应的流
             */
            conn.connect();
            System.out.println("FileImageUpload 7");
            int res = conn.getResponseCode();
            Log.e(TAG, "response code:" + res);
            if (res == 200) {
                System.out.println("FileImageUpload 8");
                InputStream is = conn.getInputStream();
                String string = getStringForService(is);
                return SUCCESS;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("FileImageUpload 9");
        } catch (IOException e) {
            System.out.println("FileImageUpload 10");
            e.printStackTrace();
        }
        return FAILURE;
    }

    /**
     * 读取服务器端返回来的数据
     * @param inputStream
     * @return
     */
    public String getStringForService(InputStream inputStream){
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s;
            StringBuffer sb = new StringBuffer();
            while ((s=bufferedReader.readLine()) != null){
                sb.append(s);
            }
            bufferedReader.close();
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
