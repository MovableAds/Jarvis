package com.movableads.jarvis;

import android.app.Application;

public class App extends Application{
	
	public MainActivity mMain = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	}
	
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}
	
	
	public void changeDoor(){
		mMain.getDoorStatus();
	}
	
	public void changePlug(){
		mMain.getSmartPlugStatus();
	}
	
	public void refresh(){
		mMain.refresh();	
	}
	
	
}
