package com.example.memelord;

import android.app.Application;

import com.example.memelord.models.Comment;
import com.example.memelord.models.Like;
import com.example.memelord.models.Post;
import com.example.memelord.models.Profile;
import com.example.memelord.models.User;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.facebook.ParseFacebookUtils;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(Comment.class);
        ParseObject.registerSubclass(Profile.class);
        ParseObject.registerSubclass(Like.class);
        ParseUser.registerSubclass(User.class);

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("fbumemelord") // should correspond to APP_ID env variable
                .clientKey("8e08b8cd42118531b2a5bd6abb61faa079b56c09602fdfb1489a4ddc4e9c7dff")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("http://fbumemelord.herokuapp.com/parse").build());
        ParseFacebookUtils.initialize(getApplicationContext());
    }
}