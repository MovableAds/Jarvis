package com.movableads.jarvis.http;

import org.json.JSONArray;
import org.json.JSONObject;

import com.movableads.jarvis.Define;
import com.movableads.jarvis.Global;
import android.os.AsyncTask;
import android.util.Log;


public class HttpGetDoorInfo extends AsyncTask</* param */String, /* progress */Object, /* result */String> {

	public HttpGetDoorInfo() {
		// TODO Auto-generated constructor stub
		String url = ApiUrl.getDoorInfo();
		Log.d("api", String.format("HttpGetDoorInfo url = %s", url));
		execute(url);
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		HttpClient c = new HttpClient();
		
		try {
			JSONObject obj = c.getHttpJson2(params[0]);
			Log.d("api", obj.toString());
			
			String status = obj.getString("status");
			if(status.equals("0")){
				JSONObject objContents = obj.getJSONObject("content");
				Log.d("api", objContents.toString());
				String value = objContents.getString("value");
				publishProgress(value);
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("api", e.toString());
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
		if(onReqRecv != null)
			onReqRecv.onStatus(values[0].toString());
	}
	
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		super.onCancelled();
	}
	
	
	protected OnRequest onReqRecv;
	public interface OnRequest{ 
		void onStatus(String status);
		void onComplete(String result); 										
	}
	public void setOnRequestComplete(OnRequest callback){ 
		onReqRecv = callback;
	}
}
