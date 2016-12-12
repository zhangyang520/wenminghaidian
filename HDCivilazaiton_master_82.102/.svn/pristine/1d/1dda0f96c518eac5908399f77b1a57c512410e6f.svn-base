package com.zhjy.hdcivilization.inner;

import com.zhjy.hdcivilization.entity.UrlParamsEntity;
import com.zhjy.hdcivilization.exception.ContentException;
import com.zhjy.hdcivilization.exception.JsonParseException;
import com.zhjy.hdcivilization.utils.FileUtils;
import com.zhjy.hdcivilization.utils.HDCivilizationConstants;
import com.zhjy.hdcivilization.utils.HttpUtil;
import com.zhjy.hdcivilization.utils.ZipUtil;

import org.apache.http.conn.ConnectTimeoutException;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

/**
 * <br/>
 *
 * @类描述： 基类，封装这边请求数据的方法和缓存本地数据的方法<br/>
 * @创建人：HXF
 */
public abstract class BaseProtocol<T> {
    private long netWorktime;
    private String userId="";
    String actionKeyName="";//关键操作字
    String keyName="";//关键字

    public String getActionKeyName() {
        return actionKeyName;
    }

    public void setActionKeyName(String actionKeyName) {
        this.actionKeyName = actionKeyName;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public long getNetWorktime() {
        return netWorktime;
    }

    public void setNetWorktime(long netWorktime) {
        this.netWorktime = netWorktime;
    }

    public T load(String urlStr, int index) throws JsonParseException, ContentException {
        System.out.println("BaseProtocol...urlStr = " + urlStr);
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                String jsonStr = HttpUtil.getInstance().readString(conn.getInputStream());
                netWorktime = System.currentTimeMillis();
                return parseJson(jsonStr);
            } else {
                throw new ContentException("请求服务器失败!");
            }
        } catch (SocketTimeoutException e) {
            throw new ContentException("链接超时!");
        } catch (IOException e) {
            e.printStackTrace();
            /**网络连接超时的异常!*/
            System.out.println("e.getMessage()=" + e.getMessage());
            throw new ContentException("请求服务器失败!");
        }
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * 解析JSON交给子类
     */
    protected abstract T parseJson(String jsonStr) throws JsonParseException, ContentException;

    //通过webService进行解析soapObject
    protected T parseJson(SoapObject soapObject) throws JsonParseException, ContentException {

        return null;
    }

    /**
     * 将内容保存到本地:
     * 但是并不是所有都要进行保存到本地
     *
     * @param json
     * @throws JsonParseException
     */
    protected void saveLocal(String json, int index) throws JsonParseException {
        //把每条数据标志id id按照数据进行递增
        //请求服务器  看看服务器的id值和本地的id值 比对如果发现一样就不更新
        //把数据一次性缓存到本地每隔一段时间，去更新数据，保存到本地，给保存到本地的文件写一个过期时间
        FileWriter fw = null;
        try {
            File file = new File(FileUtils.getInstance().getCacheDir(), getkey() + "_" + index);
            fw = new FileWriter(file);
            /**设置本地数据过期的时间为：120秒，写在数据的最前面，供本地读取的时候用到*/
            fw.write(System.currentTimeMillis() + 2 * 60 * 1000 + "\r\n");
            fw.write(json);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            FileUtils.getInstance().close(fw);
        }
    }

    public T upLoad(UrlParamsEntity urlParamEntity, String path) throws JsonParseException, ContentException {

        String end ="\r\n";
        String twoHyphens ="--";
        String boundary ="*****";

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String,String> entity : urlParamEntity.getParamsHashMap().entrySet()) {
            //进行拼接参数
            sb.append(entity.getKey() + "=" + entity.getValue() + "&");
        }
        sb.deleteCharAt(sb.length() - 1);
        String urlStr = urlParamEntity.UPLOAD_IMG_URL + sb.toString();
        String fileName=path.substring(path.lastIndexOf(File.separator) + 1, path.length());
        System.out.println("urlStr=" + urlStr+"..fileName:"+fileName);
        BufferedInputStream bis = null;
        try {
            File file = new File(path);
            if (!file.exists()) {
                throw new ContentException("本地文件不存在!");
            }
            //Content-Type:application/x-www-form-urlencoded
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
//            conn.setRequestProperty("Content-Type", "binary/octet-stream");
            conn.setDoOutput(true);

            DataOutputStream ds =
                    new DataOutputStream(conn.getOutputStream());
            ds.writeBytes(twoHyphens + boundary + end);
            ds.writeBytes("Content-Disposition: form-data; " +
                    "name=\"file1\";filename=\"" +
                    fileName + "\"" + end);
            ds.writeBytes(end);

            OutputStream os = conn.getOutputStream();
            //进行读文件:
            bis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffers = new byte[1024];
            int len = 0;
            while ((len = bis.read(buffers)) != -1) {
                System.out.println("upLoad count:"+len);
                os.write(buffers, 0, len);
            }
            ds.writeBytes(end);
            ds.writeBytes(twoHyphens + boundary + twoHyphens + end);
            bis.close();
            os.close();

            conn.connect();
            if (conn.getResponseCode() == 200) {
                String jsonStr = HttpUtil.getInstance().readString(conn.getInputStream());
                /**将文件进行关闭**/
//				File file = new File(path);
//				file.delete();
                /**保存本地*/
                //saveLocal(jsonStr , index);
                /**进行解析JSON**/
                return parseJson(jsonStr);
            } else {
                System.out.println("conn.getResponseCode():..."+conn.getResponseCode());
                /**服务器端异常**/
                throw new ContentException(conn.getResponseCode());
            }
        } catch (SocketTimeoutException e){
            e.printStackTrace();
            throw new ContentException("连接超时!");
        }catch (IOException e) {
            e.printStackTrace();
            throw new ContentException("打开链接失败!");
        }
    }


    //进行访问webServicec的服务端
    public T loadDataFromWebService(UrlParamsEntity urlParamEntity) throws ContentException, JsonParseException {
        // 命名空间
        String nameSpace = urlParamEntity.getNamingSpace();
        // 调用的方法名称
        String methodName = urlParamEntity.getMethodName();
        // EndPoint
        String endPoint = urlParamEntity.getWebServiceUrl();
        // SOAP Action
        String soapAction = nameSpace + methodName;

        // 指定WebService的命名空间和调用的方法名
        SoapObject rpc = new SoapObject(nameSpace, methodName);

        // 设置需调用WebService接口需要传入的参数 设置参数
        if (urlParamEntity.getParamsHashMap() != null) {
            for (Map.Entry<String, String> entity : urlParamEntity.getParamsHashMap().entrySet()) {
                //进行设置参数
                System.out.println("key:" + entity.getKey() + "..value:" + entity.getValue());
                rpc.addProperty(entity.getKey(), entity.getValue());
            }
        }
        // 生成调用WebService方法的SOAP请求信息,并指定SOAP的版本
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        envelope.bodyOut = rpc;
        // 设置是否调用的是dotNet开发的WebService
        envelope.dotNet = false;
        // 等价于envelope.bodyOut = rpc;
        envelope.setOutputSoapObject(rpc);
        HttpTransportSE transport = new HttpTransportSE(endPoint);
        try {
            // 调用WebService
            transport.call(soapAction, envelope);
            if (envelope.bodyIn instanceof SoapFault) {
                //失败信息服务端出现问题
                final String str = ((SoapFault) envelope.bodyIn).faultstring;
                System.out.println("(SoapFault) envelope.bodyIn).faultstring = " + str);
                throw new ContentException("获取数据失败!");
            } else {
                final SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
//				   //进行将bodyIn传递的结果字符串--->转换为可以用json进行解析的字符串
                System.out.println("right result:" + String.valueOf(resultsRequestSOAP.toString()));
                //进行解析
                return parseJson(resultsRequestSOAP);
            }

        } catch (SocketTimeoutException e) {
            System.out.println("链接超时异常" + e.getMessage());
            throw new ContentException("链接超时,请查看网络");
        } catch (IOException e) {
            e.printStackTrace();
            //网络连接超时的异常!
            throw new ContentException("请查看网络");
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            throw new ContentException("获取数据失败!");
        }
    }

    /**
     * 获取唯一数据key名字
     **/
    public abstract String getkey();

    /**
     * 文明海淀
     **/
    public T loadData(UrlParamsEntity urlParamsEntity) throws JsonParseException, ContentException {
        try {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entity : urlParamsEntity.getParamsHashMap().entrySet()) {
                //进行拼接参数
                sb.append(entity.getKey() + "=" + entity.getValue() + "&");
            }
            sb.deleteCharAt(sb.length() - 1);
            String urlStr = urlParamsEntity.HDCURL + sb.toString();
            System.out.println("url = " + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
            conn.setReadTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                String jsonStr = HttpUtil.getInstance().readString(conn.getInputStream());
                netWorktime = System.currentTimeMillis();
                return parseJson(jsonStr);
            } else {
                ContentException e=new ContentException(getActionKeyName() + "!");
                FileUtils.getInstance().saveCrashInfo2File(e);
                throw  e;
            }
        } catch (SocketTimeoutException e) {
            FileUtils.getInstance().saveCrashInfo2File(e);
            throw new ContentException(getActionKeyName() + ",请求超时!");
        } catch (ConnectTimeoutException e) {
            FileUtils.getInstance().saveCrashInfo2File(e);
            throw new ContentException(getActionKeyName() + ",链接超时!");
        } catch (IOException e) {
            e.printStackTrace();
            FileUtils.getInstance().saveCrashInfo2File(e);
            /**网络连接超时的异常!*/
            throw new ContentException(getActionKeyName() + ",请查看网络!");
        }
    }

    /**
     * 文明海淀
     **/
    public T loadData(UrlParamsEntity urlParamsEntity,int netWorktimeout) throws JsonParseException, ContentException {
        try {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entity : urlParamsEntity.getParamsHashMap().entrySet()) {
                //进行拼接参数
                sb.append(entity.getKey() + "=" + entity.getValue() + "&");
            }
            sb.deleteCharAt(sb.length() - 1);
            String urlStr = urlParamsEntity.HDCURL + sb.toString();
            System.out.println("url = "+urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(netWorktimeout);
            conn.setReadTimeout(netWorktimeout);
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                String jsonStr = HttpUtil.getInstance().readString(conn.getInputStream());
                netWorktime = System.currentTimeMillis();
                return parseJson(jsonStr);
            } else {
                throw new ContentException(getActionKeyName()+"!");
            }
        } catch (SocketTimeoutException e) {
            throw new ContentException(getActionKeyName()+",链接超时!");
        } catch (IOException e) {
            e.printStackTrace();
            /**网络连接超时的异常!*/
            throw new ContentException(getActionKeyName()+",请查看网络!");
        }
    }
    /**
     * 文明海淀
     **/
    public T loadData(UrlParamsEntity urlParamsEntity,String actionkeyName) throws JsonParseException, ContentException {
        this.actionKeyName=actionkeyName;
        try {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entity : urlParamsEntity.getParamsHashMap().entrySet()) {
                //进行拼接参数
                sb.append(entity.getKey() + "=" + entity.getValue() + "&");
            }
            sb.deleteCharAt(sb.length() - 1);
            String urlStr = urlParamsEntity.HDCURL + sb.toString();
            System.out.println("url = "+urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
            conn.setReadTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
            conn.setRequestMethod("GET");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                String jsonStr = HttpUtil.getInstance().readString(conn.getInputStream());
                netWorktime = System.currentTimeMillis();
                return parseJson(jsonStr);
            } else {
                throw new ContentException(actionkeyName+"!");
            }
        } catch (SocketTimeoutException e) {
            throw new ContentException(actionkeyName+",请求超时!");
        } catch (ConnectTimeoutException e) {
            throw new ContentException(getActionKeyName() + ",链接超时!");
        }catch (IOException e) {
            e.printStackTrace();
            /**网络连接超时的异常!*/
            throw new ContentException(actionkeyName+",请查看网络!");
        }
    }
    /**
     * 文明海淀
     **/
    public T loadDataPost(UrlParamsEntity urlParamsEntity) throws JsonParseException, ContentException {
        try {
            String urlStr = urlParamsEntity.UPLOAD_IMG;
            System.out.println("urlStr=" + urlStr);
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
            conn.setReadTimeout(HDCivilizationConstants.NEWWORK_TIME_OUT);
            conn.setRequestMethod("POST");
            StringBuilder sb = new StringBuilder();
            conn.connect();
            DataOutputStream dataOutputStream = new DataOutputStream(conn.getOutputStream());
            for (Map.Entry<String, String> entity : urlParamsEntity.getParamsHashMap().entrySet()) {
                //进行拼接参数
                System.out.println("entity.getKey() =" + entity.getKey()+";entity.getValue() = " + entity.getValue());
                sb.append(entity.getKey() + "=" + entity.getValue() + "&");
            }
            sb.deleteCharAt(sb.length() - 1);

            dataOutputStream.writeBytes(new String(sb.toString().getBytes(),"UTF-8"));
            dataOutputStream.flush();
            dataOutputStream.close();
            if (conn.getResponseCode() == 200) {
                String jsonStr = HttpUtil.getInstance().readString(conn.getInputStream());
                System.out.println("baseProtocol_jsonStr = " + jsonStr);

                if (jsonStr == null) {
                    throw new ContentException("数据为空!");
                }
                netWorktime = System.currentTimeMillis();
                System.out.println("baseProtocol_jsonStr = " + jsonStr);
                return parseJson(jsonStr);
            } else {
                System.out.println("conn.getResponseCode() = " + conn.getResponseCode() + conn.getResponseMessage());
                throw new ContentException("获取数据失败!");
            }
        } catch (SocketTimeoutException e) {
            System.out.println("链接超时异常" + e.getMessage());
            throw new ContentException("链接超时!");
        } catch (IOException e) {
            e.printStackTrace();
            /**网络连接超时的异常!*/
            System.out.println("e.getMessage()=" + e.getMessage());
            throw new ContentException("请求服务器失败!");
        }
    }
}
