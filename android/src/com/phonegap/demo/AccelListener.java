package com.phonegap.demo;

import static android.hardware.SensorManager.DATA_X;
import static android.hardware.SensorManager.DATA_Y;
import static android.hardware.SensorManager.DATA_Z;
import android.hardware.SensorManager;
import android.content.Context;
import android.hardware.SensorListener;
import android.webkit.WebView;

public class AccelListener implements SensorListener{

	WebView mAppView;
	Context mCtx;
	private SensorManager sensorManager;

	private float x, y, z;
	private float azimuth, pitch, roll;

//	private long lastUpdate = -1;

	private long lastRapidAccelChange = -1;
	private long lastShake = -1;

	private float changeMagnitude = (float)7.25;
	private int shakeSpan = 250;
	private int shakeDelay = 1000;

	private static final String SHAKE_CALL = "javascript:navigator.accelerometer.gotShaken();";
	
	AccelListener(Context ctx, WebView appView)
	{
		mCtx = ctx;
		mAppView = appView;
		sensorManager = (SensorManager) mCtx.getSystemService(Context.SENSOR_SERVICE);
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
	
	public void onAccuracyChanged(int sensor, int accuracy) {
		// This should call the FAIL method
	}
	
	public void onSensorChanged(int sensor, float[] values) {
                if (values.length < 3)
                        return;

//                int flipVals = 0;
//                if (values.length >= 6)
//                        flipVals = 3;

                if (sensor == SensorManager.SENSOR_ACCELEROMETER) {
			checkShake(values[0], values[1], values[2]);
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

	private void checkShake(float newX, float newY, float newZ) {
		float diffX = newX - x;
		float diffY = newY - y;
		float diffZ = newZ - z;
		float totalDiff = (float)Math.sqrt( diffX*diffX + diffY*diffY + diffZ*diffZ );

		if ( totalDiff >= changeMagnitude ) {
			long curTime = System.currentTimeMillis();
			if (curTime - lastRapidAccelChange < shakeSpan && curTime - lastShake > shakeDelay) {
				lastShake = curTime;
				mAppView.loadUrl(SHAKE_CALL);
			}
			lastRapidAccelChange = curTime;
		}
	}

	public float getX() { return x; }
	public float getY() { return y; }
	public float getZ() { return z; }

	public float getAzimuth() { return azimuth; }
	public float getPitch() { return pitch; }
	public float getRoll() { return roll; }
}
