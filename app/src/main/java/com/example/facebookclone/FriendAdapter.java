package com.example.facebookclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.FriendViewHolder> {

    private Context mContext;
    private List<User> mFriendsList;

    public FriendAdapter(Context context, List<User> friendsList) {
        mContext = context;
        mFriendsList = friendsList;
    }

    @Override
    public FriendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_frend_profile, parent, false);
        return new FriendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FriendViewHolder holder, int position) {
        User friend = mFriendsList.get(position);
        holder.textViewName.setText(friend.firstName + " " + friend.lastName);
        if (friend.avatarsrc != null && !friend.avatarsrc.isEmpty()) {
            Picasso.get().load(friend.avatarsrc).into(holder.imageViewProfile);
        }
    }

    @Override
    public int getItemCount() {
        return mFriendsList.size();
    }

    class FriendViewHolder extends RecyclerView.ViewHolder {
        TextView textViewName;
        ImageView imageViewProfile;

        public FriendViewHolder(View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.tvusernamefrend);
            imageViewProfile = itemView.findViewById(R.id.imageViewuser);
        }
    }
}
