package com.example.memelord.helpers;

import com.parse.ParseObject;

import java.util.List;

// Top-level singleton to query for various datasets inside our Parse Database
public class ParseQueryer {
    public static final String TAG = ParseQueryer.class.getSimpleName();

    private static ParseQueryer mSingleton;

    public interface ParseQueryerCallback {
        void done(List data, ParseObject o);
    }

    private static final int LOAD_AMOUNT = 10;
    private int mCurrentPage = 0;

    private boolean mDescendFlag = true;

    public static ParseQueryer getInstance() {
        if(mSingleton == null)
            mSingleton = new ParseQueryer();
        return mSingleton;
    }

    public void setPage(int page) {
        mCurrentPage = page;
    }

    public void inDescendingOrder(boolean toDescend) {
        mDescendFlag = toDescend;
    }
}
