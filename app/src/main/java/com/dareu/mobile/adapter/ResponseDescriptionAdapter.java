package com.dareu.mobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.DareResponseDescription;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 03/03/2017.
 */

public class ResponseDescriptionAdapter extends RecyclerView.Adapter<ResponseDescriptionAdapter.ViewHolder> {

    private List<DareResponseDescription> descriptions;
    private Context cxt;
    private ResponseDescriptionCallbacks callback;
    private ResponseType responseType;

    public ResponseDescriptionAdapter(Context cxt, List<DareResponseDescription> page,
                                      ResponseDescriptionCallbacks callback, ResponseType type) {
        this.cxt = cxt;
        this.descriptions = page;
        this.callback = callback;
        this.responseType = type;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dare_response_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final DareResponseDescription desc = descriptions.get(position);
        //set title
        holder.title.setText(desc.getDare().getName());

        //set thumb image
        SharedUtils.loadImagePicasso(holder.thumb, cxt, desc.getThumbUrl());

        //load user image
        SharedUtils.loadImagePicasso(holder.user, cxt, desc.getUser().getImageUrl());


        holder.thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.PLAY, holder.thumb);
            }
        });
        //set name listener
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to response activity, (oh god..)
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.PLAY, holder.title);
            }
        });

        //set play listener
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to response activity, (oh god..)
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.PLAY, holder.thumb);
            }
        });

        //set share listener
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.SHARE, holder.share);
            }
        });

        //set image profile listener
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.CONTACT, holder.user);
            }
        });

        holder.thumbUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.THUMB, holder.thumbUp);
            }
        });

        if(desc.isClapped())
            holder.thumbUp.setColorFilter(cxt.getResources().getColor(R.color.colorPrimary));
        else
            holder.thumbUp.setColorFilter(cxt.getResources().getColor(android.R.color.darker_gray));

        //set views
        holder.views.setText(String.valueOf(desc.getViews()));

        //set claps
        holder.claps.setText(String.valueOf(desc.getClaps()));

        //set creation date
        holder.date.setText(SharedUtils.getFromDate(desc.getUploadDate()));

        holder.comments.setText(String.valueOf(desc.getComments()));
    }

    public void clapResponse(int position, boolean clap){
        DareResponseDescription desc = descriptions.get(position);
        desc.setClapped(clap);
        desc.setClaps(clap ? desc.getClaps() + 1 : desc.getClaps() - 1);
        notifyItemChanged(position);

    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.dareResponseItemThumb)
        ImageView thumb;

        @BindView(R.id.dareResponseItemUser)
        ImageView user;

        @BindView(R.id.dareResponseItemShare)
        ImageView share;

        @BindView(R.id.dareResponseItemPlay)
        ImageView play;

        @BindView(R.id.dareResponseItemThumbButton)
        ImageView thumbUp;

        @BindView(R.id.dareResponseItemViews)
        TextView views;

        @BindView(R.id.dareResponseItemClaps)
        TextView claps;

        @BindView(R.id.dareResponseItemComments)
        TextView comments;

        @BindView(R.id.dareResponseItemTitle)
        TextView title;

        @BindView(R.id.dareResponseItemDate)
        TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    public interface ResponseDescriptionCallbacks{
        void onButtonClicked(DareResponseDescription description, int position, ResponseDescriptionCallbackType type, View view);
    }

    public enum ResponseDescriptionCallbackType{
        SHARE, PLAY, CONTACT, MENU, THUMB
    }

    public enum ResponseType{
        DEFAULT(0), ANCHORED(1), HOT(2);

        int value;
        ResponseType(int value){
            this.value = value;
        }
    }
}
