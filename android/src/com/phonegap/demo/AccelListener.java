package com.phonegap.demo;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.DATA_Z;
import android.hardware.SensorManager;
import android.content.Context;
import android.hardware.SensorListener;
import android.webkit.WebView;

import java.util.HashMap;

public class AccelListener implements SensorListener{

	WebView mAppView;
	Context mCtx;
	private SensorManager sensorManager;
	private ArgTable arguments;

	private HashMap<String, ShakeListener> shakeListeners;

	private float x, y, z;
	private float azimuth, pitch, roll;

//	private long lastUpdate = -1;
//	private static final int UPDATE_DELAY = 50;

//	private long lastRapidAccelChange = -1;
//	private long lastShake = -1;

//	private static final float CHANGE_MAG = (float)7.25;
//	private static final int SHAKE_SPAN = 250;
//	private static final int SHAKE_DELAY = 1000;


	private class ShakeListener {
		public final String UID;
		public final float CHANGE_MAG;
		public final int SHAKE_SPAN;
		public final int SHAKE_DELAY;

		public long lastRapidAccelChange = -1;
		public long lastShake = -1;
		public int x, y, z;

		private static final String SHAKE_CALL = "javascript:navigator.accelerometer.gotShaken();";
	
		public ShakeListener(String uid, float changeMag, int shakeSpan, int shakeDelay) {
			UID = uid;
			CHANGE_MAG = changeMag;
			SHAKE_SPAN = shakeSpan;
			SHAKE_DELAY = shakeDelay;

			this.x = 0;
			this.y = 0;
			this.z = 0;
		}

		private void checkShake(float newX, float newY, float newZ) {
			float diffX = newX - this.x;
			float diffY = newY - this.y;
			float diffZ = newZ - this.z;
			float totalDiff = (float)Math.sqrt( diffX*diffX + diffY*diffY + diffZ*diffZ );
			
			if ( totalDiff >= CHANGE_MAG ) {
				long curTime = System.currentTimeMillis();
				if (curTime - lastRapidAccelChange < SHAKE_SPAN && curTime - lastShake > SHAKE_DELAY) {
					lastShake = curTime;
					arguments.put("shakeID", UID);
					mAppView.loadUrl(SHAKE_CALL);
				}
				lastRapidAccelChange = curTime;
			}
		}
	}

	AccelListener(Context ctx, WebView appView, ArgTable args)
	{
		mCtx = ctx;
		mAppView = appView;
		sensorManager = (SensorManager) mCtx.getSystemService(Context.SENSOR_SERVICE);
		arguments = args;

		shakeListeners = new HashMap<String, ShakeListener>();
	}
	
	public void start()
	{
//	    System.out.println("--- Registering listener for acceleration and orientation.");
	    
	    sensorManager.registerListener(this,
	            SensorManager.SENSOR_ACCELEROMETER,
	            SensorManager.SENSOR_DELAY_GAME);
	    sensorManager.registerListener(this,
	            SensorManager.SENSOR_ORIENTATION,
	            SensorManager.SENSOR_DELAY_GAME);
	}
	
	public void stop()
	{
//		System.out.println("--- Stopping accel/orient listeners.");
		
		sensorManager.unregisterListener(this);
	}

	public void addShakeListener(String uid, float changeMag, int shakeSpan, int shakeDelay) {
		shakeListeners.put(uid, new ShakeListener(uid, changeMag, shakeSpan, shakeDelay));
	}

	public void removeShakeListener(String uid) {
		shakeListeners.remove(uid);
	}
	
	public void onAccuracyChanged(int sensor, int accuracy) {
		// This should call the FAIL method
	}
	
	public void onSensorChanged(int sensor, float[] values) {
//		System.out.println("changing sensor data");

		if (values.length < 3)
                        return;

//                int flipVals = 0;
//                if (values.length >= 6)
//                        flipVals = 3;

                if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
			checkShakes(values[0], values[1], values[2]);
                        x = values[0];
                        y = values[1];
                        z = values[2];
//			System.out.println("--- Returning acceleration data: " + x + ", " + y + ", " + z);
//                        mAppView.loadUrl("javascript:gotAcceleration(" + x + ", " + y + "," + z + ")");
                }
                else if (sensor == SensorManager.SENSOR_ORIENTATION) {
                        azimuth = values[0];
                        pitch = values[1];
                        roll = values[2];
//			System.out.println("--- Returning orientation data: " + azimuth + ", " + pitch + ", " + roll);
//                        mAppView.loadUrl("javascript:gotOrientation(" + azimuth + ", " + pitch + ", " + roll + ")");
                }
        }

	private void checkShakes(float newX, float newY, float newZ) {
		for ( ShakeListener listener : shakeListeners.values() ) {
			listener.checkShake(newX, newY, newZ);
		}
	}

	public float getX() { return x; }
	public float getY() { return y; }
	public float getZ() { return z; }

	public float getAzimuth() { return azimuth; }
	public float getPitch() { return pitch; }
	public float getRoll() { return roll; }
}
