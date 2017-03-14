package com.android.dishpatch.dishpatch.Model;

import java.util.Date;
import java.util.List;

/**
 * Created by Lenovo on 2/28/2016.
 */
public class Track {
    private String mStatus;
    private Date mDate;

    public Track(String status)
    {
        mStatus= status;
        mDate= new Date();
    }

    public Track(String status,Restaurant restaurant,List<Menu> menus)
    {
        mStatus = status;

    }

    public String getStatus() {
        return mStatus;
    }

    public void setStatus(String status) {
        mStatus = status;
    }



    public void setDate(Date date) {
        mDate = date;
    }




    public Date getDate() {
        return mDate;
    }
}
