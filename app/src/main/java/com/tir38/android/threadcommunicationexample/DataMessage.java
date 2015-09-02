package com.tir38.android.threadcommunicationexample;

public class DataMessage {

    private final DataPoint mDataPoint;

    public DataMessage(DataPoint dataPoint) {
        mDataPoint = dataPoint;
    }

    public DataPoint getDataPoint() {
        return mDataPoint;
    }
}
