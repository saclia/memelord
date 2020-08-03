package com.example.memelord.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.activities.ConversationActivity;
import com.example.memelord.activities.LoginActivity;
import com.example.memelord.adapters.PostsAdapter;
import com.example.memelord.databinding.FragmentProfileBinding;
import com.example.memelord.helpers.EndlessRecyclerViewScrollListener;
import com.example.memelord.helpers.ParseQueryer;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Comment;
import com.example.memelord.models.Conversation;
import com.example.memelord.models.Post;
import com.example.memelord.models.Profile;
import com.example.memelord.models.User;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

// TODO [Possibly]
    // Make a FrameLayout under the ProfileView
    // Make a new FeedFragment for the ProfileView and extend it from FeedFragment or use FeedFragment and rebind
public class ProfileFragment extends BaseFragment {
    public static final String TAG = ProfileFragment.class.getSimpleName();
    public static final String ARG_USER = "ARG_USER";

    private FragmentProfileBinding mBinding;

    private Util.FragmentLoader mActivity;
    private User mUser;
    private Profile mProfile;
    private ParseQueryer mQueryer;
    private List<Post> mPosts;

    private RecyclerView mRVPosts;
    private PostsAdapter mPostsAdapter;
    private EndlessRecyclerViewScrollListener mEndlessRVSListener;

    private ImageButton mIBLogOut;
    private ImageView mIVDM;
    private ImageView mIVAvatar;
    private ImageView mIVBG;
    private TextView mTVFollowingCount;
    private TextView mTVFollowerCount;
    private TextView mTVUsername;
    private Button mBTNEditProfile;
    private Button mBTNFollow;

    private User mCurrentUser;
    private Profile mCurrentUserProfile;
    private ParseRelation mProfileFollowers;
    private ParseRelation mUserFollowing;

    private int mFollowingCount;
    private int mFollowersCount;
    private boolean mFollowingProfile;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentProfileBinding.inflate(inflater);
        View view = mBinding.getRoot();

        mIBLogOut = mBinding.ibLogOut;
        mIVAvatar = mBinding.ivAvatar;
        mIVBG = mBinding.ivProfileBackground;
        mTVFollowerCount = mBinding.tvFollowersCount;
        mTVFollowingCount = mBinding.tvFollowingCount;
        mTVUsername = mBinding.etUsername;
        mRVPosts = mBinding.rvPosts;
        mBTNEditProfile = mBinding.btnEditProfile;
        mBTNFollow = mBinding.btnFollow;
        mIVDM = mBinding.ivDM;

        mActivity = (Util.FragmentLoader) getActivity();
        mCurrentUser = (User) ParseUser.getCurrentUser();
        if(mUser == null || mUser.getObjectId().equals(mCurrentUser.getObjectId())) {
            mUser = mCurrentUser;
            mBTNEditProfile.setVisibility(View.VISIBLE);
        } else {
            mBTNFollow.setVisibility(View.VISIBLE);
        }
        mProfile = (Profile) mUser.getParseObject(User.KEY_PROFILE);
        mProfileFollowers = mProfile.getFollowersRelation();
        mCurrentUserProfile = (Profile) mCurrentUser.getProfile();
        mUserFollowing = mCurrentUserProfile.getFollowingRelation();

        try {
            mFollowersCount = mProfile.fetchIfNeeded().getInt(Profile.KEY_FOLLOWERS_COUNT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            mFollowingCount = mProfile.fetchIfNeeded().getInt(Profile.KEY_FOLLOWING_COUNT);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        checkIfFollowing();

        mPosts = new ArrayList<Post>();

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mPostsAdapter = new PostsAdapter(getActivity(), getContext(), mPosts);
        mRVPosts.setLayoutManager(llm);
        mRVPosts.setAdapter(mPostsAdapter);
        mEndlessRVSListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(page);
            }
        };
        mRVPosts.addOnScrollListener(mEndlessRVSListener);

        mQueryer = ParseQueryer.getInstance();

        bindContent();
        return view;
    }

    @Override
    protected void bindContent() {
        String username = mUser.getScreenName();
        if(username == null || username.isEmpty())
            username = mUser.getUsername();
        ParseFile userIcon = mUser.getParseFile(User.KEY_AVATAR);
        ParseFile profileBG = mProfile.getBackground();
        if(userIcon != null)
            Glide.with(getContext()).load(userIcon.getUrl()).into(mIVAvatar);
        if(profileBG != null)
            Glide.with(getContext()).load(profileBG.getUrl()).into(mIVBG);
        mTVFollowingCount.setText(""+mFollowingCount);
        mTVFollowerCount.setText(""+mFollowersCount);
        mTVUsername.setText(username);

        if(mUser.equals(mCurrentUser)) {
            mIBLogOut.setVisibility(View.VISIBLE);
        } else {
            mIVDM.setVisibility(View.VISIBLE);
        }

        mIVDM.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.showProgressBar();
                Conversation convo = null;
                ParseQuery<Conversation> query = new ParseQuery<Conversation>(Conversation.class);
                query.whereEqualTo(Conversation.KEY_USER1, mCurrentUser);
                query.whereEqualTo(Conversation.KEY_USER2, mUser);
                try {
                    convo = (Conversation) query.getFirst();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                query.clear(Conversation.KEY_USER1);
                query.clear(Conversation.KEY_USER2);
                if(convo == null) {
                    query.whereEqualTo(Conversation.KEY_USER1, mUser);
                    query.whereEqualTo(Conversation.KEY_USER2, mCurrentUser);
                    try {
                        convo = (Conversation) query.getFirst();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                if(convo == null) {
                    convo = new Conversation();
                    convo.setUser1(mCurrentUser);
                    convo.setUser2(mUser);
                    try {
                        convo.save();
                    } catch (ParseException e) {
                        Log.e(TAG, "Unable to save/create new convo", e);
                    }
                }
                mActivity.hideProgressBar();
                Intent intent = new Intent(getActivity(), ConversationActivity.class);
                intent.putExtra(ConversationActivity.ARG_CONVO, convo);
                intent.putExtra(ConversationActivity.ARG_CONVO_USER, mUser);
                getActivity().startActivity(intent);
            }
        });

        mIBLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ParseUser.logOut();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        mBTNEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mActivity.loadFragment(new EditProfileFragment(), null);
            }
        });
        mBTNFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!ParseUser.getCurrentUser().equals(mUser)) {
                    Log.i(TAG, "Following?: " + mFollowingProfile);
                    if(mFollowingProfile == true) {
                        unfollowProfile();
                    } else {
                        followProfile();
                    }
                }
            }
        });
        queryPosts(0);
    }

    private void followProfile() {
        mActivity.showProgressBar();
        try {
            mCurrentUserProfile.fetchIfNeeded().put(Profile.KEY_FOLLOWING_COUNT, mCurrentUserProfile.getFollowingCount()  + 1);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
        mUserFollowing.add(mUser);
        mCurrentUserProfile.saveInBackground();
        mProfileFollowers.add(mCurrentUser);
        mProfile.setFollowersCount(mProfile.getFollowersCount() + 1);
        mFollowingProfile = true;
        mProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to save profile for current profile view", e);
                    return;
                }
                mActivity.hideProgressBar();
                mFollowingProfile = true;
            }
        });
        mTVFollowerCount.setText(""+(++mFollowersCount));
        mBTNFollow.setText("Unfollow");
    }

    private void checkIfFollowing() {
        ParseQuery query = mUserFollowing.getQuery();
        query.whereEqualTo(ParseUser.KEY_OBJECT_ID, mUser.getObjectId());
        try {
            ParseObject obj = query.getFirst();
            if(obj != null) {
                Log.i(TAG, "Settign to unfollow");
                mFollowingProfile = true;
                mBTNFollow.setText("Unfollow");
                return;
            }
            mFollowingProfile = false;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void unfollowProfile() {
        mActivity.showProgressBar();
        try {
            mCurrentUserProfile = (Profile) mCurrentUserProfile.fetchIfNeeded();
            mCurrentUserProfile.setFollowingCount(mCurrentUserProfile.getFollowingCount() - 1);
        } catch (ParseException e) {
            Log.e(TAG, "Failed to set followers count!", e);
            return;
        }
        mUserFollowing.remove(mUser);
        mCurrentUserProfile.saveInBackground();

        mProfileFollowers.remove(mCurrentUser);
        mProfile.setFollowersCount(mProfile.getFollowersCount() - 1);
        mFollowingProfile = false;
        mProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mActivity.hideProgressBar();
                mFollowingProfile = false;
            }
        });
        mTVFollowerCount.setText(""+(--mFollowersCount));
        mBTNFollow.setText("Follow");
    }

    private void queryPosts(int page) {
        mActivity.showProgressBar();
        mQueryer.setPage(page);
        mQueryer.queryPosts(new ParseQueryer.ParseQueryerCallback() {
            @Override
            public void done(List data, ParseObject o) {
                mPostsAdapter.addAll(data);
            }
        }, mUser, null, false);
        mActivity.hideProgressBar();
        mQueryer.setPage(0);
    }
}