package com.tir38.android.threadcommunicationexample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ConcurrentLinkedQueue;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int QUEUE_CAPACITY = 50; // for use if we switch to BlockingQueue
    private static final long QUEUE_POLLING_INTERVAL_MILLIS = 500;

    private ConcurrentLinkedQueue<DataMessage> mQueue;

    private MyBackgroundThread mMyBackgroundThread;
    private Handler mResponseHandler;
    private TextView mOutputValueTextView;
    private boolean mSensing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mQueue = new ConcurrentLinkedQueue<>();

        mOutputValueTextView = (TextView) findViewById(R.id.output_value_text_view);

        Button startButton = (Button) findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMyBackgroundThread == null) { // only start one thread at a time
                    startBackgroundThread();
                    startReadingFromBackground();
                }
            }
        });

        Button stopButton = (Button) findViewById(R.id.stop_button);
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBackgroundThread();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopBackgroundThread();
    }

    private void startReadingFromBackground() {
        mResponseHandler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handleMessages();
                mResponseHandler.postDelayed(this, QUEUE_POLLING_INTERVAL_MILLIS);
            }
        };
        mResponseHandler.postDelayed(runnable, QUEUE_POLLING_INTERVAL_MILLIS);
    }

    private void handleMessages() {
        // read until no more messages
        for (DataMessage message = mQueue.poll(); message != null; message = mQueue.poll()) {  // if we switch to a BlockingQueue, we don't want to block this thread so don't use .take()

            if (mSensing) { // its possible that we'll get a few more messages after we stop sensing. if so, ignore them
                double value = message.getDataPoint().getValue();
                value = Math.round(value * 100.0) / 100.0; // round to two digits
                mOutputValueTextView.setText(Double.toString(value));
                Log.d(TAG, "UI Thread reading in value from queue: " + value);
            }
        }
    }

    private void startBackgroundThread() {
        mMyBackgroundThread = new MyBackgroundThread(mQueue);
        mMyBackgroundThread.start();
        Toast.makeText(this, "starting...", Toast.LENGTH_SHORT).show();
        mSensing = true;
    }

    private void stopBackgroundThread() {
        if (mMyBackgroundThread != null) {
            mMyBackgroundThread.stopSensorRead();
            mMyBackgroundThread = null; // thread is now dead, we need to free from memory
            Toast.makeText(this, "stopping...", Toast.LENGTH_SHORT).show();
            mSensing = false;
            mOutputValueTextView.setText("--");
        }
    }
}
