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
 * ウィジェットプロバイダ
 * 
 * @author nakagat
 */
public class ScbwProvider extends AppWidgetProvider {

	/** 更新アクション名 */
	public static final String UPDATE_ACTION = ScbwProvider.class.getName() + ".UPDATE";

	/** 更新最大間隔（１分） */
	private static final long INTERVAL = 60000;

	@Override
	public void onDisabled(Context context) {
		Log.d("SCBW", "ScbwProvider#onDisabled");
		try {
			// サービス停止
			Intent serviceIntent = new Intent(context, ScbwService.class);
			context.stopService(serviceIntent);

			// アラームサービスのブロードキャスト停止
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
		Log.d("SCBW", "ScbwProvider#onReceive:" + intent.getAction());
		try {
			// アクション名取得
			String action = intent.getAction();
			// 更新アクションであるか判定
			if (UPDATE_ACTION.equals(action)) {
				// バッテリー値取得
				String battery = intent.getStringExtra("battery");
				Log.d("SCBW", "BATTERY:" + battery);
				// バッテリー値存在判定
				if (battery == null) {
					// ない場合は空文字
					battery = "";
				}
				// 更新用オブジェクト生成
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
				// 現在日時取得
				Date now = new Date(System.currentTimeMillis());
				// 時分部分生成
				CharSequence text1 = DateFormat.format("kk:mm", now);
				views.setTextViewText(R.id.textView1, text1);
				// 月日曜日、バッテリー値部分生成
				CharSequence text2 = DateFormat.format("MM/dd(E)", now) + battery;
				views.setTextViewText(R.id.textView2, text2);
				Log.d("SCBW", "rewrite:" + text1 + "/" + text2);
				//　ウィジェット更新
				ComponentName provider = new ComponentName(context, this.getClass());
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				appWidgetManager.updateAppWidget(provider, views);
				//　次回更新時刻設定（次の分が更新される時間を算出）
				long next = System.currentTimeMillis();
				long plus = INTERVAL - (long) next % INTERVAL;
				Log.d("SCBW", "after:" + plus + "msec");
				// 次回更新用インテント生成
				Intent updateIntent = new Intent(context, this.getClass());
				updateIntent.setAction(UPDATE_ACTION);
				updateIntent.putExtra("battery", battery);
				// ブロードキャスト用インテント生成（値は更新）
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
			// 初回の更新（バッテリー値はなし）
			Intent intent = new Intent(context, this.getClass());
			intent.setAction(UPDATE_ACTION);
			context.sendBroadcast(intent);
			// バッテリー値取得用サービス起動（ウィジェットが２枚はられた場合用に、再起動っぽくなってる）
			Intent serviceIntent = new Intent(context, ScbwService.class);
			context.stopService(serviceIntent);
			context.startService(serviceIntent);
		} catch (Exception e) {
			Log.e("SCBW", "Exception:" + e.getMessage(), e);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
