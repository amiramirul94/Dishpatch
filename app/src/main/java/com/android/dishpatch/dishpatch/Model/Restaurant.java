package com.android.dishpatch.dishpatch.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Restaurant implements Parcelable{

    private int mId;
    private String mName;
    private String mCity;
    private String mAddress;
    private int mDistance;
    private float mRating;
    private Uri mPhotoUri;
    private double mLatitude;
    private double mLongitude;
    private List<Menu> mMenu = new ArrayList<>();

    public Restaurant()
    {
        mName = "Mak Ngah";
        for(int i=0;i<10;i++)
        {
            Menu menu = new Menu();
            menu.setFood("Food #"+i);
            menu.setPrice(i);
            mMenu.add(menu);
        }
    }

    public Restaurant(int id, String name, Uri photoUri, double latitude, double longitude) {
        mId = id;
        mName = name;
        mPhotoUri = photoUri;
        mLatitude = latitude;
        mLongitude = longitude;
    }

    protected Restaurant(Parcel in) {
        mId = in.readInt();
        mName = in.readString();
        mCity = in.readString();
        mAddress = in.readString();
        mDistance = in.readInt();
        mRating = in.readFloat();
        mPhotoUri = in.readParcelable(Uri.class.getClassLoader());
        mLatitude = in.readDouble();
        mLongitude = in.readDouble();
        mMenu = in.createTypedArrayList(Menu.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mName);
        dest.writeString(mCity);
        dest.writeString(mAddress);
        dest.writeInt(mDistance);
        dest.writeFloat(mRating);
        dest.writeParcelable(mPhotoUri, flags);
        dest.writeDouble(mLatitude);
        dest.writeDouble(mLongitude);
        dest.writeTypedList(mMenu);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public List<Menu> getMenu() {
        return mMenu;
    }

    public void setMenu(List<Menu> menu) {
        mMenu = menu;
    }

    public int getDistance() {
        return mDistance;
    }

    public void setDistance(int distance) {
        mDistance = distance;
    }

    public float getRating() {
        return mRating;
    }

    public void setRating(float rating) {
        mRating = rating;
    }

    public Uri getPhotoUri() {
        return mPhotoUri;
    }

    public void setPhotoUri(Uri photoUri) {
        mPhotoUri = photoUri;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public void setLatitude(double latitude) {
        mLatitude = latitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public void setLongitude(double longitude) {
        mLongitude = longitude;
    }
}
