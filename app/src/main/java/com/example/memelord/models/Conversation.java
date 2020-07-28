package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel
@ParseClassName("Conversation")
public class Conversation extends ParseObject {
    public static final String  TAG = Conversation.class.getSimpleName();
    public static final String KEY_USER1 = "user1";
    public static final String KEY_USER2 = "user2";
    public static final String KEY_MESSAGES = "messages";

    public ParseUser getUser1() { return getParseUser(KEY_USER1); }
    public ParseUser getUser2() { return getParseUser(KEY_USER2); }

    public void setUser1(ParseUser user) { put(KEY_USER1, user); }
    public void setUser2(ParseUser user) { put(KEY_USER2, user); }

    public ParseRelation getMessages() { return getRelation(KEY_MESSAGES); }

}
