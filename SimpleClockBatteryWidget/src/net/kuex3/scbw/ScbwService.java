package net.kuex3.scbw;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * �o�b�e���[�l�擾�p�T�[�r�X
 * 
 * @author nakagat
 */
public class ScbwService extends Service {
	
	/** �u���[�h�L���X�g���V�[�o�i�o�b�e���[�C�x���g��M�j */
	private ScbwBroadcastReceiver receiver = new ScbwBroadcastReceiver();
	
	@Override
	public void onCreate() {
		Log.d("SCBW", "Service#onCreate");
		// �o�b�e���[�ύX�̃t�B���^�[����
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		//�@���V�[�o�o�^
		registerReceiver(receiver, filter);
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		Log.d("SCBW", "Service#onDestroy");
		// ���V�[�o����
		unregisterReceiver(receiver);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d("SCBW", "Service#onBind");
		// �o�C���h����Ă��Ȃ����Ԃ��Ȃ�
		return null;
	}
}
