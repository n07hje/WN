package com.example.wifinfc;

import android.app.Service;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.widget.Toast;

public class WifiStarterClass extends Service{

	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public int onStartCommand(Intent intent,int flag, int serviceId)
	{
		WifiManager wm = (WifiManager) this.getSystemService(WIFI_SERVICE);
		wm.setWifiEnabled(true);
		
		return super.onStartCommand(intent, flag, serviceId);
	}
	
	@Override
	public void onDestroy()
	{
		Toast.makeText(this, "WiFi indító SERVICE befejezve.", Toast.LENGTH_LONG).show();
	}
	
}