package com.android.dishpatch.dishpatch.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Menu implements Parcelable {

    private int menuId;
    private String mFood;
    private double mPrice;
    private Boolean mAvailable;
    private String mPictureUrl;

    public Menu()
    {

    }

    public Menu(String food, double price,int id)
    {
        menuId = id;
        mFood = food;
        mPrice = price;
        mAvailable = true;
    }

    public Menu(int menuId, String food, double price, Boolean available, String pictureUrl) {
        this.menuId = menuId;
        mFood = food;
        mPrice = price;
        mAvailable = available;
        mPictureUrl = pictureUrl;
    }


    protected Menu(Parcel in) {
        menuId = in.readInt();
        mFood = in.readString();
        mPrice = in.readDouble();
        mPictureUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(menuId);
        dest.writeString(mFood);
        dest.writeDouble(mPrice);
        dest.writeString(mPictureUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Menu> CREATOR = new Creator<Menu>() {
        @Override
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        @Override
        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }

    public String getFood() {
        return mFood;
    }

    public void setFood(String food) {
        mFood = food;
    }

    public double getPrice() {
        return mPrice;
    }

    public void setPrice(float price) {
        mPrice = price;
    }

    public Boolean getAvailable() {
        return mAvailable;
    }

    public void setAvailable(Boolean available) {
        mAvailable = available;
    }

    public String getPictureUrl() {
        return mPictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        mPictureUrl = pictureUrl;
    }
}
