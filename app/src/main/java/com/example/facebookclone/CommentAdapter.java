package com.example.facebookclone;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private List<Comment> commentList;

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
        }

        public void bind(Comment comment) {
            textUsername.setText(comment.getPostId());
            textCommentContent.setText(comment.getContent());
        }
    }
}
