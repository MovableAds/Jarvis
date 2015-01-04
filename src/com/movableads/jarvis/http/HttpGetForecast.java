package com.movableads.jarvis.http;

import org.json.JSONObject;
import com.movableads.jarvis.Define;
import com.movableads.jarvis.Global;


import android.os.AsyncTask;
import android.util.Log;

public class HttpGetForecast extends AsyncTask</* param */String, /* progress */Object, /* result */String> {

	public HttpGetForecast() {
		// TODO Auto-generated constructor stub
		String url = ApiUrl.forecast(Global.latitude, Global.longitude);
		Log.d("api", String.format("HttpGetForecast url = %s", url));
		execute(url);
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		HttpClient c = new HttpClient();
		
		try {
			JSONObject obj = c.getHttpJson(params[0]);
			JSONObject curObj = obj.getJSONObject("currently");
			Double temperature = curObj.getDouble("temperature");
			if(temperature < 50.00){
				Global._thermostatCoolOrHeatStatus = Define.THERMOSTAT_HEAT;
			}
			else{
				Global._thermostatCoolOrHeatStatus = Define.THERMOSTAT_COOL;
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
