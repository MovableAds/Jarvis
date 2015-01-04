package com.movableads.jarvis.http;

import java.util.Vector;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.movableads.jarvis.Define;
import com.movableads.jarvis.Global;


import android.os.AsyncTask;
import android.util.Log;

public class HttpInit extends AsyncTask</* param */String, /* progress */Object, /* result */String> {

	public HttpInit() {
		// TODO Auto-generated constructor stub
		String url = ApiUrl.init();
		execute(url);
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		
		HttpClient c = new HttpClient();
		
		try {
			JSONObject obj = c.getHttpJson(params[0]);
			
			Global.USER_ID = obj.getString("USER_ID");
			Global.PASSWD = obj.getString("PASSWD");
			Global.DOMAIN = obj.getString("DOMAIN");
			Global.APPKEY = obj.getString("APPKEY");
			
			Global.DEVICE_DOOR_LOCK = obj.getString("DEVICE_DOOR_LOCK");
			Global.DEVICE_THERMOSTAT =  obj.getString("DEVICE_THERMOSTAT");
			Global.DEVICE_SMART_PLUG = obj.getString("DEVICE_SMART_PLUG");
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return HttpReturnCode.EXCEPTION;
		}
		
		return HttpReturnCode.SUCCESS;
	}
	
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
	}
	
	
	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if(onReqRecv != null){
			onReqRecv.onComplete(result);
		}
	}
	
	
	@Override
	protected void onProgressUpdate(Object... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}
	
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	
	
	protected OnRequest onReqRecv;
	public interface OnRequest{ 
		void onComplete(String result); 										
	}
	public void setOnRequestComplete(OnRequest callback){ 
		onReqRecv = callback;
	}
}
