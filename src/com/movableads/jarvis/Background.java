/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package com.movableads.jarvis;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.movableads.jarvis.http.HttpReturnCode;
import com.movableads.jarvis.http.OrderChangeDoorStatus;
import com.movableads.jarvis.http.OrderOnOffSmartPlug;
import com.movableads.jarvis.http.OrderOnOffThermostat;
import com.movableads.jarvis.http.OrderSetTemperature;
import com.movableads.jarvis.http.OrderType;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

public class Background extends Service {

	final private int SKIP_EVENT = 0;
	final private int CONTROL_THERMOSTAT = 1;
	
	private boolean mSkipEvent = false; 
	private App mApp = null;
	
	
    // Classes that inherit from AbstractDeviceListener can be used to receive events from Myo devices.
    // If you do not override an event, the default behavior is to do nothing.
    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long timestamp) {
        	//changed(getString(R.string.connected));
        	Toast.makeText(getApplicationContext(), "onConnect", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect(Myo myo, long timestamp) {
        	//changed(getString(R.string.disconnected));
        	Toast.makeText(getApplicationContext(), "onDisconnect", Toast.LENGTH_SHORT).show();
        }

        // onPose() is called whenever the Myo detects that the person wearing it has changed their pose, for example,
        // making a fist, or not making a fist anymore.
        @Override
        public void onPose(Myo myo, long timestamp, Pose pose) {
            // Show the name of the pose in a toast.
        	//changed(getString(R.string.pose, pose.toString()));
        	
        	//if(mSkipEvent)
        	{
        		//if(pose.equals(Pose.FINGERS_SPREAD) ||
        		if(pose.equals(Pose.WAVE_IN) || 
        			pose.equals(Pose.WAVE_OUT)){
        				//mSkipEvent = true;
        				//mHandler.removeMessages(SKIP_EVENT);
                		//mHandler.sendEmptyMessageDelayed(SKIP_EVENT, 2000);
                		//return;
        		}
        	}
        	
        	changed(pose);
        }
    };

    
    Handler mHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		if(msg.what == SKIP_EVENT){
    			mSkipEvent = false;
    			mHandler.removeMessages(SKIP_EVENT);
    		}
    		else if(msg.what == CONTROL_THERMOSTAT){
    			Global._useThermostatControl = false;
    		}
    	};
    };
    
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    
    
    @Override
    public void onCreate() {
        super.onCreate();

        mApp = (App)getApplication();
        
        // First, we initialize the Hub singleton with an application identifier.
        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
        	//changed("Couldn't initialize Hub");
            stopSelf();
            return;
        }

        // Disable standard Myo locking policy. All poses will be delivered.
        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        // Next, register for DeviceListener callbacks.
        hub.addListener(mListener);

        // Finally, scan for Myo devices and connect to the first one found that is very near.
        hub.attachToAdjacentMyo();
    }

    
    @Override
    public void onDestroy() {
        super.onDestroy();
        // We don't want any callbacks when the Service is gone, so unregister the listener.
        Hub.getInstance().removeListener(mListener);
        Hub.getInstance().shutdown();
    }

    
    private void changed(Pose myoEvent) {
    	Log.d("jarvis", myoEvent.toString());
    	
    	if(myoEvent.equals(Pose.DOUBLE_TAP)){
    		if(!Global._useThermostatControl){
    			//Toast.makeText(getApplicationContext(), "온도 조절 가능", Toast.LENGTH_SHORT).show();
    		}
    		//Global._useThermostatControl = true;
    		Global._useThermostatControl = false;
    		mApp.refresh();
		}
		else if(myoEvent.equals(Pose.FINGERS_SPREAD)){
			if(Global._useThermostatControl){
				// 냉난방 장치 껐다켰다 해라.
				if(Global._thermostatOnOffStatus == Define.THERMOSTAT_OFF){
					//Toast.makeText(getApplicationContext(), "thermostat on", Toast.LENGTH_SHORT).show();
					if(Global._thermostatCoolOrHeatStatus == Define.THERMOSTAT_COOL){
						onOffThermostat("cool");	
					}
					else{
						onOffThermostat("heat");
					}
				}
				else{
					//Toast.makeText(getApplicationContext(), "thermostat off", Toast.LENGTH_SHORT).show();
					onOffThermostat("off");
				}
			}
			else{
				// smart-plug 껐다켰다 해라.
				if(Global._smartPlugOnOffStatus == Define.SMART_PLUG_OFF){
					//Toast.makeText(getApplicationContext(), "smartPlug on", Toast.LENGTH_SHORT).show();
					onOffSmartPlug("on");
				}
				else{
					//Toast.makeText(getApplicationContext(), "smartPlug off", Toast.LENGTH_SHORT).show();
					onOffSmartPlug("off");
				}
			}			
		}
    	/*
		else if(myoEvent.equals(Pose.WAVE_IN)){
			if(Global._useThermostatControl){
				//	온도 내려라
				setTemperature(false);
				//Toast.makeText(getApplicationContext(), "온도 내려라", Toast.LENGTH_SHORT).show();
				mHandler.removeMessages(CONTROL_THERMOSTAT);
				mHandler.sendEmptyMessageDelayed(CONTROL_THERMOSTAT, 5000);
			}
			else{ 
				//	문 닫아라.
				//Toast.makeText(getApplicationContext(), "문 닫아라", Toast.LENGTH_SHORT).show();
				changeDoorStatus("lock");
			}
		}
		else if(myoEvent.equals(Pose.WAVE_OUT)){
			if(Global._useThermostatControl){
				// 온도 올려라.
				setTemperature(true);
				//Toast.makeText(getApplicationContext(), "온도 올려라", Toast.LENGTH_SHORT).show();
				mHandler.removeMessages(CONTROL_THERMOSTAT);
				mHandler.sendEmptyMessageDelayed(CONTROL_THERMOSTAT, 5000);
			}
			else{
				// 문 열어라.
				//Toast.makeText(getApplicationContext(), "문 열어라", Toast.LENGTH_SHORT).show();
				changeDoorStatus("unlock");
			}
		}*/
		else if(myoEvent.equals(Pose.FIST)){
			if(Global.isDoorLocked){
				Toast.makeText(getApplicationContext(), "open door", Toast.LENGTH_SHORT).show();
				changeDoorStatus("unlock");
			}
			else{
				Toast.makeText(getApplicationContext(), "close door", Toast.LENGTH_SHORT).show();
				changeDoorStatus("lock");
			}
		}
		else if(myoEvent.equals(Pose.REST)){
		}
		else{
			// Pose.UNKNOWN
		}
    }
    
    
    private void changeDoorStatus(String value){
    	OrderChangeDoorStatus call = new OrderChangeDoorStatus(value);
    	call.setOnRequestComplete(new OrderChangeDoorStatus.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				Log.d("api", "changeDoorStatus - " + result);
				
				/*
				Toast.makeText(getApplicationContext(), "DOOR", Toast.LENGTH_SHORT).show();
				Intent intent = new Intent (Define.MYO_RECEIVER, null);
		        intent.putExtra("status", "DOOR");
		        sendBroadcast(intent);*/
				mApp.changeDoor();
			}
		});
    }
    
    
    private void onOffSmartPlug(String value){
    	OrderOnOffSmartPlug call = new OrderOnOffSmartPlug(value);
    	call.setOnRequestComplete(new OrderOnOffSmartPlug.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				Log.d("api", "onOffSmartPlug - " + result);
				if(result.equals(HttpReturnCode.SUCCESS) || result.equals(HttpReturnCode.FAIL)){
					if(Global._smartPlugOnOffStatus == Define.SMART_PLUG_OFF){
						Global._smartPlugOnOffStatus = Define.SMART_PLUG_ON;
					}
					else{
						Global._smartPlugOnOffStatus = Define.SMART_PLUG_OFF;
					}
					
					/*
					Toast.makeText(getApplicationContext(), "PLUG", Toast.LENGTH_SHORT).show();
					Intent intent = new Intent (Define.MYO_RECEIVER, null);
			        intent.putExtra("status", "PLUG");
			        sendBroadcast(intent);*/
					mApp.changePlug();
				}
			}
		});
    }
    
    private void onOffThermostat(String value){
    	OrderOnOffThermostat call = new OrderOnOffThermostat(value);
    	call.setOnRequestComplete(new OrderOnOffThermostat.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				Log.d("api", "OrderOnOffThermostat - " + result);
				if(result.equals(HttpReturnCode.SUCCESS) || result.equals(HttpReturnCode.FAIL)){
					if(Global._thermostatOnOffStatus == Define.THERMOSTAT_OFF){
						Global._thermostatOnOffStatus = Define.THERMOSTAT_ON;
					}
					else{
						Global._thermostatOnOffStatus = Define.THERMOSTAT_OFF;
					}
				}
			}
		});
    }
    
    
    private void setTemperature(boolean isUp){
    	Double dTemperature = 0.0;
    	if(isUp){
    		dTemperature = ++Global.currentThermostatTemperature;
    	}
    	else{
    		dTemperature = --Global.currentThermostatTemperature;
    	}
    	String status = Global._thermostatCoolOrHeatStatus == Define.THERMOSTAT_COOL ? "cool-setpoint" : "heat-setpoint";
    	OrderSetTemperature temperature = new OrderSetTemperature(status, dTemperature);
    	temperature.setOnRequestComplete(new OrderSetTemperature.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub	
			}
		});
    }
    
}
