package com.example.memelord.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.memelord.R;
import com.example.memelord.adapters.ConversationsAdapter;
import com.example.memelord.databinding.FragmentDirectMessagesBinding;
import com.example.memelord.helpers.EndlessRecyclerViewScrollListener;
import com.example.memelord.helpers.ParseQueryer;
import com.example.memelord.models.Conversation;
import com.parse.ParseObject;

import java.util.ArrayList;
import java.util.List;

public class DirectMessagesFragment extends BaseFragment {
    public static final String TAG = DirectMessagesFragment.class.getSimpleName();
    private FragmentDirectMessagesBinding mBinding;

    private RecyclerView mRVConversations;
    private ParseQueryer mQueryer;
    private ConversationsAdapter mConversationsAdapter;
    private List<Conversation> mConvos;

    public DirectMessagesFragment() {
        // Required empty public constructor
    }

    public static DirectMessagesFragment newInstance() {
        DirectMessagesFragment fragment = new DirectMessagesFragment();
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
        mBinding = FragmentDirectMessagesBinding.inflate(inflater);
        View view = mBinding.getRoot();

        mRVConversations = mBinding.rvConvos;
        mConvos = new ArrayList<Conversation>();
        mConversationsAdapter = new ConversationsAdapter(getContext(), mConvos);

        mQueryer = ParseQueryer.getInstance();

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryConversations(page);
            }
        };
        mRVConversations.setLayoutManager(llm);
        mRVConversations.setAdapter(mConversationsAdapter);
        mRVConversations.addOnScrollListener(endlessRecyclerViewScrollListener);


        queryConversations(0);
        return view;
    }

    public void queryConversations(int page) {
        mQueryer.setPage(page);
        mQueryer.queryConversations(new ParseQueryer.ParseQueryerCallback() {
            @Override
            public void done(List data, ParseObject o) {
                mConversationsAdapter.addAll(data);
            }
        });
        mQueryer.setPage(0);
    }
}