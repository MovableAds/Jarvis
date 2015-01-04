package com.movableads.jarvis.http;

import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;



public class OrderSetTemperature extends AsyncTask</* param */String, /* progress */Object, /* result */String> {

	public OrderSetTemperature(String status, Double temperature) {
		// TODO Auto-generated constructor stub
		String url = ApiUrl.setThermostatTemperature(status, temperature);
		Log.d("api", String.format("OrderSetTemperature url = %s", url));
		execute(url);
	}
	
	@Override
	protected String doInBackground(String... params) {
		// TODO Auto-generated method stub
		
		String result = HttpReturnCode.SUCCESS;
		HttpClient c = new HttpClient();
		
		try {
			JSONObject obj = c.postData2(params[0], null);
			
			String status = obj.getString("status");
			if(!status.equals("0")){
				result = HttpReturnCode.FAIL;
			}
			
			Log.d("api", String.format("set temperature = %s", obj.toString()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.d("api", "exception");
			return HttpReturnCode.EXCEPTION;
		}
		
		return result;
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
