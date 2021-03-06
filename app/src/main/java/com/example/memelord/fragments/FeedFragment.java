package com.example.memelord.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.memelord.R;
import com.example.memelord.activities.LoginActivity;
import com.example.memelord.adapters.PostsAdapter;
import com.example.memelord.databinding.FragmentFeedBinding;
import com.example.memelord.helpers.EndlessRecyclerViewScrollListener;
import com.example.memelord.helpers.ParseQueryer;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Post;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends BaseFragment {
    public static final String TAG = FeedFragment.class.getSimpleName();

    public static final String ARG_SEARCH_QUERY = "SEARCH_QUERY";
    private static final String ARG_TRENDING_FLAG = "TRENDING_FLAG";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentFeedBinding mBinding;

    private Util.FragmentLoader mActivity;

    private String mSearchQuery;
    private List<Post> mAllPosts;

    private RecyclerView mRVPosts;
    private PostsAdapter mPostsAdapter;
    private SwipeRefreshLayout mSwipeContainer;
    private EndlessRecyclerViewScrollListener mEndlessScrollListener;

    private Button mBTNTrending;
    private Button mBTNLatest;

    private boolean mTrendingFlag = false;

    public FeedFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
           mSearchQuery = getArguments().getString(ARG_SEARCH_QUERY);
           mTrendingFlag = getArguments().getBoolean(ARG_TRENDING_FLAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = (Util.FragmentLoader) getActivity();
        mActivity.hideProgressBar();

        // Inflate the layout for this fragment
        mBinding = FragmentFeedBinding.inflate(inflater);
        View view = mBinding.getRoot();


        mRVPosts = mBinding.rvPosts;
        mBTNTrending = mBinding.btnTrending;
        mBTNLatest = mBinding.btnLatest;
        mSwipeContainer = mBinding.swipeContainer;

        bindContent();
        return view;
    }

    @Override
    protected void bindContent() {
        mAllPosts = new ArrayList<Post>();
        mPostsAdapter = new PostsAdapter(getActivity(), getContext(), mAllPosts);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryPosts(page);
            }
        };
        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPostsAdapter.clear();
                queryPosts(0);
            }
        });
        mRVPosts.setLayoutManager(llm);
        mRVPosts.setAdapter(mPostsAdapter);
        mRVPosts.addOnScrollListener(mEndlessScrollListener);

        highlightBTN(mBTNLatest);
        clearBTNColor(mBTNTrending);

        mBTNTrending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highlightBTN(mBTNTrending);
                clearBTNColor(mBTNLatest);
                mTrendingFlag = true;
                mPostsAdapter.clear();
                queryPosts(0);
            }
        });

        mBTNLatest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                highlightBTN(mBTNLatest);
                clearBTNColor(mBTNTrending);
                mTrendingFlag = false;
                mPostsAdapter.clear();
                queryPosts(0);
            }
        });

        queryPosts(0);
    }

    private void highlightBTN(Button btn) {
        btn.setTextColor(getContext().getResources().getColor(android.R.color.white));
    }

    private void clearBTNColor(Button btn) {
        btn.setTextColor(getContext().getResources().getColor(android.R.color.black));
    }

    public void queryPosts(int page) {
        mActivity.showProgressBar();
        mQueryer.setPage(page);
        mQueryer.queryPosts(new ParseQueryer.ParseQueryerCallback() {
            @Override
            public void done(List data, ParseObject o) {
                mActivity.hideProgressBar();
                if(data != null)
                    mAllPosts.addAll(data);
                mPostsAdapter.notifyDataSetChanged();
                mSwipeContainer.setRefreshing(false);
            }
        }, null, mSearchQuery, mTrendingFlag);
        mQueryer.setPage(0);
    }
}