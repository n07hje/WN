package com.example.wifinfc;


import android.R.color;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

/**
 * C�l: 
 * 1. : a program el�sz�r kapcsolja be a wifit, majd-TAG gy�jts�n inform�ci�kat
 * 
 * 
 *2. : 	kapcsolja be az NFC-t �s figyeljen az ACTION_TAG_Discovered esem�nyre, majd 
 *   	ha bek�vetkezik mentse bele 1 v�ltoz�ba.
 * @author David2
 *
 */
public class MainActivity extends Activity {

	NfcAdapter na;
	WifiManager wm;
	WifiInfo wi;
	TextView ssidTv;
	TextView strengthTv;
	TextView ipTv;
	int elozoErosseg=0;
	int ATLAG;//int mert csak kb. �rt�k
	int ERTEK;
	int darab;
	
	//WIFI F�ggv�nyei
	void compareStrength(int elozo, int uj)
	{
		if(elozo > uj)
			Toast.makeText(this, "Jeler�ss�g cs�kkent", Toast.LENGTH_SHORT).show();
		else	
			Toast.makeText(this, "Jeler�ss�g n�tt", Toast.LENGTH_SHORT).show();
	}
	
	public void updateTextViews()//jeler�ss�g v�ltoz�s�n�l:er�ss�get sz�mol �s ezt bele�rja egy tv-be
	{
		
		wi=wm.getConnectionInfo();//MEG KELL H�VNI MIEL�TT FRISS�TEN�M?MER TEZ NEM FRISS�T // kell a pluginhez!!
		
		int jelerosseg=wi.getRssi();
		int jelerossegSzamolt = wm.calculateSignalLevel(jelerosseg,100); //eddig
		//�sszehasonl�t�shoz
		compareStrength(elozoErosseg,jelerossegSzamolt);
		elozoErosseg=jelerossegSzamolt;
		//�tlaghoz
		darab++;
		ERTEK+=jelerossegSzamolt;
		ATLAG=ERTEK/darab;
		
		
		ssidTv.setText("\n SSID : " + wi.getSSID() );
		ssidTv.setTextSize(20);	
		ssidTv.setTextColor(Color.BLUE);
		
		String ipAddress = Formatter.formatIpAddress(wi.getIpAddress());
		ipTv.setText("IP c�m : " + ipAddress );
		ipTv.setTextSize(20);	
		
		if(79<jelerossegSzamolt)
		{
			strengthTv.setText("Jeler�ss�g : " + jelerossegSzamolt + "% - Kiv�l�  " + "\n           �tlag: " + ATLAG+"%");
			strengthTv.setTextSize(20);
		}
		else if( 59 < jelerossegSzamolt && jelerossegSzamolt < 80)
		{
			strengthTv.setText("Jeler�ss�g : " + jelerossegSzamolt + "% - Megfelel�  " + "\n           �tlag: " + ATLAG+"%" );
			strengthTv.setTextSize(20);
		}
		else if( 39 < jelerossegSzamolt && jelerossegSzamolt < 60)
		{
			strengthTv.setText("Jeler�ss�g : " + jelerossegSzamolt + "% - K�zepes  " + "\n           �tlag: " + ATLAG+"%" );
			strengthTv.setTextSize(20);
		}
		else if( 19 < jelerossegSzamolt && jelerossegSzamolt < 40)
		{
			strengthTv.setText("Jeler�ss�g : " + jelerossegSzamolt + "% - Alacsony  " + "\n           �tlag: " + ATLAG+"%" );
			strengthTv.setTextSize(20);
		}
		else if( 0 <= jelerossegSzamolt && jelerossegSzamolt < 20)
		{
			strengthTv.setText("Jeler�ss�g : " + jelerossegSzamolt + "% - Rossz  " + "\n           �tlag: " + ATLAG+"%" );
			strengthTv.setTextSize(20);
		}
//		strengthTv.setText("Jeler�ss�g : " + jelerossegSzamolt + "%" );
//		strengthTv.setTextSize(20);
	}
	
	void WiFiStart()//Bekapcsolja a wifit (1. l�p�s)
	{
		if(wm.isWifiEnabled()==false)
		{
			Intent i = new Intent(this,WifiStarterClass.class);
			startService(i);
			this.stopService(i);
		
			Toast.makeText(getApplicationContext(), "WiFi bekapcsolva", Toast.LENGTH_SHORT).show();
	}	
		else
		{
			Toast.makeText(getApplicationContext(), "WiFi m�r be volt kapcsolva", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		
		ATLAG=0;//int mert csak kb. �rt�k
		ERTEK=0;
		darab=0;
			       
		this.registerReceiver(rssiReceiver, new IntentFilter(WifiManager.RSSI_CHANGED_ACTION));
		TextView titleTv = (TextView) this.findViewById(R.id.tvTitle);
		titleTv.append("Wi-Fi �s NFC m�r�s");
		titleTv.setTextSize(30);
		
		ssidTv = (TextView) this.findViewById(R.id.tvSSID);
		strengthTv = (TextView) this.findViewById(R.id.tvStrength);
		ipTv = (TextView) this.findViewById(R.id.tvIP);

		wm = (WifiManager)this.getSystemService(WIFI_SERVICE);
//		wi=wm.getConnectionInfo();
		na=NfcAdapter.getDefaultAdapter(this);
		
		WiFiStart();
		updateTextViews();
		
		
	}

	
	
	
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	
	
	
	//********************************************************************    RECEIVER    *****************
	private BroadcastReceiver rssiReceiver = new BroadcastReceiver(){
		
	@Override
	public void onReceive(Context context, Intent intent) {
	
		updateTextViews();
		}
	
	};
	
	
	
	
	
	
//	public class MyReceiver extends BroadcastReceiver
//	{
//
//		@Override
//		public void onReceive(Context context, Intent intent)
//		{
//			Toast.makeText(context, "Jeler�ss�g megv�ltozott", Toast.LENGTH_LONG).show();
//			
//		}
//		
//	}
	//mAnifest
	/*       <receiver android:name="com.example.wifinfc.MainActivity.rssiReceiver">
     		 <intent-filter>
        		 <action android:name="android.net.wifi.RSSI_CHANGED_ACTION">
      				</action>
     		 </intent-filter>
  		 </receiver>
	 * 
	 */
	
}
