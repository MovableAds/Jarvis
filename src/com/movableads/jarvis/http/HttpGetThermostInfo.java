package com.movableads.jarvis.http;

import org.json.JSONArray;
import org.json.JSONObject;

import com.movableads.jarvis.Define;
import com.movableads.jarvis.Global;
import android.os.AsyncTask;
import android.util.Log;


public class HttpGetThermostInfo extends AsyncTask</* param */String, /* progress */Object, /* result */String> {

	public HttpGetThermostInfo() {
		// TODO Auto-generated constructor stub
		String url = ApiUrl.getThermostatInfo();
		Log.d("api", String.format("HttpGetThermostatInfo url = %s", url));
		execute(url);
	}
	
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		HttpClient c = new HttpClient();
		
		try {
			//JSONObject obj = c.postData2(params[0], null);
			JSONObject obj = c.getHttpJson2(params[0]);
			Log.d("api", obj.toString());
			
			String status = obj.getString("status");
			String curThermostatTemperature = "";
			
			if(status.equals("0")){
				JSONObject objContents = obj.getJSONObject("content");
				JSONArray ary = objContents.getJSONArray("attributes");
				for(int i = 0; i < ary.length(); i++){
					if(Global._thermostatCoolOrHeatStatus == Define.THERMOSTAT_COOL){
						JSONObject obj_ = ary.getJSONObject(i);
						if(obj_.getString("label").equals("cool-setpoint")){
							curThermostatTemperature = obj_.getString("value");
							break;
						}	
					}
					else{
						JSONObject obj_ = ary.getJSONObject(i);
						if(obj_.getString("label").equals("heat-setpoint")){
							curThermostatTemperature = obj_.getString("value");
							break;
						}	
					}
				}
				
				Log.d("api", String.format("curThermostatTemperature = %s", curThermostatTemperature));
				Global.currentThermostatTemperature = Double.parseDouble(curThermostatTemperature) / 2.0 - 40;
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
