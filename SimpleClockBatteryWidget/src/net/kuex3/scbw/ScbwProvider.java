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
 * �E�B�W�F�b�g�v���o�C�_
 * 
 * @author nakagat
 */
public class ScbwProvider extends AppWidgetProvider {

	/** �X�V�A�N�V������ */
	public static final String UPDATE_ACTION = ScbwProvider.class.getName() + ".UPDATE";

	/** �X�V�ő�Ԋu�i�P���j */
	private static final long INTERVAL = 60000;

	@Override
	public void onDisabled(Context context) {
		Log.d("SCBW", "ScbwProvider#onDisabled");
		try {
			// �T�[�r�X��~
			Intent serviceIntent = new Intent(context, ScbwService.class);
			context.stopService(serviceIntent);

			// �A���[���T�[�r�X�̃u���[�h�L���X�g��~
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
			// �A�N�V�������擾
			String action = intent.getAction();
			// �X�V�A�N�V�����ł��邩����
			if (UPDATE_ACTION.equals(action)) {
				// �o�b�e���[�l�擾
				String battery = intent.getStringExtra("battery");
				Log.d("SCBW", "BATTERY:" + battery);
				// �o�b�e���[�l���ݔ���
				if (battery == null) {
					// �Ȃ��ꍇ�͋󕶎�
					battery = "";
				}
				// �X�V�p�I�u�W�F�N�g����
				RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.main);
				// ���ݓ����擾
				Date now = new Date(System.currentTimeMillis());
				// ������������
				CharSequence text1 = DateFormat.format("kk:mm", now);
				views.setTextViewText(R.id.textView1, text1);
				// �����j���A�o�b�e���[�l��������
				CharSequence text2 = DateFormat.format("MM/dd(E)", now) + battery;
				views.setTextViewText(R.id.textView2, text2);
				Log.d("SCBW", "rewrite:" + text1 + "/" + text2);
				//�@�E�B�W�F�b�g�X�V
				ComponentName provider = new ComponentName(context, this.getClass());
				AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
				appWidgetManager.updateAppWidget(provider, views);
				//�@����X�V�����ݒ�i���̕����X�V����鎞�Ԃ��Z�o�j
				long next = System.currentTimeMillis();
				long plus = INTERVAL - (long) next % INTERVAL;
				Log.d("SCBW", "after:" + plus + "msec");
				// ����X�V�p�C���e���g����
				Intent updateIntent = new Intent(context, this.getClass());
				updateIntent.setAction(UPDATE_ACTION);
				updateIntent.putExtra("battery", battery);
				// �u���[�h�L���X�g�p�C���e���g�����i�l�͍X�V�j
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
			// ����̍X�V�i�o�b�e���[�l�͂Ȃ��j
			Intent intent = new Intent(context, this.getClass());
			intent.setAction(UPDATE_ACTION);
			context.sendBroadcast(intent);
			// �o�b�e���[�l�擾�p�T�[�r�X�N���i�E�B�W�F�b�g���Q���͂�ꂽ�ꍇ�p�ɁA�ċN�����ۂ��Ȃ��Ă�j
			Intent serviceIntent = new Intent(context, ScbwService.class);
			context.stopService(serviceIntent);
			context.startService(serviceIntent);
		} catch (Exception e) {
			Log.e("SCBW", "Exception:" + e.getMessage(), e);
		}
		super.onUpdate(context, appWidgetManager, appWidgetIds);
	}
}
