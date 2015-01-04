package com.movableads.jarvis.http;

import org.json.JSONObject;

import com.movableads.jarvis.http.OrderChangeDoorStatus.OnRequest;


import android.os.AsyncTask;

public class OrderSetTemperature extends AsyncTask</* param */String, /* progress */Object, /* result */String> {

	public OrderSetTemperature(String value) {
		// TODO Auto-generated constructor stub
		String url = "";
		execute(url);
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		HttpClient c = new HttpClient();
		JSONObject obj = null;
		try {
			obj = c.getHttpJson(params[0]);
			String state = obj.getString("state");
			String url   = obj.getString("url");
			int    remain = obj.getInt("remain_time");
			String msg   = obj.getString("msg");
			
			publishProgress(state, url, remain, msg);
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
