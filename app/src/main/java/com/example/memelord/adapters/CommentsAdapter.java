package com.example.memelord.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Comment;
import com.example.memelord.models.User;
import com.parse.ParseFile;

import java.util.Date;
import java.util.List;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {
    public static final String TAG = CommentsAdapter.class.getSimpleName();

    private List<Comment> mComments;
    private Context mContext;

    public CommentsAdapter(Context context, List<Comment> comments) {
        mComments = comments;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment comment = mComments.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }

    public void addAll(List<Comment> data) {
        mComments.addAll(data);
        notifyDataSetChanged();
    }
    public class ViewHolder extends  RecyclerView.ViewHolder {

        private ImageView mIVAvatar;
        private TextView mTVBody;
        private TextView mTVUsername;
        private TextView mTVDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mIVAvatar = itemView.findViewById(R.id.ivAvatar);
            mTVBody = itemView.findViewById(R.id.tvBody);
            mTVUsername = itemView.findViewById(R.id.tvUsername);
            mTVDate = itemView.findViewById(R.id.tvDate);
        }

        public void bind(Comment comment) {
            ParseFile avatar = comment.getUser().getParseFile(User.KEY_AVATAR);
            Date date = comment.getCreatedAt();
            if (date == null)
                date = new Date();
            if(avatar != null)
                Glide.with(mContext).load(avatar.getUrl()).into(mIVAvatar);
            String name = comment.getUser().getString(User.KEY_SCREEN_NAME);
            if(name == null)
                name = comment.getUser().getUsername();
            mTVUsername.setText(name);
            mTVBody.setText(comment.getBody());
            mTVDate.setText(Util.getRelativeTimeAgo(date));
        }
    }
}
