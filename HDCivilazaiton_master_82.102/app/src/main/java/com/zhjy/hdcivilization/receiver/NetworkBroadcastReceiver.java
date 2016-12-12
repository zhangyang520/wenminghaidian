package com.zhjy.hdcivilization.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.zhjy.hdcivilization.utils.UiUtils;

/**
 * 进行定义监听网络状态变化的广播接收者
	 */
public class NetworkBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			
			String action = intent.getAction(); 
	        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
//	            Toast.makeText(UiUtils.getContext(), "NetworkBroadcastReceiver CONNECTIVITY_ACTION", 0).show();  
	        	ConnectivityManager mConnectivityManager = (ConnectivityManager) UiUtils.getInstance().getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
	            NetworkInfo netInfo = mConnectivityManager.getActiveNetworkInfo();
	            if(netInfo != null && netInfo.isAvailable()){ 
	                 /////////////网络连接 
	                String name = netInfo.getTypeName(); 
	                   
	                if(netInfo.getType()==ConnectivityManager.TYPE_WIFI){ 

	                }else if(netInfo.getType()==ConnectivityManager.TYPE_ETHERNET){ 
	                /////有线网络 
	   
	                }else if(netInfo.getType()==ConnectivityManager.TYPE_MOBILE){ 
	                /////////3g网络 
	   
	                } 
	              }else{ 

          			  
	               }
	            } 
	        }
}

