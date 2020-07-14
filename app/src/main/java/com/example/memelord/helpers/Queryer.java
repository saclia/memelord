package com.example.memelord.helpers;

public class Queryer {
    public static final String TAG = Queryer.class.getSimpleName();

    private static Queryer mSingleton;

    private static final int LOAD_AMOUNT = 10;
    private int mCurrentPage = 0;

    public static Queryer getInstance() {
        if(mSingleton == null)
            mSingleton = new Queryer();
        return mSingleton;
    }
}
