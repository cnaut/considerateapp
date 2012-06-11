package com.pinokia.considerateapp;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;
import java.util.Date;

public class FlipService extends Service implements SensorEventListener {
    // FlipService is the background process that checks for when the phone
    // is flipped over so that it can silence the phone.
    
    //This is another singleton service. There's only ever going to be one instance
    // of this service running, so we put some bookkeeping to ensure that in
    // the static class.

    // This is a bit of a hacky way to see if the service is running, but from
    // sources, it looks like it's the best way to do it.
    protected static boolean running = false;

    // This makes the running variable read-only.
    public static boolean isRunning() {
        return running;
    }

    public static final String tag = "FLIP_SERVICE";
    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private static AudioManager am;

    private static boolean faceDown = false;
    private static int prevAudioState;

    public FlipService() {
    }

    // Code to toggle the service
    public static boolean toggleService(Context context) {
        if (isRunning()) {
            stop(context, true);
            return false;
        } else {
            start(context, true);
            return true;
        }
    }

    //Make sure we save the last state of the service so that we can load it
    //on first boot.
    private static void writePreference(boolean pref, Context c) {
        SharedPreferences prefs = c.getSharedPreferences(
                ConsiderateAppActivity.prefsName, 0);
        SharedPreferences.Editor prefsEdit = prefs.edit();
        prefsEdit.putBoolean("considerate_mode", pref);
        prefsEdit.commit();
    }

    public static void start(Context context, boolean showToast) {
        if (isRunning())
            return;
        Intent serviceIntent = new Intent(context, FlipService.class);
        if (showToast) {
            Toast.makeText(context, "Considerate Mode is enabled.",
                    Toast.LENGTH_SHORT).show();
        }
        context.startService(serviceIntent);
        writePreference(true, context);
        running = true;
    }

    public static void stop(Context context, boolean showToast) {
        if (!isRunning())
            return;
        Intent serviceIntent = new Intent(context, FlipService.class);
        if (showToast) {
            Toast.makeText(context, "Considerate Mode is disabled.",
                    Toast.LENGTH_SHORT).show();
        }
        context.stopService(serviceIntent);
        writePreference(false, context);
        running = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        running = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        running = false;
        sensorManager.unregisterListener(this);
        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        assert (tm != null);
        tm.listen(phoneListener, PhoneStateListener.LISTEN_NONE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Configure accelerometer
        accelerometerSensor = sensorManager
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);

        List<Sensor> sensorList = sensorManager
            .getSensorList(Sensor.TYPE_ACCELEROMETER);
        if (sensorList.size() > 0) {
            accelerometerSensor = sensorList.get(0);
        } else {
            Log.e(tag, "No accelerometer present!");
        }

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        prevAudioState = am.getRingerMode();

        final TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        assert (tm != null);
        tm.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        return 1;
    }

    //If the phone's z-acceleration shifts by more than 0.2 m/s^2
    //in one polling cycle, that means we're not face-down on a stable surface
    private float error = (float) 0.2;

    private boolean isSimilarAcceleration(float prev, float curr) {
        if (prev + error > curr && prev - error < curr) {
            return true;
        }
        return false;
    }

    private float prev_z, curr_z;
    private Date startingTime = null;

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Do nothing. We just have to override this.
    }

    public void onSensorChanged(SensorEvent event) {
        boolean newFaceDown = faceDown;

        Date currTime = new Date();

        // Handle accelerometer change
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            curr_z = event.values[2];
            Log.v(tag, "ACCELEROMETER:" + curr_z);
            if (prev_z < -7 && curr_z < -7
                    && isSimilarAcceleration(prev_z, curr_z)) {
                if (startingTime == null) {
                    startingTime = new Date();
                } else if (currTime.getTime() - startingTime.getTime() > 5000) {
                    newFaceDown = true;
                }

            } else {
                startingTime = null;
                newFaceDown = false;
            }
        }

        // Only silence the phone when transitioning from face up to face down
        if (!faceDown && newFaceDown) {
            // Change phone to Silent mode
            prevAudioState = am.getRingerMode();
            Log.i(tag, "going silent");
            am.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            SharedPreferences prefs = this.getSharedPreferences(
                    ConsiderateAppActivity.prefsName, 0);
            int score = 5 + prefs.getInt("phonescore", 99);
            SharedPreferences.Editor prefsEdit = prefs.edit();
            prefsEdit.putInt("phonescore", score);
            prefsEdit.commit();
        } else if (faceDown && !newFaceDown) {
            Log.i(tag, "ungoing silent");
            // Change phone back to previous state
            am.setRingerMode(prevAudioState);
        }

        faceDown = newFaceDown;
        prev_z = curr_z;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    PhoneStateListener phoneListener = new PhoneStateListener() {

        public void onCallStateChanged(int state, String incomingNumber) {
            if (isRunning() && faceDown) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        //When we get a phone call, we check to see if the number is a
                        //favorite contact, and if so, we let the call through.
                        Uri uri = Uri.withAppendedPath(
                                PhoneLookup.CONTENT_FILTER_URI,
                                Uri.encode(incomingNumber));
                        Cursor contactLookup = getContentResolver().query(uri,
                                new String[] { PhoneLookup.STARRED }, null, null,
                                null);
                        if (contactLookup != null) {
                            assert (contactLookup.getCount() == 1);
                            contactLookup.moveToNext();
                            int starred = contactLookup.getInt(contactLookup
                                    .getColumnIndex(PhoneLookup.STARRED));
                            if (starred == 1) {
                                am.setRingerMode(prevAudioState);
                                Log.d("DEBUG", "Turn to previous audio state");
                            }
                        }
                        Log.d("DEBUG", "RINGING");
                        break;
                }
            }
        }

    };
}
