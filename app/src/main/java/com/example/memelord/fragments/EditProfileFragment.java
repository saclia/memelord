package com.example.memelord.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.memelord.R;
import com.example.memelord.databinding.FragmentEditProfileBinding;
import com.example.memelord.models.Profile;
import com.example.memelord.models.User;
import com.parse.ParseUser;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EditProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {
    public static final String TAG = EditProfileFragment.class.getSimpleName();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentEditProfileBinding mBinding;

    private User mUser;
    private Profile mProfile;

    private ImageView mIVBackground;
    private ImageView mIVAvatar;
    private Button mBTNBackground;
    private Button mBTNAvatar;
    private EditText mETUsername;

    public EditProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EditProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EditProfileFragment newInstance(String param1, String param2) {
        EditProfileFragment fragment = new EditProfileFragment();
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
        }
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

        return view;
    }

    private void bindContent() {

    }
}