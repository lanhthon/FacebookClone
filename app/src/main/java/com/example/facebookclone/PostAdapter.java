package com.example.facebookclone;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private List<Post> postList;
    private Context context;

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
        holder.textViewUserName.setText(post.getUserName());
        holder.textViewContent.setText(post.getContent());
        holder.textViewLikes.setText(String.format("%d likes", post.getLikesCount()));
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        // Sử dụng Glide để tải ảnh từ URL và hiển thị trong ImageView
        Glide.with(context).load(post.getImageUrl()).into(holder.imageViewPost);
        if (currentUser != null && post.getLikes().containsKey(currentUser.getUid())) {
            // Nếu người dùng đã thích, đổi icon thành biểu tượng trái tim
            holder.buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_heart, 0, 0, 0);
            holder.buttonLike.setTextColor(ContextCompat.getColor(context, R.color.colorfacebook));
        } else {
            // Nếu người dùng chưa thích, sử dụng icon like bình thường
            holder.buttonLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like, 0, 0, 0);
            holder.buttonLike.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
        // Bắt sự kiện khi người dùng click vào nút like, comment, share
        holder.buttonLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem người dùng đã đăng nhập hay chưa

                if (currentUser == null) {
                    // Nếu chưa đăng nhập, yêu cầu người dùng đăng nhập trước khi like
                    // (Bạn có thể hiển thị một thông báo hoặc mở màn hình đăng nhập tại đây)
                    Toast.makeText(context, "Bạn cần đăng nhập để thích bài viết.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Lấy id của bài viết được click
                String postId = post.getPostId();

                // Thực hiện cập nhật trạng thái like của người dùng
                DatabaseReference likesRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postId).child("likes").child(currentUser.getUid());
                likesRef.runTransaction(new Transaction.Handler() {
                    @NonNull
                    @Override
                    public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                        // Lấy trạng thái hiện tại của like
                        String currentLikeState = mutableData.getValue(String.class);

                        // Nếu chưa có trạng thái like hoặc là "false", đặt trạng thái mới là "true" (người dùng đã like)
                        // Nếu đã có trạng thái like và là "true", đặt trạng thái mới là "false" (người dùng đã bỏ like)
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
                            // Xử lý lỗi nếu có
                            Log.e("LikeError", "Failed to like post: " + databaseError.getMessage());
                        } else {
                            // Cập nhật số lượng likes
                            updateLikesCount(postId);
                        }
                    }
                });
            }
        });


        holder.buttonComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng click vào nút comment
            }
        });
        holder.buttonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Xử lý khi người dùng click vào nút share
            }
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    private void updateLikesCount(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference().child("posts").child(postId);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Đếm số lượng likes dựa trên dữ liệu từ Firebase
                    long likesCount = dataSnapshot.child("likes").getChildrenCount();

                    // Cập nhật số lượng likes trong danh sách bài viết
                    for (int i = 0; i < postList.size(); i++) {
                        if (postList.get(i).getPostId().equals(postId)) {
                            postList.get(i).setLikesCount((int) likesCount-1);
                            break;
                        }
                    }


                    postRef.child("likesCount").setValue(likesCount-1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý lỗi nếu có
                Log.e("LikesCountError", "Failed to update likes count: " + databaseError.getMessage());
            }
        });
    }

    public static class PostViewHolder extends RecyclerView.ViewHolder {
        TextView textViewUserName, textViewContent, textViewLikes;
        ImageView imageViewPost;
        Button buttonLike, buttonComment, buttonShare;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewLikes = itemView.findViewById(R.id.textViewLikeCount);
            textViewUserName = itemView.findViewById(R.id.textViewUserName);
            textViewContent = itemView.findViewById(R.id.textViewContent);
            imageViewPost = itemView.findViewById(R.id.imageViewPost);
            buttonLike = itemView.findViewById(R.id.buttonLike);
            buttonComment = itemView.findViewById(R.id.buttonComment);
            buttonShare = itemView.findViewById(R.id.buttonShare);
        }
    }
}
