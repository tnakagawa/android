package net.kuex3.scbw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 
 * @author nakagat
 */
public class ScbwBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		int scale = intent.getIntExtra("scale", -1);
		int level = intent.getIntExtra("level", -1);
		Log.d("SCBW", "" + intent.getAction() + ":" + + level + "/" + scale);
		String battery = null;
		if (scale > -1 && level > -1) {
			int per = (level * 100) / scale;
			battery = " " + per + "%";
		}
		Log.d("SCBW", "BATTERY:" + scale + "/" + level + "/" + battery);
		if (battery != null) {
			Intent updateIntent = new Intent(context, ScbwProvider.class);
			updateIntent.setAction(ScbwProvider.UPDATE_ACTION);
			updateIntent.putExtra("battery", battery);
			context.sendBroadcast(updateIntent);
		}
	}
}
