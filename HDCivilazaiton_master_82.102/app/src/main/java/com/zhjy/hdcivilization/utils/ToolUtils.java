package com.zhjy.hdcivilization.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
/**
 * 
 * @项目名：AroundYou 
 * @类名称：ToolUtils   
 * @类描述：   普通工具方法的集合类
 * @创建人：HXF   
 * @修改人：    
 * @创建时间：2015-12-4 上午10:18:30  
 * @version    
 *
 */
public class ToolUtils {


	private static ToolUtils instance;

	private ToolUtils(){

	}

	public static ToolUtils getInstance(){
		if (instance == null){
			synchronized(ToolUtils.class){
				if (instance == null){
					instance = new ToolUtils();
				}
			}
		}
		return instance;
	}

	/**
	 * 
	 * 描述：正则表达式识别电话号码是否输入正确
	 * @param mobiles
	 * @return boolean
	 */
	public boolean isMobileNo(String mobiles) {
		/**
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 * "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		 */
		String telRegex = "[1][34578]\\d{9}";
		/**判断是否为空字符串*/
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		} else {
			return mobiles.matches(telRegex);
		}
	}
	/**
	 * 
	 * 描述：正则表达式验证有数字和字母的密码
	 */
	public boolean isPasswordNo(String string){
		String checkPassword = "^[A-Za-z]+$";
		if (TextUtils.isEmpty(checkPassword)) {
			return false;
		}else{
			return checkPassword.matches(string);
		}
	}

	/**
	 * 
	 * 描述：汉字的识别表达式
	 * @param chinese
	 * @return boolean
	 */
	public boolean isChineseNo(String chinese) {
		Pattern pattern = Pattern.compile("^[\u4e00-\u9fa5]*$ ");
		Matcher matcher = pattern.matcher(chinese);
		if (matcher.find()) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 描述：强制关闭软键盘
	 * @param editText
	 */
	public void closeKeyBoard(EditText editText) {
		@SuppressWarnings("static-access")
		InputMethodManager imm = (InputMethodManager) UiUtils.getInstance().getContext()
				.getSystemService(UiUtils.getInstance().getContext().INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}

	public void openKeyBoard(EditText editText) {
		@SuppressWarnings("static-access")
		InputMethodManager imm = (InputMethodManager) UiUtils.getInstance().getContext()
				.getSystemService(UiUtils.getInstance().getContext().INPUT_METHOD_SERVICE);
		imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
	}
	/**
	 * 描述：设置吐司
	 * @param string
	 */
	public void playToast(String string){
		Toast.makeText(UiUtils.getInstance().getContext(), string, Toast.LENGTH_SHORT).show();
	}
	
	
	/**
	 * 
	 * 描述：获取系统的版本号
	 * @param context
	 */
	public int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/****设置密码是否是明文密码**********/
	public void setTransformationMothed(CheckBox checkBox,EditText editText){
		if (checkBox.isChecked()){
			editText.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
		}else{
			editText.setTransformationMethod(PasswordTransformationMethod.getInstance());
		}
	}


	// 获取ApiKey
	public static String getMetaValue(Context context, String metaKey) {
		Bundle metaData = null;
		String apiKey = null;
		if (context == null || metaKey == null) {
			return null;
		}
		try {
			ApplicationInfo ai = context.getPackageManager()
					.getApplicationInfo(context.getPackageName(),
							PackageManager.GET_META_DATA);
			if (null != ai) {
				metaData = ai.metaData;
			}
			if (null != metaData) {
				apiKey = metaData.getString(metaKey);
			}
		} catch (NameNotFoundException e) {
		}
		return apiKey;
	}

	public String showEmoji(String data){
		String str = data.substring(1, data.length() - 1);
		String strArray[] = str.split(",");
		byte[] chars = new byte[strArray.length];
		for (int i = 0; i < strArray.length; i++) {
			System.out.println("strArray[i]:" + strArray[i]);
			chars[i] = Byte.decode(strArray[i]);
		}
		String newString = new String(chars);
		return newString;
	}

	/************以下是表情的相关方法时间：2016-10-10************/
	//提交之前的数据，使表情编程数字数组 [-16, -97, -104, -95]
	String string;
	public String filterEmoji(String source) {
		System.out.println("ctatting running source = "+source);
		if (!containsEmoji(source)) {
			return source;// 如果不包含，直接返回
		}
		StringBuilder buf = null;
		int len = source.length();
		for (int i = 0; i < len; i++) {
			char codePoint = source.charAt(i);
			if (buf == null) {
				buf = new StringBuilder(source.length());
			}
			if (!isEmojiCharacter(codePoint)) {
				string = String.valueOf(codePoint);
			} else {
				try {
					StringBuilder builder = new StringBuilder(2);
					byte[] str = builder.append(String.valueOf(codePoint))
							.append(String.valueOf(source.charAt(i+1)))
							.toString().getBytes("UTF-8");
					String strin = Arrays.toString(str);
					String newString = strin.substring(1, strin.length() - 1);
					string = "Γ"+newString+"Γ";
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				i++;
			}
			buf.append(string+"⅞");
		}
		if (buf == null) {
			return "";
		} else {
			if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
				buf = null;
				return source;
			} else {
				String bufStr = buf.toString();
				String newBufStr= bufStr.substring(0, bufStr.length() - 1);
				return newBufStr;
			}
		}
	}

	// 判别是否包含Emoji表情
	private boolean containsEmoji(String str) {
		int len = str.length();
		for (int i = 0; i < len; i++) {
			if (isEmojiCharacter(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	private boolean isEmojiCharacter(char codePoint) {
		return !((codePoint == 0x0) ||
				(codePoint == 0x9) ||
				(codePoint == 0xA) ||
				(codePoint == 0xD) ||
				((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
				((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
	}

	//得到服务器的数据之后进行解析，显示在UI上
	String newsString;
	public void setString(TextView textView,String string) {
		StringBuilder stringBuilder = new StringBuilder();
		String arrays[] = string.split("⅞");
		for (int j = 0; j < arrays.length; j++) {
			String  ss = arrays[j];
			char char_ss = ss.charAt(0);
			if (String.valueOf(char_ss).equals("Γ")){
				String new_SS = ss.substring(1, ss.length() - 1);
				String strArrays[] = new_SS.split(", ");
				byte[] chars = new byte[strArrays.length];
				for (int i = 0; i < strArrays.length; ++i) {
					System.out.println("strArrays[i]:" + strArrays[i]);
					chars[i] = Byte.decode(strArrays[i]);
				}
				newsString = new String(chars);
			}else{
				newsString =ss;
			}
			stringBuilder.append(newsString);
			textView.setText(stringBuilder.toString());
		}
	}
	/************以上是表情的相关方法时间：2016-10-10************/







	/************以下是表情的相关方法时间：2016-10-10************/
	//提交之前的数据，使表情编程数字数组 [-16, -97, -104, -95]
//	String string;
//	public String filterEmojiNew(String source) {
//		if (!containsEmoji(source)) {
//			return source;// 如果不包含，直接返回
//		}
//		StringBuilder buf = null;
//		int len = source.length();
//		for (int i = 0; i < len; i++) {
//			char codePoint = source.charAt(i);
//			if (buf == null) {
//				buf = new StringBuilder(source.length());
//			}
//			if (!isEmojiCharacter(codePoint)) {
//				string = String.valueOf(codePoint);
//			} else {
//				try {
//					StringBuilder builder = new StringBuilder(2);
//					byte[] str = builder.append(String.valueOf(codePoint))
//							.append(String.valueOf(source.charAt(i+1)))
//							.toString().getBytes("UTF-8");
//					String strin = Arrays.toString(str);
//					String newString = strin.substring(1, strin.length() - 1);
//					string = "Γ"+newString+"Γ";
//				} catch (UnsupportedEncodingException e) {
//					e.printStackTrace();
//				}
//				i++;
//			}
//			buf.append(string+"⅞");
//		}
//		if (buf == null) {
//			return "";
//		} else {
//			if (buf.length() == len) {// 这里的意义在于尽可能少的toString，因为会重新生成字符串
//				buf = null;
//				return source;
//			} else {
//				String bufStr = buf.toString();
//				String newBufStr= bufStr.substring(0, bufStr.length() - 1);
//				return newBufStr;
//			}
//		}
//	}
//
//	// 判别是否包含Emoji表情
//	private boolean containsEmojiNew(String str) {
//		int len = str.length();
//		for (int i = 0; i < len; i++) {
//			if (isEmojiCharacter(str.charAt(i))) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private boolean isEmojiCharacterNew(char codePoint) {
//		return !((codePoint == 0x0) ||
//				(codePoint == 0x9) ||
//				(codePoint == 0xA) ||
//				(codePoint == 0xD) ||
//				((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||
//				((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) ||
//				((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)));
//	}
//
//	//得到服务器的数据之后进行解析，显示在UI上
//	String newsString;
//	public void setStringNew(TextView textView,String string) {
//		StringBuilder stringBuilder = new StringBuilder();
//		String arrays[] = string.split("⅞");
//		for (int j = 0; j < arrays.length; j++) {
//			String  ss = arrays[j];
//			char char_ss = ss.charAt(0);
//			if (String.valueOf(char_ss).equals("Γ")){
//				String new_SS = ss.substring(1, ss.length() - 1);
//				String strArrays[] = new_SS.split(", ");
//				byte[] chars = new byte[strArrays.length];
//				for (int i = 0; i < strArrays.length; ++i) {
//					System.out.println("strArrays[i]:" + strArrays[i]);
//					chars[i] = Byte.decode(strArrays[i]);
//				}
//				newsString = new String(chars);
//			}else{
//				newsString =ss;
//			}
//			stringBuilder.append(newsString);
//			textView.setText(stringBuilder.toString());
//		}
//	}
	/************以上是表情的相关方法时间：2016-10-10************/


}
