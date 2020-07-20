package com.example.memelord.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.databinding.FragmentPostViewBinding;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Comment;
import com.example.memelord.models.Post;
import com.example.memelord.models.User;
import com.parse.ParseFile;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostViewFragment extends Fragment {

    public static final String ARG_POST = "PARSE_POST";

    private FragmentPostViewBinding mBinding;

    private Post mPost;

    private RecyclerView mRVComments;
    private TextView mTVDate;
    private TextView mTVViews;
    private TextView mTVLikesCount;
    private TextView mTVPostTitle;

    private ImageView mIVLikesBTN;
    private ImageView mIVAvatar;
    private ImageView mIVMeme;

    private EditText mETDesc; // Still a display view
    private EditText mETComment;


    public PostViewFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static PostViewFragment newInstance(Post post) {
        PostViewFragment fragment = new PostViewFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_POST, post);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPost = getArguments().getParcelable(ARG_POST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentPostViewBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        mRVComments = mBinding.rvComments;
        mTVDate = mBinding.tvDate;
        mTVViews = mBinding.tvViews;
        mTVLikesCount = mBinding.tvLikeCount;
        mTVPostTitle = mBinding.tvTitle;
        mIVLikesBTN = mBinding.ivLikeBTN;
        mIVAvatar = mBinding.ivAvatar;
        mETComment = mBinding.etComment;
        mETDesc = mBinding.etPostDescription;
        mIVMeme = mBinding.ivMeme;

        mETDesc.setText(mPost.getBody());
        mTVPostTitle.setText(mPost.getTitle());
        mTVLikesCount.setText(Util.formatNumber(mPost.getLikesCount()));
        mTVDate.setText(Util.getRelativeTimeAgo(mPost.getCreatedAt()));
        ParseFile file = mPost.getImage();
        if(file != null)
            Glide.with(getContext()).load(file.getUrl()).into(mIVMeme);
        ParseFile avatar = mPost.getUser().getParseFile(User.KEY_AVATAR);
        if(avatar != null)
            Glide.with(getContext()).load(file.getUrl()).into(mIVAvatar);
        mETComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    // TODO Rethink model for comments
//                    Comment comment = new Comment();
                }
                return false;
            }
        });
        return view;
    }
}