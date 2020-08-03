package com.example.memelord.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.databinding.FragmentEditProfileBinding;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Profile;
import com.example.memelord.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class EditProfileFragment extends Fragment {
    public static final String TAG = EditProfileFragment.class.getSimpleName();

    private static final int REQUEST_CODE_GALLERY_BG = 38;
    private static final int REQUEST_CODE_GALLERY_AVATAR = 39;

    private FragmentEditProfileBinding mBinding;

    private User mUser;
    private Profile mProfile;
    private Util.FragmentLoader mActivity;

    private ImageView mIVBackground;
    private ImageView mIVAvatar;
    private Button mBTNBackground;
    private Button mBTNAvatar;
    private Button mBTNSave;
    private EditText mETUsername;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        mBinding = FragmentEditProfileBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();
        mBTNAvatar = mBinding.btnChangeAvatar;
        mBTNBackground = mBinding.btnChangeBG;
        mIVAvatar = mBinding.ivAvatar;
        mIVBackground = mBinding.ivProfileBackground2;
        mETUsername = mBinding.etUsername;
        mBTNSave = mBinding.btnSave;
        mUser = (User) ParseUser.getCurrentUser();
        mProfile = (Profile) mUser.getProfile();
        mActivity = (Util.FragmentLoader) getActivity();

        bindContent();
        return view;
    }

    private void bindContent() {
        String name = mUser.getScreenName();
        if(name == null || name.isEmpty())
            name = mUser.getUsername();
        ParseFile background = mProfile.getBackground();
        ParseFile avatar = mUser.getAvatar();
        mETUsername.setText(name);
        mETUsername.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                   String name = mETUsername.getText().toString();
                   if(name != null && !name.isEmpty()) {
                       ParseUser.getCurrentUser().put(User.KEY_SCREEN_NAME, name);
                   }
                }
                return false;
            }
        });
        if(background != null)
            Glide.with(getContext()).load(background.getUrl()).into(mIVBackground);
        if(avatar != null)
            Glide.with(getContext()).load(avatar.getUrl()).into(mIVAvatar);
        mBTNBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(REQUEST_CODE_GALLERY_BG);
            }
        });
        mBTNAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openGallery(REQUEST_CODE_GALLERY_AVATAR);
            }
        });
        mBTNSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProfileAndUser();
            }
        });
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

    private void saveProfileAndUser() {
        Bitmap avatarBitmap = null;
        Drawable avatarDrawable = mIVAvatar.getDrawable();
        if(avatarDrawable != null) {
            avatarBitmap = ((BitmapDrawable) avatarDrawable).getBitmap();
        }
        Bitmap bgBitmap = null;
        Drawable bgDrawable = mIVBackground.getDrawable();
        if(bgDrawable != null) {
            bgBitmap = ((BitmapDrawable) bgDrawable).getBitmap();
        }
        ParseFile avatarFile = null;
        ParseFile bgFile = null;
        if(avatarBitmap != null)
            avatarFile = bitmapToParseFile(avatarBitmap, "avatar");
        if(bgBitmap != null)
            bgFile = bitmapToParseFile(bgBitmap, "background");
        String name = mETUsername.getText().toString();
        if(bgFile != null) {
            mProfile.setBackground(bgFile);
            mProfile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "Couldn't save profile background", e);
                        toastWarning("Failed to save profile background!");
                    }
                }
            });
        }
        if(avatarFile != null) {
            mUser.setAvatar(avatarFile);
            mUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "Couldn't save user avatar", e);
                        toastWarning("Failed to save user's avatar!");
                    }
                }
            });
        }
        if(name != null && !name.isEmpty()) {
            mUser.setScreenName(name);
            mUser.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "Couldn't save screename", e);
                        toastWarning("Failed to save new username");
                    }
                }
            });
        }
        mActivity.loadFragment(new FeedFragment(), null);
    }

    private void toastWarning(String body) {
        Toast.makeText(getContext(), body, Toast.LENGTH_SHORT).show();
    }

    private ParseFile bitmapToParseFile(Bitmap imageBitmap, String fileName) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] image = stream.toByteArray();
        ParseFile file = new ParseFile(ParseUser.getCurrentUser().getUsername()+fileName+".png", image);
        try {
            file.save();
        } catch (ParseException e) {
            Log.e(TAG, "Failed to save ParseFile: " + fileName, e);
        }
        return file;
    }

    private void openGallery(final int REQUEST_CODE) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
    }

    private void setAvatarPreview(Bitmap imageBitmap) {
        mIVAvatar.setImageBitmap(imageBitmap);
    }

    private void setBackgroundBitmap(Bitmap imageBitmap) {
        mIVBackground.setImageBitmap(imageBitmap);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_GALLERY_BG || requestCode == REQUEST_CODE_GALLERY_AVATAR && resultCode == getActivity().RESULT_OK) {
            if(data == null) {
                Log.e(TAG, "Failed to get an image from the gallery. Perhaps it's from a documents app - not the native gallery");
                return;
            }
            Bitmap imageBitmap = uriToBitmap(data.getData());
            if(requestCode == REQUEST_CODE_GALLERY_AVATAR) {
                setAvatarPreview(imageBitmap);
            } else if(requestCode == REQUEST_CODE_GALLERY_BG) {
                setBackgroundBitmap(imageBitmap);
            }
        }
    }
}