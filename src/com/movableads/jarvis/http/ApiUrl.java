package com.movableads.jarvis.http;

import com.movableads.jarvis.Global;


public class ApiUrl {

	static private String HOST = "https://systest.digitallife.att.com:443/penguin/api/";
	
	
	static public String getAuthTokens(){
		return String.format("%sauthtokens", HOST);
	}
	
	static public String openDoor(String value){
		return String.format("%s%s/devices/%s/lock/%s", HOST, Global.tokenID, Global.DEVICE_DOOR_LOCK, value);
	}
	
	static public String onOffSmartPlug(String value){
		return String.format("%s%s/devices/%s/switch/%s", HOST, Global.tokenID, Global.DEVICE_SMART_PLUG, value);
	}
	
	static public String offThermostat(){
		return String.format("%s%s/devices/%s/thermostat-mode/off", HOST, Global.tokenID, Global.DEVICE_THERMOSTAT);
	}
	
	static public String getThermostatInfo(){
		return String.format("%s%s/devices/%s", HOST, Global.tokenID, Global.DEVICE_THERMOSTAT);
	}
	
	static public String forecast(Double lat, Double lon){
		return String.format("https://api.forecast.io/forecast/6d1ed5d9103f993a2af79d15ba307932/%f,%f", lat, lon);
	}
	
	
	
	
	static public String upTemperature(String value){
		return String.format("%s/upTemperature", HOST);
		//return HOST;
	}
	
	static public String downTemperature(String value){
		return String.format("%s/downTemperature", HOST);
		//return HOST;
	}
	
	
	
	
	
	/*
	static public String getDoorStatus(){
		return String.format("%s/downTemperature", HOST);
		//return HOST;
	}
	
	static public String getTemperatureStatus(){
		return String.format("%s/downTemperature", HOST);
		//return HOST;
	}*/
	
}
