package com.android.dishpatch.dishpatch.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Lenovo on 4/23/2016.
 */
public class Order implements Parcelable {

    private Menu mMenu;
    private Date mDate;
    private Customer mCustomer;
    private Restaurant mRestaurant;
    private Track mTrack;
    private Dispatch mDispatch;
    private String mLocation;
    private String mRemarks;
    private int mQuantity;
    private static int id=0;
    public Order(){

        mRestaurant = new Restaurant();
        mMenu = new Menu("Nasi Goreng",12,++id);
        mDate = new Date();
        mCustomer = new Customer();
        mTrack = new Track("preparing");
        mLocation = "No.2 Jln PI 15/6 Taman Pulai Indah";

    }

    public Order(Menu menu, Date date, Customer customer)
    {

    }

    public Order(Menu menu,int quantity)
    {
        mMenu = menu;
        mQuantity = quantity;
    }


    protected Order(Parcel in) {
        mMenu = in.readParcelable(Menu.class.getClassLoader());
        mRestaurant = in.readParcelable(Restaurant.class.getClassLoader());
        mDispatch = in.readParcelable(Dispatch.class.getClassLoader());
        mLocation = in.readString();
        mRemarks = in.readString();
        mQuantity = in.readInt();
    }

    public static final Creator<Order> CREATOR = new Creator<Order>() {
        @Override
        public Order createFromParcel(Parcel in) {
            return new Order(in);
        }

        @Override
        public Order[] newArray(int size) {
            return new Order[size];
        }
    };

    public Menu getMenu() {
        return mMenu;
    }

    public void setMenu(Menu menu) {
        mMenu = menu;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Customer getCustomer() {
        return mCustomer;
    }

    public void setCustomer(Customer customer) {
        mCustomer = customer;
    }



    public Restaurant getRestaurant() {
        return mRestaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        mRestaurant = restaurant;
    }

    public Track getTrack() {
        return mTrack;
    }

    public void setTrack(Track track) {
        mTrack = track;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getRemarks() {
        return mRemarks;
    }

    public void setRemarks(String remarks) {
        mRemarks = remarks;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mMenu, flags);
        dest.writeParcelable(mRestaurant, flags);
        dest.writeParcelable(mDispatch, flags);
        dest.writeString(mLocation);
        dest.writeString(mRemarks);
        dest.writeInt(mQuantity);
    }
}
