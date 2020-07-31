package com.example.memelord.helpers;

public class TrendingAlgorithm {
    public static final String TAG = TrendingAlgorithm.class.getSimpleName();

    private TrendingAlgorithm mSingleton;
    private ParseQueryer mQueryer;

    private TrendingAlgorithm() {
        mQueryer = new ParseQueryer();
    }

    public TrendingAlgorithm getInstance() {
        if(mSingleton == null)
            mSingleton = new TrendingAlgorithm();
        return mSingleton;
    }


    // Basic acceleration algorithm
    // (votes - 1) / (time in hours)^1.5
    public void getSimpleTrendingPosts() {

    }

    public void getComplexTrendingPosts() {

    }
}

