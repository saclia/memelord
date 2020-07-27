package com.example.memelord.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ComposeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ComposeFragment extends BaseFragment {
    public static final String TAG = ComposeFragment.class.getSimpleName();
    public static final String ARG_COMPOSE_TYPE = "composeType";
    public static final String ARG_IMAGE_PATH = "imagePath";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentComposeBinding mBinding;

    private Util.FragmentLoader mActivity;

    private String mImagePath;
    private String mComposeType;

    private ImageView mIVMeme;
    private EditText mETPostDesc;
    private EditText mETTitle;
    private Button mBTNPublish;
    private Button mBTNUploadImage;

    private boolean mPublishDebounce = false;

    public ComposeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ComposeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ComposeFragment newInstance(String param1, String param2) {
        ComposeFragment fragment = new ComposeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mComposeType = getArguments().getString(ARG_COMPOSE_TYPE);
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

        mIVMeme.setVisibility(View.GONE);
        if(mImagePath != null) {
            Uri filepath = Uri.parse(mImagePath);
//            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filepath);
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
        return view;
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
                mPublishDebounce = false;
                mBTNPublish.setVisibility(View.VISIBLE);
            }
        });
        mActivity.loadFragment(new FeedFragment(), null);
    }

    private Bitmap uriToBitmap(Uri fileUri) {
        try {
            ParcelFileDescriptor parcelFileDescriptor =
                    getActivity().getContentResolver().openFileDescriptor(fileUri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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