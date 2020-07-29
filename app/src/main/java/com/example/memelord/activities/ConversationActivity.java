package com.example.memelord.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Explode;
import android.transition.Slide;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.adapters.MessagesAdapter;
import com.example.memelord.databinding.ActivityConversationBinding;
import com.example.memelord.helpers.EndlessRecyclerViewScrollListener;
import com.example.memelord.helpers.ParseQueryer;
import com.example.memelord.models.Conversation;
import com.example.memelord.models.Message;
import com.example.memelord.models.User;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationActivity extends AppCompatActivity {
    public static final String TAG = ConversationActivity.class.getSimpleName();
    public static final String ARG_CONVO_USER = "TARGET_USER";
    public static final String ARG_CONVO = "TARG_CONVERSATION";
    private ActivityConversationBinding mBinding;

    private MessagesAdapter mMessagesAdapter;
    private User mUser;
    private User mCurrentUser;
    private ParseQueryer mQueryer;
    private Conversation mConvo;

    private ImageView mIVTargetAvatar;
    private RecyclerView mRVMessages;
    private EditText mETMessage;
    private TextView mTVSendBTN;
    private TextView mTargetUSN;

    private List<Message> mMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = ActivityConversationBinding.inflate(getLayoutInflater());
        View view = mBinding.getRoot();

        Intent intent = getIntent();
        mConvo = intent.getParcelableExtra(ARG_CONVO);
        mUser = intent.getParcelableExtra(ARG_CONVO_USER);
        mCurrentUser = (User) ParseUser.getCurrentUser();

        if(mUser == null || mCurrentUser == null) {
            finish();
        }

        mQueryer = ParseQueryer.getInstance();
        mMessages = new ArrayList<Message>();

        mIVTargetAvatar = mBinding.ivAvatar;
        mRVMessages = mBinding.rvMessages;
        mETMessage = mBinding.etMessage;
        mTVSendBTN = mBinding.tvSendBTN;
        mTargetUSN = mBinding.tvTargetName;

        mMessagesAdapter = new MessagesAdapter(this, mMessages, mUser);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setReverseLayout(true);
        llm.setStackFromEnd(true);
        mRVMessages.setLayoutManager(llm);
        mRVMessages.setAdapter(mMessagesAdapter);
        EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener = new EndlessRecyclerViewScrollListener(llm) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                queryMessages(page, false);
            }
        };
        mRVMessages.addOnScrollListener(endlessRecyclerViewScrollListener);

        String targetUSN = mUser.getScreenName();
        if(targetUSN == null || targetUSN.isEmpty())
            targetUSN = mUser.getUsername();
        mTargetUSN.setText(targetUSN);

        ParseFile targetUserAvatar = mUser.getAvatar();
        if(targetUserAvatar != null)
            Glide.with(this).load(targetUserAvatar.getUrl()).into(mIVTargetAvatar);

        mTVSendBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String body = mETMessage.getText().toString();

                Message msg = new Message();
                msg.setUser(mCurrentUser);
                msg.setBody(body);
                msg.put(ParseObject.KEY_CREATED_AT, new Date());
                mMessages.add(0, msg);
                mMessagesAdapter.notifyItemInserted(0);
                mRVMessages.scrollToPosition(0);
                msg.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null) {
                            Log.e(TAG, "Failed to save message", e);
                            Toast.makeText(ConversationActivity.this, "Your message failed to send!", Toast.LENGTH_SHORT).show();
                        }
                        ParseRelation convoMessages = mConvo.getMessages();
                        convoMessages.add(msg);
                        mConvo.saveInBackground();
                    }
                });
                mETMessage.clearFocus();
                mETMessage.setText("");
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });

        queryMessages(0, true);
        setContentView(view);
    }

    private void queryMessages(int page, boolean scroll) {
        mQueryer.setPage(page);
        mQueryer.queryMessages(new ParseQueryer.ParseQueryerCallback() {
            @Override
            public void done(List data, ParseObject o) {
                if(data != null)
                    mMessagesAdapter.addAll(data);
                if(scroll)
                    mRVMessages.scrollToPosition(0);
            }
        }, mConvo);
        mQueryer.setPage(0);
    }
}