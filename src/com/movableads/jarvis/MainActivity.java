package com.movableads.jarvis;

import com.movableads.jarvis.R;
import com.movableads.jarvis.http.HttpGetAuthTokens;
import com.movableads.jarvis.http.HttpGetDoorInfo;
import com.movableads.jarvis.http.HttpGetForecast;
import com.movableads.jarvis.http.HttpGetSmartPlugInfo;
import com.movableads.jarvis.http.HttpGetThermostInfo;
import com.movableads.jarvis.http.HttpInit;
import com.movableads.jarvis.http.HttpReturnCode;
import com.movableads.jarvis.http.OrderChangeDoorStatus;
import com.movableads.jarvis.http.OrderOnOffThermostat;
import com.movableads.jarvis.http.OrderSetTemperature;
import com.movableads.jarvis.http.OrderType;
import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;
import com.thalmic.myo.scanner.ScanActivity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private boolean isRefresh = true;
	
	private LocationManager mLocationManager = null;
	private MyoReceiver myoReceiver = new MyoReceiver();
	
	private TextView mTxvMessage = null;
	private Button mBtnLocation = null;
	private Button mBtnOutside = null;
	private Button mBtnInside = null;
	private ImageView mImgDoor = null;
	private ImageView mImgSmartPlug = null;
	private TextView mTxvDoorMsg = null;
	private TextView mTxvPlugMsg = null;
	
	private App mApp = null;
	
	private MediaPlayer mSndOpenDoor = null;
    private MediaPlayer mSndClosedDoor = null;
    private MediaPlayer mSndLightOn = null;
    private MediaPlayer mSndLightOff = null;
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mApp = (App)getApplication();
        mApp.mMain = this;
        
        registerMyoReceiver();
        startService(new Intent(MainActivity.this, Background.class));
        
        mTxvMessage = (TextView)findViewById(R.id.progress_msg);
        mTxvMessage.setText("Loading ...");
        
        mBtnLocation = (Button)findViewById(R.id.location);
        mBtnOutside = (Button)findViewById(R.id.outside);
        mBtnInside = (Button)findViewById(R.id.inside);
        mImgDoor = (ImageView)findViewById(R.id.door);
        mImgSmartPlug = (ImageView)findViewById(R.id.plug);
        mTxvDoorMsg = (TextView)findViewById(R.id.door_msg);
        mTxvPlugMsg = (TextView)findViewById(R.id.plug_msg);
        initSound();
        init();
        
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 1, mLocationListener);    
    }
    
    
    private void initSound(){

        if(mSndOpenDoor == null){
            int resID = getResources().getIdentifier("door_is_open", "raw", getPackageName());
            mSndOpenDoor = MediaPlayer.create(getApplicationContext(), resID);
        }

        if(mSndClosedDoor == null){
            int resID = getResources().getIdentifier("door_is_locked", "raw", getPackageName());
            mSndClosedDoor = MediaPlayer.create(getApplicationContext(), resID);
        }

        if(mSndLightOn == null){
            int resID = getResources().getIdentifier("smartplug_on", "raw", getPackageName());
            mSndLightOn = MediaPlayer.create(getApplicationContext(), resID);
        }

        if(mSndLightOff == null){
            int resID = getResources().getIdentifier("smartplug_off", "raw", getPackageName());
            mSndLightOff = MediaPlayer.create(getApplicationContext(), resID);
        }
    }
    
    
    public void init(){
    	HttpInit init = new HttpInit();
    	init.setOnRequestComplete(new HttpInit.OnRequest() {
			
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				getToken();
			}
		});
    }
    
    
    LocationListener mLocationListener = new LocationListener() {
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

			if (location != null) {
				mTxvMessage.setText("Search location ...");
				
				Global.latitude = location.getLatitude();
				Global.longitude = location.getLongitude();
				mLocationManager.removeUpdates(mLocationListener);
				getForecast();
			}
		}
	};
    
	
	private void getForecast(){
		mTxvMessage.setText("Get forecast ...");
		HttpGetForecast forecast = new HttpGetForecast();
		forecast.setOnRequestComplete(new HttpGetForecast.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				if(result.equals(HttpReturnCode.SUCCESS)){
					//mLocationManager.removeUpdates(mLocationListener);
					getCurrentThermostInfo();
				}
			}

			@Override
			public void onLocation(String location, String temperature) {
				// TODO Auto-generated method stub
				mBtnLocation.setText(location);
				mBtnOutside.setText("Outside\n" + temperature + "℉");
			}
		});
	}
    
	
	private void getCurrentThermostInfo(){
		mTxvMessage.setText("Get temperature ...");
		HttpGetThermostInfo info = new HttpGetThermostInfo();
		info.setOnRequestComplete(new HttpGetThermostInfo.OnRequest() {
			@Override
			public void onTemperature(String temperature) {
				// TODO Auto-generated method stub
				//mBtnInside.setText("Inside\n" + temperature + " ℃");
				mBtnInside.setText("Inside\n" + temperature + "℉");
			}
			
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				mTxvMessage.setText("2015 AT&T Developer Summit");
				getDoorStatus();
			}
		});
	}
	
	
	public void getDoorStatus(){
		mTxvMessage.setText("Get door status ...");
		HttpGetDoorInfo info = new HttpGetDoorInfo();
		info.setOnRequestComplete(new HttpGetDoorInfo.OnRequest() {
			@Override
			public void onStatus(String status) {
				// TODO Auto-generated method stub
				//mBtnDoor.setText("Door - " + status);
				if(status.equals("lock")){
					Global.isDoorLocked = true;
					mImgDoor.setImageResource(R.drawable.door_closed);
					mTxvDoorMsg.setText("Door closed");
					if(!mSndClosedDoor.isPlaying() && !isRefresh)
						mSndClosedDoor.start();
				}
				else{
					Global.isDoorLocked = false;
					mImgDoor.setImageResource(R.drawable.door_open);
					mTxvDoorMsg.setText("Door open");
					if(!mSndOpenDoor.isPlaying() && !isRefresh)
						mSndOpenDoor.start();
				}
			}
			
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				mTxvMessage.setText("2015 AT&T Developer Summit");
				getSmartPlugStatus();
			}
		});
	}
	
	
	public void getSmartPlugStatus(){
		mTxvMessage.setText("Get smart plug status ...");
		HttpGetSmartPlugInfo info = new HttpGetSmartPlugInfo();
		info.setOnRequestComplete(new HttpGetSmartPlugInfo.OnRequest() {
			@Override
			public void onStatus(String status) {
				// TODO Auto-generated method stub
				//mBtnSmartPlug.setText("Smart Plug - " + status);
				if(status.equals("on")){
					mImgSmartPlug.setImageResource(R.drawable.light_on);
					mTxvPlugMsg.setText("Light on");
					/*
					if(!mSndLightOn.isPlaying() && !isRefresh)
						mSndLightOn.start();*/
				}
				else{
					mImgSmartPlug.setImageResource(R.drawable.light_off);
					mTxvPlugMsg.setText("Light off");
					/*
					if(!mSndLightOff.isPlaying() && !isRefresh)
						mSndLightOff.start();*/
				}
			}
			
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				mTxvMessage.setText("2015 AT&T Developer Summit");
				isRefresh = false;
			}
		});
	}
	
	
    private void getToken(){
    	HttpGetAuthTokens call = new HttpGetAuthTokens();
    	call.setOnRequestComplete(new HttpGetAuthTokens.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				Log.d("api", "getToken - " + result);
			}
		});
    }


    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (R.id.action_scan == id) {
        	Intent intent = new Intent(this, ScanActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
    public class MyoReceiver extends BroadcastReceiver {
    	  
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		String status = intent.getStringExtra("status");
    		if(status.equals("DOOR")){
    			getDoorStatus();
    		}
			else if(status.equals("PLUG")){
				getSmartPlugStatus();
			}
    		
    		/*
    		if(myoEvent.equals(Pose.DOUBLE_TAP.toString())){
    		}
    		else if(myoEvent.equals(Pose.FINGERS_SPREAD.toString())){
    		}
			else if(myoEvent.equals(Pose.FIST.toString())){
			}
			else if(myoEvent.equals(Pose.REST.toString())){
			}
			else if(myoEvent.equals(Pose.WAVE_IN.toString())){
			}
			else if(myoEvent.equals(Pose.WAVE_OUT.toString())){
			}
    		// Pose.UNKNOWN
			else{
				
			}*/
    	}
    }
    
    
    public void registerMyoReceiver() {
    	this.registerReceiver(myoReceiver, new IntentFilter(Define.MYO_RECEIVER));
    }
    
    
    public void unregisterMyoReceiver() {
        this.unregisterReceiver(myoReceiver);
    }
    
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(mSndClosedDoor != null){
    		mSndClosedDoor.stop();
    		mSndClosedDoor.release();
    		mSndClosedDoor = null;
        }
    	
    	if(mSndOpenDoor != null){
    		mSndOpenDoor.stop();
    		mSndOpenDoor.release();
    		mSndOpenDoor = null;
        }
    	
    	if(mSndLightOff != null){
    		mSndLightOff.stop();
    		mSndLightOff.release();
    		mSndLightOff = null;
        }
    	
    	if(mSndLightOn != null){
    		mSndLightOn.stop();
    		mSndLightOn.release();
    		mSndLightOn = null;
        }
    	
    	mLocationManager.removeUpdates(mLocationListener);
    	stopService(new Intent(this, Background.class));
    }
    
    
    
    public void refresh(){
    	isRefresh = true;
    	getForecast();
    }
}
