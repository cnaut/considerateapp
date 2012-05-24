package com.pinokia.considerateapp;

import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneListener extends PhoneStateListener {

	public void onCallStateChanged(int state, String incomingNumber) {
		if (FlipService.isRunning() && FlipService.faceDown) {
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE:
				Log.d("DEBUG", "IDLE");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.d("DEBUG", "OFFHOOK");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				for (int i = 0; i < ConsiderateAppActivity.whitelist.size(); i++) {
					if (PhoneNumberUtils.compare(
							ConsiderateAppActivity.whitelist.get(i),
							incomingNumber)) {
						FlipService.setToPrevAudioState();
						Log.d("DEBUG", "Turn to previous audio state");
					}
				}
				Log.d("DEBUG", "RINGING");
				break;
			}
		}
	}

}
