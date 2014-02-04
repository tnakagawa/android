package net.kuex3.scbw;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

public class ScbwService extends Service {
	
	private ScbwBroadcastReceiver receiver = new ScbwBroadcastReceiver();
	
	@Override
	public void onCreate() {
		Log.d("SCBW", "Service#onCreate");
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d("SCBW", "Service#onDestroy");
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SCBW", "Service#onBind");
		return null;
	}

}
