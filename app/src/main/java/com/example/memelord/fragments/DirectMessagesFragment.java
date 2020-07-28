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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DirectMessagesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DirectMessagesFragment extends BaseFragment {
    public static final String TAG = DirectMessagesFragment.class.getSimpleName();
    private FragmentDirectMessagesBinding mBinding;

    private RecyclerView mRVConversations;
    private ParseQueryer mQueryer;
    private ConversationsAdapter mConversationsAdapter;
    private List<Conversation> mConvos;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DirectMessagesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DirectMessagesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DirectMessagesFragment newInstance(String param1, String param2) {
        DirectMessagesFragment fragment = new DirectMessagesFragment();
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