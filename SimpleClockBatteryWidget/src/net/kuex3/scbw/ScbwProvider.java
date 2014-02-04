package net.kuex3.scbw;

import java.util.Date;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.RemoteViews;

/**
 * 
 * 
 * @author nakagat
 */
public class ScbwProvider extends AppWidgetProvider {

	public static final String UPDATE_ACTION = ScbwProvider.class.getName() + ".UPDATE";

	private static final long INTERVAL = 60000;

	@Override
	public void onDisabled(Context context) {
		Log.d("SCBW", "onDisabled:" + context.getPackageName() + ":::" + this.hashCode());
		try {
			Intent serviceIntent = new Intent(context, ScbwService.class);
			context.stopService(serviceIntent);

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Intent intent = new Intent(context, this.getClass());
			intent.setAction(UPDATE_ACTION);
			PendingIntent operation = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_ONE_SHOT);
			alarmManager.cancel(operation);
		} catch (Exception e) {
			Log.e("SCBW", "Exception:" + e.getMessage(), e);
		}

		super.onDisabled(context);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d("SCBW", "onReceive:" + intent.getAction() + ":::" + this.hashCode());
		try {
			String action = intent.getAction();
			if (UPDATE_ACTION.equals(action)) {
				String battery = intent.getStringExtra("battery");
				Log.d("SCBW", "BATTERY:" + battery);
				if (battery == null) {
					battery = "";
				}
				ComponentName provider = new ComponentName(context, this.getClass());
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
				Date now = new Date(System.currentTimeMillis());
				CharSequence text1 = DateFormat.format("kk:mm", now);
				CharSequence text2 = DateFormat.format("MM/dd(E)", now) + battery;
				Log.d("SCBW", "rewrite:" + text1 + "/" + text2);
				views.setTextViewText(R.id.textView1, text1);
				views.setTextViewText(R.id.textView2, text2);
				//
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				appWidgetManager.updateAppWidget(provider, views);
				//
				long next = System.currentTimeMillis();
				long plus = INTERVAL - (long) next % INTERVAL;
				Log.d("SCBW", "after:" + plus + "msec");
				Intent updateIntent = new Intent(context, this.getClass());
				updateIntent.setAction(UPDATE_ACTION);
				updateIntent.putExtra("battery", battery);
				PendingIntent operation = PendingIntent.getBroadcast(context, 0, updateIntent, PendingIntent.FLAG_UPDATE_CURRENT);
				AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
				alarmManager.set(AlarmManager.RTC, next + plus, operation);
			}
		} catch (Exception e) {
			Log.e("SCBW", "Exception:" + e.getMessage(), e);
		}
		super.onReceive(context, intent);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		Log.d("SCBW", "onUpdate:" + context.getPackageCodePath() + ":::" + this.hashCode());
		try {
			Intent intent = new Intent(context, this.getClass());
			intent.setAction(UPDATE_ACTION);
			context.sendBroadcast(intent);

			Intent serviceIntent = new Intent(context, ScbwService.class);
			context.startService(serviceIntent);
		} catch (Exception e) {
			Log.e("SCBW", "Exception:" + e.getMessage(), e);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
