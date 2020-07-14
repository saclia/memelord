package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseUser;

@ParseClassName("User")
public class User extends ParseUser {
    public static final String TAG = User.class.getSimpleName();
}
