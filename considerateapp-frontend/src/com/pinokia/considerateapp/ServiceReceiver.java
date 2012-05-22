package com.pinokia.considerateapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class ServiceReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PhoneListener phoneListener = new PhoneListener();
		TelephonyManager telephony = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);

		telephony.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
	}
}