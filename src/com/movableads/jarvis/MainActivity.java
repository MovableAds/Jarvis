package com.movableads.jarvis;

import com.movableads.jarvis.R;
import com.movableads.jarvis.http.HttpGetAuthTokens;
import com.movableads.jarvis.http.HttpGetForecast;
import com.movableads.jarvis.http.HttpGetThermostInfo;
import com.movableads.jarvis.http.HttpReturnCode;
import com.movableads.jarvis.http.OrderChangeDoorStatus;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	
	private LocationManager mLocationManager = null;
	private TextView mMessage = null;
	//private MyoReceiver myoReceiver = new MyoReceiver();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
      //registerMyoReceiver();
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, mLocationListener);
        
        mMessage = (TextView)findViewById(R.id.message);
        getToken();
        startService(new Intent(this, Background.class));
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
				Global.latitude = location.getLatitude();
				Global.longitude = location.getLongitude();
				getForecast();
				//mLocationManager.removeUpdates(this);
			}
		}
	};
    
	
	private void getForecast(){
		HttpGetForecast forecast = new HttpGetForecast();
		forecast.setOnRequestComplete(new HttpGetForecast.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				if(result.equals(HttpReturnCode.SUCCESS)){
					mLocationManager.removeUpdates(mLocationListener);
					getCurrentThermostInfo();
				}
			}
		});
	}
    
	
	private void getCurrentThermostInfo(){
		HttpGetThermostInfo info = new HttpGetThermostInfo();
		info.setOnRequestComplete(new HttpGetThermostInfo.OnRequest() {
			@Override
			public void onComplete(String result) {
				// TODO Auto-generated method stub
				Toast.makeText(getApplicationContext(), "init complete", Toast.LENGTH_SHORT).show();
				Toast.makeText(getApplicationContext(), Double.toString(Global.currentThermostatTemperature) + "도", Toast.LENGTH_SHORT).show();
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

    /*
    public class MyoReceiver extends BroadcastReceiver {
    	  
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		String myoEvent = intent.getStringExtra("myoEvent");
    		mMessage.setText(myoEvent);
    		if(myoEvent.equals(Pose.DOUBLE_TAP.toString())){
    			
    		}
    		else if(myoEvent.equals(Pose.FINGERS_SPREAD.toString())){
    			OrderSetTemperature order = new OrderSetTemperature(OrderType.OPEN_DOOR);
    			Toast.makeText(getApplicationContext(), "open door", Toast.LENGTH_SHORT).show();
    		}
			else if(myoEvent.equals(Pose.FIST.toString())){
			    			
			}
			else if(myoEvent.equals(Pose.REST.toString())){
				
			}
			else if(myoEvent.equals(Pose.WAVE_IN.toString())){
				Toast.makeText(getApplicationContext(), "down temparature", Toast.LENGTH_SHORT).show();
				OrderSetTemperature order = new OrderSetTemperature(OrderType.DOWN_TEMPERATURE);
			}
			else if(myoEvent.equals(Pose.WAVE_OUT.toString())){
				Toast.makeText(getApplicationContext(), "up temparature", Toast.LENGTH_SHORT).show();
				OrderSetTemperature order = new OrderSetTemperature(OrderType.UP_TEMPERATURE);
			}
    		// Pose.UNKNOWN
			else{
				
			}
    	}
    }
    
    
    public void registerMyoReceiver() {
    	this.registerReceiver(myoReceiver, new IntentFilter(Define.MYO_RECEIVER));
    }
    
    
    public void unregisterMyoReceiver() {
        this.unregisterReceiver(myoReceiver);
    }
    */
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mLocationManager.removeUpdates(mLocationListener);
    	stopService(new Intent(this, Background.class));
    }
}
