package com.example.memelord.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.memelord.R;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Message;
import com.example.memelord.models.User;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    private List<Message> mMessages;
    private Context mContext;
    private User mTargetUser;

    public MessagesAdapter(Context ctx, List<Message> data, User targetUser) {
        mMessages = data;
        mContext = ctx;
        mTargetUser = targetUser;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_message_receiver, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Message msg = mMessages.get(position);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        if(msg.getUser().getObjectId().equals(currentUser.getObjectId())) {
            holder.itemView.findViewById(R.id.tvMessageBody).setBackgroundResource(R.drawable.message_receiver_bubble);
        } else {
            holder.itemView.findViewById(R.id.tvMessageBody).setBackgroundResource(R.drawable.message_sender_bubble);
        }
        holder.itemView.findViewById(R.id.tvMessageBody).setPadding(50,20,50,20);
        holder.bind(msg);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    public void addAll(List<Message> data) {
        mMessages.addAll(data);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mTVBody;
        private TextView mTVDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTVBody = itemView.findViewById(R.id.tvMessageBody);
            mTVDate = itemView.findViewById(R.id.tvTime);
        }

        public void bind(Message msg) {
            Date createdAt = msg.getCreatedAt();
            if(createdAt == null)
                createdAt = new Date();
            mTVBody.setText(msg.getBody());
            mTVDate.setText(Util.getRelativeTimeAgo(createdAt));
        }
    }
}
