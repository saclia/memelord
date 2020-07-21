package com.example.memelord.helpers;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.memelord.models.Comment;
import com.example.memelord.models.Like;
import com.example.memelord.models.Post;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

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

    public void queryPosts(ParseQueryerCallback callback, @Nullable ParseUser user) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_LIKES);
        query.setLimit(LOAD_AMOUNT);
        query.setSkip(LOAD_AMOUNT * mCurrentPage);
        if(user != null)
            query.whereEqualTo(Post.KEY_USER, user);
        if(mDescendFlag)
            query.addDescendingOrder(Post.KEY_CREATED_AT);

        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to fetch posts", e);
                }
                callback.done(objects, null);
            }
        });
    }

    public void queryComments(ParseQueryerCallback callback, Post post) {
        ParseRelation relation = post.getComments();
        ParseQuery<Comment> query = relation.getQuery();
        query.include(Comment.KEY_USER);
        query.setLimit(LOAD_AMOUNT);
        query.setSkip(LOAD_AMOUNT * mCurrentPage);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to fetch comments from post!", e);
                    return;
                }
                callback.done(objects, null);
            }
        });
    }

}
