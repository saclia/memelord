package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

@ParseClassName("Profile")
public class Profile extends ParseObject {
    public static final String TAG = Profile.class.getSimpleName();
    public static final String KEY_USER = "user";
    public static final String KEY_FOLLOWERS_COUNT = "followersCount";
    public static final String KEY_FOLLOWING_COUNT = "followingCOunt";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_BG = "background";

    public void setUser(ParseUser user) { put(KEY_USER, user); }
    public User getUser() { return (User) getParseUser(KEY_USER); }

    public void setFollowersCount(int count) { put(KEY_FOLLOWERS_COUNT, count); }
    public int getFollowersCount() { return getInt(KEY_FOLLOWERS_COUNT);}

    public void setFollowingCount(int count) { put(KEY_FOLLOWING_COUNT, count); }
    public int getFollowingCount() { return getInt(KEY_FOLLOWING_COUNT); }

    public ParseRelation getFollowingRelation() { return getRelation(KEY_FOLLOWING); }
    public ParseRelation getFollowersRelation() { return getRelation(KEY_FOLLOWERS); }

    public void setBackground(ParseFile image) { put(KEY_BG, image); }
    public ParseFile getBackground() { return getParseFile(KEY_BG); }
}
