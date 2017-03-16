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

/**
 * Created by jose.rubalcaba on 03/09/2017.
 */

public class ResponseCommentAdapter  extends RecyclerView.Adapter<ResponseCommentAdapter.CommentViewHolder>{

    private Context cxt;
    private List<CommentDescription> list;

    public ResponseCommentAdapter(Context cxt, List<CommentDescription> list){
        this.cxt = cxt;
        this.list = list;
    }


    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //create view
        View view = LayoutInflater.from(cxt)
                .inflate(R.layout.response_comment_tem, parent, false);

        return new CommentViewHolder(view);
    }

    public void add(CommentDescription description){
        list.add(description);
        notifyItemInserted(list.size() - 1);
    }

    @Override
    public void onBindViewHolder(final CommentViewHolder holder, int position) {
        CommentDescription description = list.get(position);
        //load image
        SharedUtils.loadImagePicasso(holder.image, cxt, description.getResponse().getUser().getImageUrl());
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: GO TO PROFILE ACTIVITY HERE
            }
        });
        //load text views
        holder.name.setText(description.getUser().getName());
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: GO TO PROFILE ACTIVITY
            }
        });

        holder.comment.setText(description.getComment());
        holder.date.setText(description.getCommentDate());
        holder.clap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: CLAP A COMMENT HERE
                holder.image.setImageDrawable(cxt.getResources().getDrawable(R.drawable.ic_thumb_orange));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name, comment, date, clap;

        public CommentViewHolder(View view) {
            super(view);
            image = (ImageView)view.findViewById(R.id.responseCommentItemImage);
            name = (TextView)view.findViewById(R.id.responseCommentItemName);
            comment = (TextView)view.findViewById(R.id.responseCommentItemComment);
            date = (TextView)view.findViewById(R.id.responseCommentItemDate);
            clap = (TextView)view.findViewById(R.id.responseCommentItemClap);
        }
    }
}
