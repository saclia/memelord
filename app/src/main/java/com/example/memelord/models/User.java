package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel
@ParseClassName("_User")
public class User extends ParseUser {
    public static final String TAG = User.class.getSimpleName();
    public static final String KEY_SCREEN_NAME = "screenName";
    public static final String KEY_AVATAR = "avatar";
    public static final String KEY_PROFILE = "profile";

    public void setScreenName(String name) {
        put(KEY_SCREEN_NAME, name);
    }
    public String getScreenName() { return getString(KEY_SCREEN_NAME); }

    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }

    public void setAvatar(ParseFile file) { put(KEY_AVATAR, file); }
    public ParseFile getAvatar() { return getParseFile(KEY_AVATAR); }

    public void setProfile(Profile profile) { put(KEY_PROFILE, profile); }
    public ParseObject getProfile() { return getParseObject(KEY_PROFILE); }
}
