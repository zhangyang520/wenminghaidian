package com.zhjy.hdcivilization.utils;


import java.util.Iterator;
import java.util.Vector;

import android.app.Activity;
/**
 * 当打开一个Activity时，添加进Vector，系统退出时关闭所有的Activity
 * */
public class SysControl {
	
	//Vector 初始大小为20
	public static Vector<Activity> sysVector=new Vector<Activity>(20);
	
	/*
	 * 添加一个打开的Activity进入Vector
	 * */
	public static Vector<Activity> add(Activity activity){
		
		sysVector.add(activity);
		return sysVector;
	}
	
	/**
	 * 退出当前个
	 */
	public static void removeCurrent(Activity activity){
		//进行删除对应的集合
		sysVector.remove(activity);
	}
	/*
	 * 系统退出
	 * 先关闭所有打开的Activity
	 * */
	public static void exit_Sys(){
		
		Iterator<Activity> iterator=sysVector.iterator();
		while(iterator.hasNext()){
			Activity activity=iterator.next();
			if(!activity.isFinishing()){
				//关闭每个打开的Activity
				activity.finish();
//				//退出系统 0表示正常退出
//				System.exit(0);
			}else{

			}
		}
		sysVector.removeAllElements();
		//退出系统
//		System.exit(0);
	}
	/*
	 * 删除Vector中的最后一个Activity
	 *
	 */
	public static void deleteLastActivity(){
		sysVector.removeElementAt(sysVector.size()-1);
	}
	
	/**
	 * 返回倒数第二个页面
	 */
	public static void goBack2Back(int i){
		for(int k=0;k<i;k++){
			Activity a = sysVector.get(sysVector.size()-1);
			if(!a.isFinishing()){
				a.finish();
			}
			deleteLastActivity();
		}
	}

	public static void goToFirstActivity(){
		for(int k=sysVector.size()-1;k>=1;k--){
			Activity a=sysVector.remove(k);
//			Activity a = sysVector.get(sysVector.size()-1);
			if(!a.isFinishing()){
				a.finish();
			}
//			deleteLastActivity();
		}
	}
}
