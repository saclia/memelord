package com.example.memelord.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ahmedadeltito.photoeditorsdk.BrushDrawingView;
import com.ahmedadeltito.photoeditorsdk.PhotoEditorSDK;
import com.example.memelord.R;
import com.example.memelord.databinding.FragmentComposeBinding;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class ComposeFragment extends BaseFragment {
    public static final String TAG = ComposeFragment.class.getSimpleName();
    public static final String ARG_IMAGE_PATH = "imagePath";

    private FragmentComposeBinding mBinding;

    private Util.FragmentLoader mActivity;

    private String mImagePath;

    private ImageView mIVMeme;
    private EditText mETPostDesc;
    private EditText mETTitle;
    private Button mBTNPublish;
    private Button mBTNUploadImage;

    private boolean mPublishDebounce = false;

    public ComposeFragment() {
        // Required empty public constructor
    }

    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mImagePath = getArguments().getString(ARG_IMAGE_PATH);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentComposeBinding.inflate(inflater);
        View view = mBinding.getRoot();

        mActivity = (Util.FragmentLoader) getActivity();

        mIVMeme = mBinding.ivMeme;
        mBTNPublish = mBinding.btnPublish;
        mBTNUploadImage = mBinding.btnUploadImage;
        mETPostDesc = mBinding.etPostDesc;
        mETTitle = mBinding.etTitle;
        bindContent();
        return view;
    }

    @Override
    protected void bindContent() {
        mIVMeme.setVisibility(View.GONE);
        if(mImagePath != null) {
            Uri filepath = Uri.parse(mImagePath);
            Bitmap imageBitmap = uriToBitmap(filepath);
            mIVMeme.setImageBitmap(imageBitmap);
            mIVMeme.setVisibility(View.VISIBLE);
        }

        mBTNUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery();
            }
        });

        mBTNPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.showProgressBar();
                String body = mETPostDesc.getText().toString();
                String title = mETTitle.getText().toString();
                if(body == null || body.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a proper description", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(title == null || title.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a valid title.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!mPublishDebounce) {
                    mBTNPublish.setVisibility(View.INVISIBLE);
                    mPublishDebounce = true;
                    uploadPostToParse();
                }
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        MenuItem instantMessageItem = menu.findItem(R.id.action_direct_message);
        instantMessageItem.setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    private void uploadPostToParse() {
        if(mImagePath != null) {
            Bitmap imageBitmap = ((BitmapDrawable) mIVMeme.getDrawable()).getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            ParseFile file = new ParseFile(ParseUser.getCurrentUser().getUsername()+".png", image);
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "Failed to save file for post", e);
                        return;
                    }
                    publishPost(file);
                }
            });
        } else {
            publishPost(null);
        }
    }

    private void publishPost(ParseFile file) {
        Post newPost = new Post();
        if(file != null)
            newPost.setImage(file);
        newPost.setBody(mETPostDesc.getText().toString());
        newPost.setTitle(mETTitle.getText().toString());
        newPost.setLikesCount(0);
        newPost.setUser(ParseUser.getCurrentUser());
        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to upload composed post to Parse", e);
                }
                mActivity.hideProgressBar();
                mActivity.loadFragment(new FeedFragment(), null);
                mBTNPublish.setVisibility(View.VISIBLE);
            }
        });
        mPublishDebounce = false;
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_GALLERY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GALLERY && resultCode == getActivity().RESULT_OK) {
            if(data == null) {
                Log.e(TAG, "Failed to get an image from the gallery. Perhaps it's from a documents app - not the native gallery");
                return;
            }
            mImagePath = data.getData().getPath();
            Bitmap imageBitmap = uriToBitmap(data.getData());
            mIVMeme.setImageBitmap(imageBitmap);
            mIVMeme.setVisibility(View.VISIBLE);
        }
    }
}