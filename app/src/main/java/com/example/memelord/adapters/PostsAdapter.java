package com.example.memelord.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.memelord.R;
import com.example.memelord.fragments.PostViewFragment;
import com.example.memelord.fragments.ProfileFragment;
import com.example.memelord.helpers.Util;
import com.example.memelord.models.Post;
import com.example.memelord.models.User;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    public static final String TAG = PostsAdapter.class.getSimpleName();
    public static final String FORMAT_DATE_CATEGORY = "%s / %s";
    private Context mContext;
    private Util.FragmentLoader mActivity;
    private List<Post> mPosts;

    public PostsAdapter(Activity activity, Context ctx, List<Post> data) {
        mContext = ctx;
        mActivity = (Util.FragmentLoader) activity;
        mPosts = data;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = mPosts.get(position);
        holder.bind(post);
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {

        private ConstraintLayout mCLContent;

        private ImageView mIVAvatar;
        private ImageView mIVBG;
        private TextView mTVUsername;
        private TextView mTVDateCategory;
        private TextView mTVBody;
        private TextView mTVLikesCount;
        private int mTextColor = Color.BLACK;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIVAvatar = itemView.findViewById(R.id.ivAvatar);
            mIVBG = itemView.findViewById(R.id.ivBG);
            mTVUsername = itemView.findViewById(R.id.etUsername);
            mTVDateCategory = itemView.findViewById(R.id.tvDateCategory);
            mTVBody = itemView.findViewById(R.id.tvBody);
            mTVLikesCount = itemView.findViewById(R.id.tvLikeCount);
            mCLContent = itemView.findViewById(R.id.clContent);
        }

        public void bind(Post post) {
            ParseUser author = post.getUser();
            ParseFile avatar = post.getUser().getParseFile(User.KEY_AVATAR);
            ParseFile background = post.getImage();
            String category = "CopyPasta";
            mTVBody.setText(post.getBody());
            mIVBG.setVisibility(View.GONE);
            if(avatar != null) {
                Glide.with(mContext).load(avatar.getUrl()).into(mIVAvatar);
            } else {
                mIVAvatar.setImageBitmap(null);
            }
            if(background != null) {
                category = "Classic Meme";
                Glide.with(mContext).load(background.getUrl()).into(mIVBG);
                mTextColor = Color.WHITE;
                mTVBody.setTextColor(mTextColor);
                mTVDateCategory.setTextColor(mTextColor);
                mTVUsername.setTextColor(mTextColor);
                mIVBG.setVisibility(View.VISIBLE);
                mTVBody.setTextSize(24f);
                mTVBody.setText(post.getTitle());
                mTVBody.setTypeface(Typeface.DEFAULT_BOLD);
                mCLContent.setBackgroundColor(mContext.getResources().getColor(R.color.memelord_sakura_light));
            } else {
                mTVUsername.setTextColor(mContext.getResources().getColor(R.color.memelord_primary_text_dark));
                mTVBody.setTextColor(mContext.getResources().getColor(android.R.color.secondary_text_dark));
                mTVDateCategory.setTextColor(mContext.getResources().getColor(R.color.memelord_primary_text_dark));
                mTVBody.setTextColor(mTVBody.getTextColors().getDefaultColor());
                mTVBody.setTextSize(10f);
                mCLContent.setBackgroundColor(Color.WHITE);
                mTVBody.setTypeface(Typeface.DEFAULT);
            }
            String name = author.getString(User.KEY_SCREEN_NAME);
            if(name == null || name.isEmpty())
                name = author.getUsername();
            mTVUsername.setText(name);
            mTVLikesCount.setText(Util.formatNumber((double) post.getLikesCount()));
            mTVDateCategory.setText(String.format(FORMAT_DATE_CATEGORY, Util.getRelativeTimeAgo(post.getCreatedAt()), category));

            mIVAvatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(ProfileFragment.ARG_USER, (User) author);
                    mActivity.loadFragment(new ProfileFragment(), bundle);
                }
            });
            mCLContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // TODO Go to post view fragment
                    Bundle bundle = new Bundle();
                    bundle.putParcelable(PostViewFragment.ARG_POST, post);
                    mActivity.loadFragment(new PostViewFragment(), bundle);
                }
            });
        }
    }
}
