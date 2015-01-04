package com.movableads.jarvis.http;

import com.movableads.jarvis.Global;


public class ApiUrl {

	static private String HOST = "https://systest.digitallife.att.com:443/penguin/api/";
	
	
	static public String init(){
		return "http://team-pluto.kr/api/mountain/test.php";
	}
	
	static public String getAuthTokens(){
		return String.format("%sauthtokens", HOST);
	}
	
	static public String openDoor(String value){
		return String.format("%s%s/devices/%s/lock/%s", HOST, Global.tokenID, Global.DEVICE_DOOR_LOCK, value);
	}
	
	static public String onOffSmartPlug(String value){
		return String.format("%s%s/devices/%s/switch/%s", HOST, Global.tokenID, Global.DEVICE_SMART_PLUG, value);
	}
	
	static public String onOffThermostat(String value){
		return String.format("%s%s/devices/%s/thermostat-mode/%s", HOST, Global.tokenID, Global.DEVICE_THERMOSTAT, value);
	}
	
	static public String getThermostatInfo(){
		return String.format("%s%s/devices/%s", HOST, Global.tokenID, Global.DEVICE_THERMOSTAT);
	}
	
	static public String getDoorInfo(){
		return String.format("%s%s/devices/%s/lock", HOST, Global.tokenID, Global.DEVICE_DOOR_LOCK);
	}
	
	static public String getSmartPlugInfo(){
		return String.format("%s%s/devices/%s/switch", HOST, Global.tokenID, Global.DEVICE_SMART_PLUG);
	}
	
	static public String forecast(Double lat, Double lon){
		return String.format("https://api.forecast.io/forecast/6d1ed5d9103f993a2af79d15ba307932/%f,%f", lat, lon);
	}
	
	static public String setThermostatTemperature(String status, Double temperature){
		if(temperature > 0)
			temperature = temperature * 2 + 40;
		else
			temperature = temperature * -2 + 40;
		
		return String.format("%s%s/devices/%s/%s/%.0f", HOST, Global.tokenID, Global.DEVICE_THERMOSTAT, status, temperature);
	}
	
}
