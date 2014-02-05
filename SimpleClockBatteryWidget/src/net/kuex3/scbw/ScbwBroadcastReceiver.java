package net.kuex3.scbw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * バッテリー値受信用レシーバ
 * 
 * @author nakagat
 */
public class ScbwBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// スケールとレベル取得
		int scale = intent.getIntExtra("scale", 0);
		int level = intent.getIntExtra("level", -1);
		String battery = null;
		// 値判定
		if (scale > 0 && level > -1) {
			// バッテリー値算出
			int per = (level * 100) / scale;
			battery = " " + per + "%";
		}
		Log.d("SCBW", "BATTERY:" + scale + "/" + level + "/" + battery);
		// バッテリー値存在チェック
		if (battery != null) {
			// 更新用インテント生成
			Intent updateIntent = new Intent(context, ScbwProvider.class);
			updateIntent.setAction(ScbwProvider.UPDATE_ACTION);
			updateIntent.putExtra("battery", battery);
			// ブロードキャスト
			context.sendBroadcast(updateIntent);
		}
	}
}
