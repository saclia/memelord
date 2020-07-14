package com.example.memelord.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

@ParseClassName("Post")
public class Post extends ParseObject {
    public static final String TAG = Post.class.getSimpleName();
}
