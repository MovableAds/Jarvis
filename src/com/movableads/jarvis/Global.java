package com.movableads.jarvis;

public class Global {

	static public int _smartPlugOnOffStatus = 0;	//	off : 0, on : 1
	
	static public boolean _useThermostatControl = false;
	static public int _thermostatCoolOrHeatStatus = 0;	//	cool : 0, heat : 1
	static public int _thermostatOnOffStatus = 0;	//	off : 0, on : 1
	
	
	
	static public String tokenID = "";
	static public String reqToken = "";
	static public String authToken = "";
	
	// temp
	/*
	static public String USER_ID = "553474464";
	static public String PASSWD = "NO-PASSWD";
	static public String DOMAIN = "DL";
	static public String APPKEY = "KE_FEFD8F0FF0EEABB4_1";
	
	static public String DEVICE_DOOR_LOCK = "DL00000005";
	static public String DEVICE_THERMOSTAT =  "DT00000004";
	static public String DEVICE_SMART_PLUG = "PE00000002";
	*/
	
	static public String USER_ID = "";
	static public String PASSWD = "";
	static public String DOMAIN = "";
	static public String APPKEY = "";
	
	static public String DEVICE_DOOR_LOCK = "";
	static public String DEVICE_THERMOSTAT =  "";
	static public String DEVICE_SMART_PLUG = "";
	
	static public Double latitude = 0.0;
	static public Double longitude = 0.0;
	
	static public Double currentThermostatTemperature = 0.0;
	
	static public boolean isDoorLocked = false;
}
