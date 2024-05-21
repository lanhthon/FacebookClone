package com.example.facebookclone;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private List<Notification> notificationList;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView timeTextView;
        public ImageView iconImageView;

        public ViewHolder(View view) {
            super(view);
            messageTextView = view.findViewById(R.id.text_notification_message);
            timeTextView = view.findViewById(R.id.text_notification_time);
            iconImageView = view.findViewById(R.id.image_notification_icon);

            view.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Notification notification = notificationList.get(position);
                    Intent intent;
                    if (notification.isFriendRequest()) {
                        intent = new Intent(itemView.getContext(), profile.class);
                        intent.putExtra("userId", notification.getId());
                    } else {
                        intent = new Intent(itemView.getContext(), PostDetailActivity.class);
                        intent.putExtra("postId", notification.getId());
                    }
                    view.getContext().startActivity(intent);
                }
            });
        }
        }


    public NotificationAdapter(List<Notification> notificationList) {
        this.notificationList = notificationList;
    }

    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Notification notification = notificationList.get(position);
        holder.messageTextView.setText(notification.getMessage());
        holder.timeTextView.setText(notification.getTime());
        if (notification.getSenderAvatar() != null && !notification.getSenderAvatar().isEmpty()) {
            Picasso.get().load(notification.getSenderAvatar()).into(holder.iconImageView);
        }

        holder.iconImageView.setImageResource(R.drawable.img_avatar);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }
}
