package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String TAG = Comment.class.getSimpleName();
    public static final String KEY_BODY = "body";
    public static final String KEY_USER = "user";

    public void setUser(ParseUser user) { put(KEY_USER, user); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setBody(String body) { put(KEY_BODY, body); }
    public String getBody() { return getString(KEY_BODY); }
}
