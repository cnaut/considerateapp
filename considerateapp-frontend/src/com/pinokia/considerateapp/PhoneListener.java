package com.pinokia.considerateapp;

import android.media.AudioManager;
import android.telephony.PhoneNumberUtils;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class PhoneListener extends PhoneStateListener {
  private AudioManager am;
  
  public void onCallStateChanged(int state,String incomingNumber){
    if(FlipService.isRunning() && FlipService.faceDown && ConsiderateAppActivity.whitelistEnabled) {
      switch(state)
      {
        case TelephonyManager.CALL_STATE_IDLE:
          Log.d("DEBUG", "IDLE");
          break;
        case TelephonyManager.CALL_STATE_OFFHOOK:
          Log.d("DEBUG", "OFFHOOK");
          break;
        case TelephonyManager.CALL_STATE_RINGING:
          if(compareIncomingToFavorites(incomingNumber)) {
            FlipService.setToPrevAudioState();
            Log.d("DEBUG", "Turn to previous audio state");
          }
          Log.d("DEBUG", "RINGING");
          break;
      }
    }
  }
 
}