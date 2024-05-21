package com.example.facebookclone;

import static java.security.AccessController.getContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;
    private List<Comment> commentList;

    public PostAdapter(List<Post> postList, Context context) {
        this.postList = postList;
        this.context = context;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        Post post = postList.get(position);
        String postId = post.getPostId();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(post.getUserId());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String avatarSrc = dataSnapshot.child("avatarsrc").getValue(String.class);
                    holder.textViewUserName.setText(String.format("%s %s", firstName, lastName));
                    if (avatarSrc != null) {
                        Glide.with(context).load(avatarSrc).into(holder.imageViewuser);
                    }
                } else {
                    Log.d("UserInfo", "User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("UserInfo", "Error: " + databaseError.getMessage());
            }
        });

        holder.textViewTime.setText(edittime(post.getTime()));
        holder.textViewContent.setText(post.getContent());

        holder.textViewLikes.setText(String.format("%d likes", post.getLikesCount()));
        countComments(postId, new CountCommentsCallback() {
            @Override
            public void onCountReceived(long count) {
                holder.textViewComment.setText(String.format("%d comments", count));
            }
        });

        Glide.with(context).load(post.getImageUrl()).into(holder.imageViewPost);

        if (currentUser != null && post.getLikes().containsKey(currentUser.getUid())) {
            holder.buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart, 0, 0, 0);
            holder.buttonLike.setTextColor(ContextCompat.getColor(context, R.color.colorfacebook));
        } else {
            holder.buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
            holder.buttonLike.setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentUser == null) {
                    Toast.makeText(context, "Bạn cần đăng nhập để thích bài viết.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes").child(currentUser.getUid());
                likesRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        String currentLikeState = mutableData.getValue(String.class);
                        if (currentLikeState == null || currentLikeState.equals("false")) {
                            mutableData.setValue("true");
                        } else {
                            mutableData.setValue(null);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                        if (databaseError != null) {
                            Log.e("LikeError", "Failed to like post: " + databaseError.getMessage());
                        } else {
                            updateLikesCount(postId);
                        }
                    }
                });
            }
        });

        holder.buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommentDialog(context, post.getPostId());
            }
        });

        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePost(post);
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public void countComments(String postId, final CountCommentsCallback callback) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                callback.onCountReceived(count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    public interface CountCommentsCallback {
        void onCountReceived(long count);
    }

    private String edittime(String firebaseDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String timeElapsedString = "";
        try {
            Date firebaseDate = dateFormat.parse(firebaseDateString);
            Date currentDate = new Date();
            long elapsedTime = currentDate.getTime() - firebaseDate.getTime();
            long seconds = elapsedTime / 1000;
            long minutes = seconds / 60;
            long hours = minutes / 60;
            long days = hours / 24;
            long months = days / 30;
            if (months > 0) {
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                timeElapsedString = outputFormat.format(firebaseDate);
            } else if (days > 0) {
                timeElapsedString = days + " ngày trước";
            } else if (hours > 0) {
                timeElapsedString = hours + " giờ trước";
            } else if (minutes > 0) {
                timeElapsedString = minutes + " phút trước";
            } else {
                timeElapsedString = seconds + " giây trước";
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeElapsedString;
    }

    private void showCommentDialog(Context context, String postId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.activity_comment, null);
        builder.setView(view);

        EditText editTextComment = view.findViewById(R.id.edit_text_comment);
        Button buttonSendComment = view.findViewById(R.id.button_send_comment);
        RecyclerView recyclerViewComments = view.findViewById(R.id.recycler_view_comments);
        commentList = new ArrayList<>();
        CommentAdapter commentAdapter = new CommentAdapter(commentList);
        recyclerViewComments.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewComments.setAdapter(commentAdapter);

        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        commentsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Comment comment = snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
                updateCommentCount(postId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "Failed to load comments: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        buttonSendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentContent = editTextComment.getText().toString().trim();
                if (!commentContent.isEmpty()) {
                    saveCommentToDatabase(postId, commentContent);
                    editTextComment.setText("");
                } else {
                    Toast.makeText(context, "Please enter a comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void saveCommentToDatabase(String postId, String commentContent) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        String commentId = commentsRef.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Comment comment = new Comment(commentId, postId, userId, commentContent);

        commentsRef.child(commentId).setValue(comment)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(context, "Comment posted successfully", Toast.LENGTH_SHORT).show();
                    updateCommentCount(postId);
                })
                .addOnFailureListener(e -> Toast.makeText(context, "Failed to post comment: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void updateCommentCount(String postId) {
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference().child("comments").child(postId);
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long count = dataSnapshot.getChildrenCount();
                for (Post post : postList) {
                    if (post.getPostId().equals(postId)) {
                        post.setCommentsCount((int) count);
                        notifyDataSetChanged();
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error
            }
        });
    }


    private void updateLikesCount(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long likesCount = dataSnapshot.child("likes").getChildrenCount();
                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getPostId().equals(postId)) {
                            postList.get(i).setLikesCount((int) likesCount - 1);
                            break;
                        }
                    }
                    postRef.child("likesCount").setValue(likesCount - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("LikesCountError", "Failed to update likes count: " + databaseError.getMessage());
            }
        });
    }

    private void sharePost(Post post) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareContent = post.getContent() + "\n\n" + post.getImageUrl();
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
        context.startActivity(Intent.createChooser(shareIntent, "Share post via"));
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewContent, textViewLikes, textViewTime, textViewComment;
        ImageView imageViewPost, imageViewuser;
        Button buttonLike, buttonComment, buttonShare;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLikes = itemView.findViewById(R.id.textViewLikeCount);
            textViewComment = itemView.findViewById(R.id.textViewCommentCount);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            textViewTime = itemView.findViewById(R.id.textViewUserDescription);
            imageViewPost = itemView.findViewById(R.id.imageViewPost);
            imageViewuser = itemView.findViewById(R.id.imageViewAvatar);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            buttonComment = itemView.findViewById(R.id.buttonComment);
            buttonShare = itemView.findViewById(R.id.buttonShare);
        }
    }
}
