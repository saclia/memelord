package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Like")
public class Like extends ParseObject {
    public static final String TAG = Like.class.getSimpleName();
    public static final String KEY_USER_ID = "userId";
    public static final String KEY_POST_ID = "postId";
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";

    public void setUserId(String id) { put(KEY_USER_ID, id); }
    public String getUserId() { return getString(KEY_USER_ID); }

    public void setPostId(String id) { put(KEY_POST_ID, id); }
    public String getPostId() { return getString(KEY_POST_ID); }

    public void setUser(ParseUser user) { put(KEY_USER, user); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setPost(Post post) { put(KEY_POST, post); }
    public ParseObject getPost() { return getParseObject(KEY_POST); }
}
