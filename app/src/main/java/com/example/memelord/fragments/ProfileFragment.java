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
import com.example.memelord.activities.LoginActivity;
import com.example.memelord.adapters.PostsAdapter;
import com.example.memelord.databinding.FragmentProfileBinding;
import com.example.memelord.helpers.EndlessRecyclerViewScrollListener;
import com.example.memelord.helpers.ParseQueryer;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Post;
import com.example.memelord.models.Profile;
import com.example.memelord.models.User;
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

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
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

        mActivity = (Util.FragmentLoader) getActivity();
        mCurrentUser = (User) ParseUser.getCurrentUser();
        if(mUser == null || mUser.getObjectId().equals(mCurrentUser.getObjectId())) {
            mUser = mCurrentUser;
            mBTNEditProfile.setVisibility(View.VISIBLE);
        } else {
            mBTNFollow.setVisibility(View.VISIBLE);
        }
        mProfile = (Profile) mUser.getParseObject(User.KEY_PROFILE);
        if(mProfile == null) {
            mProfile = createProfile();
        }
        mProfileFollowers = mProfile.getFollowersRelation();
        mCurrentUserProfile = (Profile) mCurrentUser.getProfile();
        if(mCurrentUserProfile == null) {
            mCurrentUserProfile = createProfile();
            mCurrentUser.setProfile(mCurrentUserProfile);
            try {
                mCurrentUserProfile.save();
            } catch (ParseException e) {
                Log.e(TAG, "Failed to save created profile for current user", e);
            }
            try {
                mCurrentUser.save();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mUserFollowing = mCurrentUserProfile.getFollowingRelation();

        mFollowersCount = mProfile.getFollowersCount();
        mFollowingCount = mProfile.getFollowingCount();
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
                    if(mFollowingProfile) {
                        unfollowProfile();
                        mBTNFollow.setText("Follow");
                    } else {
                        followProfile();
                        mBTNFollow.setText("Unfollow");
                    }
                }
            }
        });
        queryPosts(0);
    }

    private void followProfile() {
        mUserFollowing.add(mUser);
        mCurrentUserProfile.setFollowingCount(mCurrentUserProfile.getFollowingCount() + 1);
        mCurrentUserProfile.saveInBackground();
        mProfileFollowers.add(mCurrentUser);
        mProfile.setFollowersCount(++mFollowersCount);
        mProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mFollowingProfile = true;
            }
        });
        mTVFollowerCount.setText(""+mFollowersCount);
    }

    private void checkIfFollowing() {
        ParseQuery query = mUserFollowing.getQuery();
        query.whereEqualTo(User.KEY_OBJECT_ID, mCurrentUser.getObjectId());
       query.getFirstInBackground(new GetCallback() {
           @Override
           public void done(ParseObject object, ParseException e) {
               if(object != null) {
                   mFollowingProfile = true;
                   mBTNFollow.setText("Unfollow");
               }
               mFollowingProfile = false;
           }

           @Override
           public void done(Object o, Throwable throwable) {

           }
       });
    }

    private void unfollowProfile() {
        mUserFollowing.remove(mUser);
        mCurrentUserProfile.setFollowingCount(mCurrentUserProfile.getFollowingCount() - 1);
        mCurrentUserProfile.saveInBackground();
        mProfileFollowers.remove(mCurrentUser);
        mProfile.setFollowersCount(--mFollowersCount);
        mProfile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                mFollowingProfile = false;
            }
        });
        mTVFollowerCount.setText(""+mFollowersCount);
    }


    private Profile createProfile() {
        Profile profile = new Profile();
        profile.setUser(mUser);
        profile.saveInBackground();
        return profile;
    }

    private void queryPosts(int page) {
        mQueryer.setPage(page);
        mQueryer.queryPosts(new ParseQueryer.ParseQueryerCallback() {
            @Override
            public void done(List data, ParseObject o) {
                mPostsAdapter.addAll(data);
            }
        }, mUser, null);
        mQueryer.setPage(0);
    }
}