package com.example.proxilock;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;

public class ProximitySensor implements SensorEventListener {

    public static final int SENSITIVITY_DELAY_MS = 200; // Sensibilité réglable
    private long lastEventTime = 0;
    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private PowerManager powerManager;
    private PowerManager.WakeLock wakeLock;
    private int lockCount = 0;
    private int unlockCount = 0;
    private OnEventListener eventListener;

    public interface OnEventListener {
        void onEvent();
    }

    // Méthode pour définir l’écouteur
    public void setEventListener(OnEventListener listener) {
        this.eventListener = listener;
    }


    public ProximitySensor(Context context) {
        //lehy b sensor l kol
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        ////power en generale
        powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        if (powerManager != null) {
            wakeLock = powerManager.newWakeLock(PowerManager.PROXIMITY_SCREEN_OFF_WAKE_LOCK, "ProxiLock:WakeLock");
        }
    }


    public void start() {
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (proximitySensor != null) {
            sensorManager.unregisterListener(this);
        }
        if (wakeLock.isHeld()) {
            wakeLock.release();
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastEventTime < SENSITIVITY_DELAY_MS) {
            return; // Ignorer l'événement si trop proche du précédent
        }

        lastEventTime = currentTime;

        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            if (event.values[0] < proximitySensor.getMaximumRange()) {
                // Verrouillage
                if (!wakeLock.isHeld()) {
                    wakeLock.acquire();
                    lockCount++;
                }
            } else {
                // Déverrouillage
                if (wakeLock.isHeld()) {
                    wakeLock.release();
                    unlockCount++;
                }
            }
            // Notifier l’activité
            if (eventListener != null) {
                eventListener.onEvent();
            }
        }
    }

    public int getLockCount() {
        return lockCount;
    }

    public int getUnlockCount() {
        return unlockCount;
    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not used
    }
}

