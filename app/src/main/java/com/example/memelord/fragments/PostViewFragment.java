package com.example.memelord.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.adapters.CommentsAdapter;
import com.example.memelord.databinding.FragmentPostViewBinding;
import com.example.memelord.helpers.EndlessRecyclerViewScrollListener;
import com.example.memelord.helpers.ParseQueryer;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Comment;
import com.example.memelord.models.Like;
import com.example.memelord.models.Post;
import com.example.memelord.models.User;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
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
 * Use the {@link PostViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostViewFragment extends Fragment {
    public static final String TAG = PostViewFragment.class.getSimpleName();
    public static final String ARG_POST = "PARSE_POST";

    private FragmentPostViewBinding mBinding;

    private Post mPost;
    private Like mLike = null;
    List<Comment> mComments;

    private RecyclerView mRVComments;
    private CommentsAdapter mCommentsAdapter;

    private TextView mTVDate;
    private TextView mTVViews;
    private TextView mTVLikesCount;
    private TextView mTVPostTitle;

    private ImageView mIVLikesBTN;
    private ImageView mIVAvatar;
    private ImageView mIVMeme;

    private EditText mETDesc; // Still a display view
    private EditText mETComment;

    private int mLikesCount;
    private boolean mLikeDebounce;


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

        ParseRelation relation = mPost.getLikes();
        ParseQuery<Like> likesQuery = relation.getQuery();

        mComments = new ArrayList<Comment>();
        mCommentsAdapter = new CommentsAdapter(getContext(), mComments);
        ParseQueryer queryer = ParseQueryer.getInstance();
        fetchComments(queryer, 0);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRVComments = mBinding.rvComments;
        mRVComments.setLayoutManager(llm);
        mRVComments.setAdapter(mCommentsAdapter);
        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                fetchComments(queryer, page);
            }
        };

        mTVDate = mBinding.tvDate;
        mTVViews = mBinding.tvViews;
        mTVLikesCount = mBinding.tvLikeCount;
        mTVPostTitle = mBinding.tvTitle;
        mIVLikesBTN = mBinding.ivLikeBTN;
        mIVAvatar = mBinding.ivAvatar;
        mETComment = mBinding.etComment;
        mETDesc = mBinding.etPostDescription;
        mIVMeme = mBinding.ivMeme;

        mLikesCount = mPost.getLikesCount();

        mLikeDebounce = false;
        likesQuery.whereEqualTo(Like.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
        likesQuery.findInBackground(new FindCallback<Like>() {
            @Override
            public void done(List<Like> objects, ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to query for likes", e);
                    return;
                }
                if(!objects.isEmpty()) {
                    mLike = objects.get(0);
                    mIVLikesBTN.setColorFilter(R.color.memelord_sakura);
                }
                mLikeDebounce = true;
            }
        });

        mETDesc.setText(mPost.getBody());
        mTVPostTitle.setText(mPost.getTitle());
        mTVLikesCount.setText(Util.formatNumber(mLikesCount));
        mTVDate.setText(Util.getRelativeTimeAgo(mPost.getCreatedAt()));
        ParseFile file = mPost.getImage();
        if(file != null)
            Glide.with(getContext()).load(file.getUrl()).into(mIVMeme);
        ParseFile avatar = mPost.getUser().getParseFile(User.KEY_AVATAR);
        if(avatar != null)
            Glide.with(getContext()).load(avatar.getUrl()).into(mIVAvatar);
        if(mLike != null)
            mIVLikesBTN.setColorFilter(R.color.memelord_sakura);

        mIVLikesBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishLike();
            }
        });
        mETComment.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    // TODO Rethink model for comments
                    String body = mETComment.getText().toString();
                    mETComment.clearFocus();
                    mETComment.getText().clear();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mETComment.getWindowToken(), 0);

                    if(body != null && !body.isEmpty()) {
                        publishComment(body);
                    } else {
                        Toast.makeText(getContext(), "Please add a valid comment", Toast.LENGTH_SHORT);
                    }
                }
                return false;
            }
        });
        return view;
    }

    private void publishLike() {
        if(!mLikeDebounce)
            return;
        if(mLike != null ) {
            mTVLikesCount.setText(Util.formatNumber(--mLikesCount));
            mIVLikesBTN.clearColorFilter();
            ParseRelation relation = mPost.getLikes();
            relation.remove(mLike);
            mLike.deleteInBackground(new DeleteCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "Failed to removed like from relation", e);
                        Toast.makeText(getContext(), "Your like was not removed.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mPost.setLikesCount(mLikesCount);
                    mPost.saveInBackground();
                    mLike = null;
                }
            });
        } else {
            mTVLikesCount.setText(Util.formatNumber(++mLikesCount));
            mIVLikesBTN.setColorFilter(R.color.memelord_sakura);
            ParseUser currentUser = ParseUser.getCurrentUser();
            ParseRelation relation = mPost.getLikes();
            Like like = new Like();
            like.setUserId(currentUser.getObjectId());
            like.setPostId(mPost.getObjectId());
            like.setUser(currentUser);
            like.setPost(mPost);
            mLike = like;
            like.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if(e != null) {
                        Log.e(TAG, "Failed to save like!", e);
                        Toast.makeText(getContext(), "Your like was not registered.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Log.i(TAG, "saved like");
                    relation.add(like);
                    mPost.setLikesCount(mLikesCount);
                    mPost.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e != null) {
                                Log.e(TAG, "Failed to save post on like", e);
                                return;
                            }
                            Log.i(TAG, "saved post on like");
                        }
                    });
                }
            });
        }
    }

    private void publishComment(String body) {
        ParseRelation relation = mPost.getComments();
        Comment comment = new Comment();
        comment.setBody(body);
        comment.setUser(ParseUser.getCurrentUser());
        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null) {
                    Log.e(TAG, "Failed to save comment to Parse", e);
                    return;
                }
                relation.add(comment);
                mPost.saveInBackground();
                Toast.makeText(getContext(), "Your comment was added successfully!", Toast.LENGTH_SHORT).show();
            }
        });
        mComments.add(0, comment);
        mCommentsAdapter.notifyItemInserted(0);
    }

    private void fetchComments(ParseQueryer queryer, int page) {
        queryer.setPage(page);
        queryer.queryComments(new ParseQueryer.ParseQueryerCallback() {
            @Override
            public void done(List data, ParseObject o) {
                mCommentsAdapter.addAll(data);
            }
        }, mPost);
        queryer.setPage(0);
    }
}