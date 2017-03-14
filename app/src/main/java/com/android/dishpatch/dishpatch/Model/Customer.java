package com.android.dishpatch.dishpatch.Model;

/**
 * Created by Lenovo on 4/23/2016.
 */
public class Customer {
    private String mName;
    private String mId;

    Customer(){
        mName = "Muhammad Zafran";
    }

    Customer(String name, String id)
    {
        mName = name;
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
