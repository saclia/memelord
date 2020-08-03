package com.example.memelord.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memelord.R;

public abstract class BaseFragment extends Fragment {
    public static final String TAG = BaseFragment.class.getSimpleName();
    public static final int REQUEST_CODE_GALLERY = 31;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_base, container, false);
    }

    protected void bindContent() {

    }
}