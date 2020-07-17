package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String TAG = Post.class.getSimpleName();
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_LIKES_COUNT = "likesCount";
    public static final String KEY_BODY = "body";
    public static final String KEY_TITLE = "title";

    public void setUser(ParseUser user) { put(KEY_USER, user); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setImage(ParseFile image) { put(KEY_IMAGE, image); }
    public ParseFile getImage() { return getParseFile(KEY_IMAGE); }

    public ParseRelation getLikes() { return getRelation(KEY_LIKES); }

    public void setBody(String body) { put(KEY_BODY, body); }
    public String getBody() { return getString(KEY_BODY); }

    public void setTitle(String title) { put(KEY_TITLE, title); }
    public String getTitle() { return getString(KEY_TITLE); }

    public void setLikesCount(int count) { put(KEY_LIKES_COUNT, count); }
    public int getLikesCount() { return getInt(KEY_LIKES_COUNT); }
}
