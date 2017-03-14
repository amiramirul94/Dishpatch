package com.android.dishpatch.dishpatch.Model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lenovo on 3/18/2016.
 */
public class Dispatch implements Parcelable{
    private String mDispatcherName;
    private int mDispatcherId;
    private double mDistance;
    private long mPhoneNumber;
    private int mAverageResponseTime;
    //private int


    public Dispatch(int id)
    {
        mDispatcherId = id;
    }

    public Dispatch()
    {
        mDispatcherName = "Muhammad Zafran";
        mDispatcherId=123;
        mPhoneNumber = 0137446251;
        mAverageResponseTime = 20;
    }


    protected Dispatch(Parcel in) {
        mDispatcherName = in.readString();
        mDispatcherId = in.readInt();
        mDistance = in.readDouble();
        mPhoneNumber = in.readLong();
        mAverageResponseTime = in.readInt();
    }

    public static final Creator<Dispatch> CREATOR = new Creator<Dispatch>() {
        @Override
        public Dispatch createFromParcel(Parcel in) {
            return new Dispatch(in);
        }

        @Override
        public Dispatch[] newArray(int size) {
            return new Dispatch[size];
        }
    };

    public String getDispatcherName() {
        return mDispatcherName;
    }

    public void setDispatcherName(String dispatcherName) {
        mDispatcherName = dispatcherName;
    }

    public int getDispatcherId() {
        return mDispatcherId;
    }

    public void setDispatcherId(int dispatcherId) {
        mDispatcherId = dispatcherId;
    }

    public long getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public int getAverageResponseTime() {
        return mAverageResponseTime;
    }

    public void setAverageResponseTime(int averageResponseTime) {
        mAverageResponseTime = averageResponseTime;
    }

    public double getDistance() {
        return mDistance;
    }

    public void setDistance(double distance) {
        mDistance = distance;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mDispatcherName);
        dest.writeInt(mDispatcherId);
        dest.writeDouble(mDistance);
        dest.writeLong(mPhoneNumber);
        dest.writeInt(mAverageResponseTime);
    }
}
