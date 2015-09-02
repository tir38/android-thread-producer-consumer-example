package com.tir38.android.threadcommunicationexample;

public class DataPoint {

    private final long systemTimeMilis;
    private final double mValue;

    public DataPoint(long systemTimeMilis, double value) {
        mValue = value;
        this.systemTimeMilis = systemTimeMilis;
    }

    public long getSystemTimeMilis() {
        return systemTimeMilis;
    }

    public double getValue() {
        return mValue;
    }
}
