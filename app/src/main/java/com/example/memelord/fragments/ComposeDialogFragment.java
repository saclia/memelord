package com.example.memelord.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.memelord.R;
import com.example.memelord.activities.MainActivity;
import com.example.memelord.activities.PhotoEditorActivity;
import com.example.memelord.databinding.FragmentComposeDialogBinding;
import com.example.memelord.helpers.Util;

public class ComposeDialogFragment extends DialogFragment {
    public static final String TAG = ComposeDialogFragment.class.getSimpleName();

    private FragmentComposeDialogBinding mBinding;
    private Context mContext;
    private Util.FragmentLoader mActivity;

    private ImageView mCloseDialogBtn;
    private TextView mTVMemeBtn;
    private TextView mTVCopyPastaBtn;
    private TextView mTVComposeMemeBtn;

    public ComposeDialogFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeDialogFragment newInstance(String param1, String param2) {
        ComposeDialogFragment fragment = new ComposeDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentComposeDialogBinding.inflate(inflater);
        mCloseDialogBtn = mBinding.ibCloseDialog;
        mTVCopyPastaBtn = mBinding.tvCopyPasta;
        mTVMemeBtn = mBinding.tvMemeBtn;
        mTVComposeMemeBtn = mBinding.tvCreateMeme;

        View view = mBinding.getRoot();
        mActivity = (Util.FragmentLoader) getActivity();

        mCloseDialogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mTVMemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
                mActivity.loadFragment(new ComposeFragment(), null);
            }
        });

        mTVComposeMemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PhotoEditorActivity.class);
                startActivityForResult(intent, MainActivity.REQUEST_CODE_PHOTO_EDIT);
            }
        });

        mTVCopyPastaBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                dismiss();
                mActivity.loadFragment(new ComposeFragment(), null);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}