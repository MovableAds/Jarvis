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

public class HttpGetAuthTokens extends AsyncTask</* param */String, /* progress */Object, /* result */String> {

	public HttpGetAuthTokens() {
		// TODO Auto-generated constructor stub
		String url = ApiUrl.getAuthTokens();
		Log.d("api", String.format("HttpGetAuthTokens url = %s", url));
		execute(url);
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		Vector<NameValuePair> postInfo = new Vector<NameValuePair>();
		postInfo.add(new BasicNameValuePair("userId", Global.USER_ID));
		postInfo.add(new BasicNameValuePair("password", Global.PASSWD)); 
		postInfo.add(new BasicNameValuePair("domain", Global.DOMAIN)); 
		postInfo.add(new BasicNameValuePair("appKey", Global.APPKEY)); 
		
		HttpClient c = new HttpClient();
		
		try {
			JSONObject obj = c.postData(params[0], postInfo);
			//Log.d("api", String.format("token = %s", obj.toString()));
			
			String status = obj.getString("status");
			if(status.equals("0")){
				JSONObject objContents = obj.getJSONObject("content");
				Log.d("api", String.format("content = %s", objContents.toString()));
				
				Global.reqToken = objContents.getString("requestToken");
				Global.authToken = objContents.getString("authToken");
				Log.d("api", String.format("reqToken = %s", Global.reqToken));
				Log.d("api", String.format("authToken = %s", Global.authToken));
				
				JSONArray aryGateways = objContents.getJSONArray("gateways");
				//Log.d("api", String.format("gateways = %s", aryGateways.toString()));
				JSONObject idObj = aryGateways.getJSONObject(0);
				String id = idObj.getString("id");
				
				// save
				Global.tokenID = id;
			}
			
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
