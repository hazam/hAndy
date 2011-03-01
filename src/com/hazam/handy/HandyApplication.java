package com.hazam.handy;

import java.util.HashMap;

import android.app.Application;

public class HandyApplication extends Application {
	
	public static String IMAGE_CACHE_APPSERVICE = "imageCacheService";
	
	@Override
	public void onCreate() {
		super.onCreate();
		if (appServices == null) {
			appServices = new HashMap<String, Object>();
		}
	}
	
	private static HashMap<String, Object> appServices;
	
	public static Object getAppService(String name) {
		return appServices.get(name);
	}
	
	protected static void registerAppService(String name, Object serv) {
		appServices.put(name, serv);
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		appServices = null;
	}
}
