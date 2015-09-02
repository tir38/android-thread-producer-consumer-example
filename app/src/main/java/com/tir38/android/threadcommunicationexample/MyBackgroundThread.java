package com.tir38.android.threadcommunicationexample;

import android.util.Log;

import java.util.Queue;

public class MyBackgroundThread extends Thread {

    private static final String MY_BACKGROUND_THREAD = "my background thread";
    private static final String TAG = MyBackgroundThread.class.getSimpleName();
    private static final long SENSOR_POLLING_INTERVAL_MILLIS = 1000;

    private Queue mQueue;
    private final SoundMeter mSoundMeter;

    public MyBackgroundThread(Queue queue) {
        super(MY_BACKGROUND_THREAD);
        mQueue = queue;
        mSoundMeter = new SoundMeter();
    }

    @Override
    public void run() {
        super.run();

        mSoundMeter.start();

        Log.d(TAG, "starting sensor read");
        while (true) {
            long currentTimeMillis = System.currentTimeMillis();
            double amplitude = mSoundMeter.getAmplitudeEMA();
            DataMessage dataMessage = new DataMessage(new DataPoint(currentTimeMillis, amplitude));
            try {
                mQueue.add(dataMessage);
                Log.d(TAG, "adding new data point to queue: " + amplitude);
                Thread.sleep(SENSOR_POLLING_INTERVAL_MILLIS);
            } catch (InterruptedException e) {
                Log.d(TAG, "exception caught; interrupting thread");
                Thread.currentThread().interrupt();
                break;
                // shutdown via interrupt: http://www.javaspecialists.eu/archive/Issue056.html
            }
        }
    }

    public void stopSensorRead() {
        interrupt();
        mSoundMeter.stop();
        Log.d(TAG, "stop sensor read");
    }
}
