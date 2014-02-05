package net.kuex3.scbw;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * バッテリー値取得用サービス
 * 
 * @author nakagat
 */
public class ScbwService extends Service {
	
	/** ブロードキャストレシーバ（バッテリーイベント受信） */
	private ScbwBroadcastReceiver receiver = new ScbwBroadcastReceiver();
	
	@Override
	public void onCreate() {
		Log.d("SCBW", "Service#onCreate");
		// バッテリー変更のフィルター生成
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		//　レシーバ登録
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d("SCBW", "Service#onDestroy");
		// レシーバ解除
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SCBW", "Service#onBind");
		// バインドされてもないも返さない
		return null;
	}
}
