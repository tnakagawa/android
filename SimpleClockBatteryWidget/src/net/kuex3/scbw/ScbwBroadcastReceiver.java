package net.kuex3.scbw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * �o�b�e���[�l��M�p���V�[�o
 * 
 * @author nakagat
 */
public class ScbwBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// �X�P�[���ƃ��x���擾
		int scale = intent.getIntExtra("scale", 0);
		int level = intent.getIntExtra("level", -1);
		String battery = null;
		// �l����
		if (scale > 0 && level > -1) {
			// �o�b�e���[�l�Z�o
			int per = (level * 100) / scale;
			battery = " " + per + "%";
		}
		Log.d("SCBW", "BATTERY:" + scale + "/" + level + "/" + battery);
		// �o�b�e���[�l���݃`�F�b�N
		if (battery != null) {
			// �X�V�p�C���e���g����
			Intent updateIntent = new Intent(context, ScbwProvider.class);
			updateIntent.setAction(ScbwProvider.UPDATE_ACTION);
			updateIntent.putExtra("battery", battery);
			// �u���[�h�L���X�g
			context.sendBroadcast(updateIntent);
		}
	}
}
