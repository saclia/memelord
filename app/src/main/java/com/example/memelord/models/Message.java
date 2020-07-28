package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel
@ParseClassName("Message")
public class Message extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_BODY = "body";

    public void setUser(ParseUser user) { put(KEY_USER, user); }
    public ParseUser getUser() { return getParseUser(KEY_USER); }

    public void setBody(String body) { put(KEY_BODY, body); }
    public String getBody() { return getString(KEY_BODY); }
}
