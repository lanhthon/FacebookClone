package com.example.facebookclone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;
    private Context context;
    private ImageView imageViewAvater;

    public CommentAdapter(List<Comment> commentList) {
        this.commentList = commentList;
    }
    public void setComments(List<Comment> comments) {
        this.commentList = comments;
        notifyDataSetChanged(); // Thông báo cho RecyclerView cập nhật giao diện
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = commentList.get(position);
        holder.bind(comment);
    }

    @Override
    public int getItemCount() {
        if (commentList != null) {
            return commentList.size();
        } else {
            return 0;
        }
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder {
        private TextView textUsername;
        private TextView textCommentContent;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            textUsername = itemView.findViewById(R.id.text_username);
            textCommentContent = itemView.findViewById(R.id.text_comment_content);
            imageViewAvater=itemView.findViewById(R.id.imageViewAvatar);
        }

        public int getItemCountcomment() {
            return commentList.size();

        }

        public void bind(Comment comment) {

            textCommentContent.setText(comment.getContent());

            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(comment.getUserId());
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String firstName = dataSnapshot.child("firstName").getValue(String.class);
                        String lastName = dataSnapshot.child("lastName").getValue(String.class);
                        String avatarSrc = dataSnapshot.child("avatarSrc").getValue(String.class);
                        textUsername.setText(String.format("%s %s", firstName, lastName));
                        if (avatarSrc != null) {
                            Picasso.get().load(avatarSrc).into(imageViewAvater);
                        }
                    } else {
                        Log.d("UserInfo", "User not found");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }

            });
        }
    }
}
