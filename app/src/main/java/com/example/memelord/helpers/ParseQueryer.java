package com.example.memelord.helpers;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.memelord.models.Comment;
import com.example.memelord.models.Conversation;
import com.example.memelord.models.Like;
import com.example.memelord.models.Message;
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

    private int mLoadAmount = 10;
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

    public void restoreDefaults() {
        mLoadAmount = 10;
        mCurrentPage = 0;
        mDescendFlag = true;
    }

    public void queryPosts(ParseQueryerCallback callback, @Nullable ParseUser user, @Nullable String title) {
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        query.include(Post.KEY_USER);
        query.include(Post.KEY_LIKES);
        query.setLimit(mLoadAmount);
        query.setSkip(mLoadAmount * mCurrentPage);
        if(user != null)
            query.whereEqualTo(Post.KEY_USER, user);
        if(title != null) {
            query.whereContains(Post.KEY_TITLE, title);
        }
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
        query.setLimit(mLoadAmount);
        query.setSkip(mLoadAmount * mCurrentPage);
        if(mDescendFlag)
            query.addDescendingOrder(Comment.KEY_CREATED_AT);
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

    public void queryConversations(ParseQueryerCallback callback) {
        ParseQuery<Conversation> query = ParseQuery.getQuery(Conversation.class);
        query.include(Conversation.KEY_USER1);
        query.include(Conversation.KEY_USER2);
        query.setLimit(mLoadAmount);
        query.setSkip(mLoadAmount * mCurrentPage);
        if(mDescendFlag)
            query.addDescendingOrder(Conversation.KEY_UPDATED_AT);
        query.whereEqualTo(Conversation.KEY_USER1, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to fetch conversations", e);
                }
                callback.done(objects, null);
            }
        });
        query.clear(Conversation.KEY_USER1);
        query.whereEqualTo(Conversation.KEY_USER2, ParseUser.getCurrentUser());
        query.findInBackground(new FindCallback<Conversation>() {
            @Override
            public void done(List<Conversation> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to fetch conversations", e);
                }
                callback.done(objects, null);
            }
        });
    }

    public void queryMessages(ParseQueryerCallback callback, Conversation convo) {
        ParseRelation relation = convo.getMessages();
        ParseQuery<Message> query = relation.getQuery();
        query.include(Message.KEY_USER);
        query.setLimit(mLoadAmount);
        query.setSkip(mLoadAmount * mCurrentPage);
        if(mDescendFlag)
            query.addDescendingOrder(Comment.KEY_CREATED_AT);
        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to fetch messages in conversation", e);
                }
                callback.done(objects, null);
            }
        });
    }

}
