package com.example.memelord.models;

import android.util.Log;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;

import java.util.Date;

@Parcel
@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String TAG = Post.class.getSimpleName();
    public static final String KEY_USER = "user";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_LIKES_COUNT = "likesCount";
    public static final String KEY_BODY = "body";
    public static final String KEY_TITLE = "title";
    private static final String KEY_COMMENTS = "comments";
    public static final String KEY_BTS = "basicTrendingScore";

    public void setUser(ParseUser user) { put(KEY_USER, user); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setImage(ParseFile image) { put(KEY_IMAGE, image); }
    public ParseFile getImage() { return getParseFile(KEY_IMAGE); }

    public ParseRelation getLikes() { return getRelation(KEY_LIKES); }
    public ParseRelation getComments() { return getRelation(KEY_COMMENTS); }

    public void setBody(String body) { put(KEY_BODY, body); }
    public String getBody() { return getString(KEY_BODY); }

    public void setTitle(String title) { put(KEY_TITLE, title); }
    public String getTitle() { return getString(KEY_TITLE); }

    public void setLikesCount(int count) { put(KEY_LIKES_COUNT, count); }
    public int getLikesCount() { return getInt(KEY_LIKES_COUNT); }

    public void setBasicTrendingScore(double score) { put(KEY_BTS, score); }
    public int getBasicTrendingScore() { return getInt(KEY_BTS); }

    public static void onSave(Post post) throws ParseException {
        Log.i(TAG, "Attempting to save post's trending score [1]");
        int likesCount = post.fetchIfNeeded().getInt(KEY_LIKES_COUNT);
        Date postCreatedAt = post.getCreatedAt();
        double timeInHoursSinceCreation = (double) (postCreatedAt.getTime() / (1000 * 3600));
        double trendingScore = (likesCount/Math.pow(timeInHoursSinceCreation, 1.5));
        post.setBasicTrendingScore(trendingScore);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "Attempting to save post's trending score");
                if(e != null) {
                    Log.e(TAG, "Failed to save post's basic trending score", e);
                }
            }
        });
    }
}
