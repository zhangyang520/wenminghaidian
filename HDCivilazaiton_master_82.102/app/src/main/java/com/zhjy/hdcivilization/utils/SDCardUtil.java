package com.zhjy.hdcivilization.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * 
 * @项目名：AroundYou 
 * @类名称：SDCardUtil   
 * @类描述：   对SD卡的操作
 * @创建人：huangxianfeng
 * @修改人：    
 * @创建时间：2016-7-19 下午5:11:52
 * @version    
 *
 */
public class SDCardUtil {
	private static SDCardUtil instance;
	public static SDCardUtil getInstance(){
		if (instance == null){
			synchronized(SDCardUtil.class){
				if (instance == null){
					instance = new SDCardUtil();
				}
			}
		}
		return instance;
	}
	private String AppRootPath,debugPath,iconPath,picPath,uploadPicPath,downloadPath;
	private SDCardUtil(){
		//应用程序根目录
		AppRootPath = getRootPath();

		//bug存储文件目录
		debugPath=AppRootPath + File.separator + HDCivilizationConstants.DEBUGPATH;
		iconPath=AppRootPath + File.separator + HDCivilizationConstants.ICON_PATH;
		picPath = AppRootPath + File.separator+ HDCivilizationConstants.PIC_PATH;
		uploadPicPath = AppRootPath + File.separator+ HDCivilizationConstants.UP_LOAD_PIC;
		downloadPath = AppRootPath + File.separator+ HDCivilizationConstants.DOWNLOAD_PATH;
	}


	/**
	 * 检测SD卡是否存在
	 * @return
	 */
	public boolean isExitsSdcard() {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	/**
	 * 通过指定目录创建文件夹
	 * @param dirPath
	 */
	public void makeDirs(String dirPath){
		File file = new File(dirPath);
		if (!file.exists()) {
			file.mkdirs();
		}
	}

	/**
	 * 获取系统默认存储目录
	 * @return
	 */
	public  String getRootPath()
	{
		String filepath = "";
//		if(isExitsSdcard())
//			filepath = getSDCardPath()+File.separator+ HDCivilizationConstants.ROOTPATH+File.separator;
//		else {
//			filepath = getRootDirectoryPath()+File.separator+ HDCivilizationConstants.ROOTPATH+File.separator;
//		}
		filepath = getRootDirectoryPath()+File.separator+ HDCivilizationConstants.ROOTPATH+File.separator;
		File f = new File(filepath);
		if(!f.exists() || !f.isDirectory()){
			f.mkdirs();
		}
		return filepath;
	}

	private  String getSDCardPath()
	{
		return Environment.getExternalStorageDirectory().getAbsolutePath()
				+ File.separator;
	}

	/**
	 * 获取系统存储路径
	 *
	 * @return
	 */
	private  String getRootDirectoryPath()
	{
		return UiUtils.getInstance().getContext().getFilesDir().getAbsolutePath();
	}
	/**
	 * 
	 * @描述:根据对应的路径读取txt文件
	 * @方法名：getJsonDataForSD
	 * @创建时间：2015-9-16 下午4:33:32
	 */
	public String getJsonDataForSD(String jsonPath){
		StringBuffer sb = new StringBuffer();
		File file = new File(jsonPath);
		if ((file.exists() && file.isFile())) {
			InputStreamReader read;
			BufferedReader bufferedReader = null;
			try {
				read = new InputStreamReader(new FileInputStream(file), "UTF-8");
				bufferedReader = new BufferedReader(read);
				String lineTxt = "";
				while ((lineTxt = bufferedReader.readLine()) != null) {
					sb.append(lineTxt);
				}
				return sb.toString();
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if ( bufferedReader != null) {
					try {
						bufferedReader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}

	/**
	 * 进行获取debugPath的路径
	 * @return
	 */
	public String getDebugPath() {
		makeDirs(debugPath);
		return debugPath;
	}

	public void setDebugPath(String debugPath) {
		this.debugPath = debugPath;
	}

	public String getIconPath() {
		makeDirs(iconPath);
		return iconPath;
	}

	public void setIconPath(String iconPath) {
		this.iconPath = iconPath;
	}

	public String getPicPath() {
		makeDirs(picPath);
		return picPath;
	}

	public String getUploadPicPath(){
		makeDirs(uploadPicPath);
		return uploadPicPath;
	}

	/**
	 * 进行获取下载的路径
	 * @return
	 */
	public String getDownloadPath(){
		makeDirs(downloadPath);
		return downloadPath;
	}

	/**
	 * 进行获取xmlpath
	 * @return
	 */
}
