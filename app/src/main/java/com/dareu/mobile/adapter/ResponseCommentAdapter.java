package com.dareu.mobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.CommentDescription;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 03/09/2017.
 */

public class ResponseCommentAdapter  extends RecyclerView.Adapter<ResponseCommentAdapter.CommentViewHolder>{

    private Context cxt;
    private List<CommentDescription> list;
    private CommentButtonClickListener listener;

    public ResponseCommentAdapter(Context cxt, List<CommentDescription> list, CommentButtonClickListener listener){
        this.cxt = cxt;
        this.list = list;
        this.listener = listener;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create view
        View view = LayoutInflater.from(cxt)
                .inflate(R.layout.response_comment_item, parent, false);

        return new CommentViewHolder(view);
    }

    public void add(CommentDescription description){
        list.add(description);
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, final int position) {
        final CommentDescription description = list.get(position);
        //load image
        SharedUtils.loadImagePicasso(holder.image, cxt, description.getResponse().getUser().getImageUrl());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentButtonClicked(description, position, CommentEventType.CONTACT);
            }
        });
        //load text views
        holder.name.setText(description.getUser().getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentButtonClicked(description, position, CommentEventType.CONTACT);
            }
        });

        holder.comment.setText(description.getComment());
        holder.date.setText(SharedUtils.getFromDate(description.getCommentDate()));
        holder.clap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onCommentButtonClicked(description, position, CommentEventType.CLAP);
            }
        });

        if(description.isClapped()){
            holder.clap.setText("Remove clap");
        }else{
            holder.clap.setText("Clap comment");
        }
    }

    public void clapComment(boolean clapped, int position){
        CommentDescription desc = list.get(position);
        desc.setClapped(clapped);
        if(clapped){
            desc.setClaps(desc.getClaps() + 1);
        }else{
            desc.setClaps(desc.getClaps() - 1);
        }
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.responseCommentItemImage)
        ImageView image;

        @BindView(R.id.responseCommentItemName)
        TextView name;

        @BindView(R.id.responseCommentItemComment)
        TextView comment;

        @BindView(R.id.responseCommentItemDate)
        TextView date;

        @BindView(R.id.responseCommentItemClap)
        TextView clap;

        public CommentViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface CommentButtonClickListener {
        public void onCommentButtonClicked(CommentDescription desc, int position, CommentEventType type);
    }

    public enum CommentEventType{
        CONTACT, CLAP
    }
}
