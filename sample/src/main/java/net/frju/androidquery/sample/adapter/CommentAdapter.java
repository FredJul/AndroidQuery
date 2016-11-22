package net.frju.androidquery.sample.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import net.frju.androidquery.sample.R;
import net.frju.androidquery.sample.model.Comment;

import java.text.DateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Comment[] comments;

    public void setComments(Comment[] comments) {
        this.comments = comments;
        notifyDataSetChanged();
    }

    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.comment_adapter, parent, false);

        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CommentAdapter.CommentViewHolder holder, int position) {
        Comment comment = comments[position];
        holder.populate(comment);
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.length : 0;
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.comment_adapter_author)
        TextView author;

        @BindView(R.id.comment_adapter_body)
        TextView body;

        @BindView(R.id.comment_adapter_timestamp)
        TextView timestamp;

        public CommentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void populate(Comment comment) {
            author.setText(comment.user.username);
            body.setText(comment.body);
            timestamp.setText(DateFormat.getDateInstance().format(comment.timestamp));
        }
    }
}
