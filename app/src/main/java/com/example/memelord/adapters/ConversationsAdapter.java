package com.example.memelord.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.activities.ConversationActivity;
import com.example.memelord.models.Conversation;
import com.example.memelord.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class ConversationsAdapter extends RecyclerView.Adapter<ConversationsAdapter.ViewHolder> {
    public static final String TAG = ConversationsAdapter.class.getSimpleName();
    private List<Conversation> mConversations;
    private Context mContext;

    public ConversationsAdapter(Context ctx, List<Conversation> convos) {
        mConversations = convos;
        mContext = ctx;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation convo = mConversations.get(position);
        holder.bind(convo);
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public void addAll(List<Conversation> convos) {
        mConversations.addAll(convos);
        notifyDataSetChanged();
    }

    public void clear(List<Conversation> convos) {
        mConversations.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder  extends RecyclerView.ViewHolder {
        private ImageView mIVAvatar;
        private TextView mTVName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIVAvatar = itemView.findViewById(R.id.ivAvatar);
            mTVName = itemView.findViewById(R.id.tvUsername);
        }

        public void bind(Conversation convo) {
            User targetUser = null;
            ParseUser currentUser = ParseUser.getCurrentUser();
            if(convo.getUser1().getObjectId().equals(currentUser.getObjectId())) {
                targetUser = (User) convo.getUser2();
            } else {
                targetUser = (User) convo.getUser1();
            }

            String name = targetUser.getScreenName();
            if(name == null || name.isEmpty()) {
                name = targetUser.getUsername();
            }
            mTVName.setText(name);
            ParseFile avatar = targetUser.getAvatar();
            if(avatar != null)
                Glide.with(mContext).load(avatar.getUrl()).into(mIVAvatar);
            mIVAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    User user = null;
                    ParseUser cUser = ParseUser.getCurrentUser();
                    if(convo.getUser1().getObjectId().equals(currentUser.getObjectId())) {
                        user = (User) convo.getUser2();
                    } else {
                        user = (User) convo.getUser1();
                    }
                    Intent intent = new Intent(mContext, ConversationActivity.class);
                    intent.putExtra(ConversationActivity.ARG_CONVO_USER, user);
                    intent.putExtra(ConversationActivity.ARG_CONVO, convo);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
