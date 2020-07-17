package com.example.memelord.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.memelord.models.Post;
import com.parse.ParseFile;
import com.parse.ParseUser;

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
    public static final int REQUEST_CODE_GALLERY = 31;
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


    private String mImagePath;
    private String mComposeType;

    private ImageView mIVMeme;
    private EditText mETPostDesc;
    private Button mBTNPublish;
    private Button mBTNUploadImage;

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

        mIVMeme = mBinding.ivMeme;
        mBTNPublish = mBinding.btnPublish;
        mBTNUploadImage = mBinding.btnUploadImage;
        mETPostDesc = mBinding.etPostDesc;

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
                String body = mETPostDesc.getText().toString();
                if(body != null && !body.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a proper description", Toast.LENGTH_SHORT).show();
                    return;
                }
                uploadPostToParse();
            }
        });
        return view;
    }

    private void uploadPostToParse() {
        ParseFile file = null;
        if(mImagePath != null) {
            Uri filepath = Uri.parse(mImagePath);
            Bitmap imageBitmap = uriToBitmap(filepath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] image = stream.toByteArray();
            file = new ParseFile(mImagePath,image);
            file.saveInBackground();
        }
        Post newPost = new Post();
        if(file != null)
            newPost.setImage(file);
        newPost.setBody(mETPostDesc.getText().toString());
        newPost.setLikesCount(0);
        newPost.setUser(ParseUser.getCurrentUser());
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
            Bitmap imageBitmap = uriToBitmap(data.getData());
            mIVMeme.setImageBitmap(imageBitmap);
            mIVMeme.setVisibility(View.VISIBLE);
        }
    }
}