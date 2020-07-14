package com.example.memelord;

import android.app.Application;

import com.example.memelord.models.Comment;
import com.example.memelord.models.Post;
import com.example.memelord.models.User;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApplication extends Application {
    public static final String APP_ID = BuildConfig.APPLICATION_ID;
    public static final String CLIENT_KEY = BuildConfig.CLIENT_KEY; // Use https so android does not complain
    public static final String SERVER_URL = BuildConfig.SERVER_URL;

    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseUser.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(APP_ID) // should correspond to APP_ID env variable
                .clientKey(CLIENT_KEY)  // set explicitly unless clientKey is explicitly configured on Parse server
                .server(SERVER_URL).build());
    }
}