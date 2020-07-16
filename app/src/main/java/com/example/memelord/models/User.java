package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String TAG = User.class.getSimpleName();
    public static final String KEY_SCREEN_NAME = "screenName";

    public void setScreenName(String name) {
        put(KEY_SCREEN_NAME, name);
    }

    public String getScreenName() { return getString(KEY_SCREEN_NAME); }

    public static User getCurrentUser() {
        return (User) ParseUser.getCurrentUser();
    }
}
