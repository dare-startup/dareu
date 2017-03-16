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

/**
 * Created by jose.rubalcaba on 03/03/2017.
 */

public class ResponseDescriptionAdapter extends RecyclerView.Adapter<ResponseDescriptionAdapter.ViewHolder> {

    private List<DareResponseDescription> descriptions;
    private Context cxt;
    private ResponseDescriptionCallbacks callback;

    public ResponseDescriptionAdapter(Context cxt, List<DareResponseDescription> page,
                                      ResponseDescriptionCallbacks callback) {
        this.cxt = cxt;
        this.descriptions = page;
        this.callback = callback;
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

        //set play listener
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to response activity, (oh god..)
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.PLAY);
            }
        });

        //set share listener
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.SHARE);
            }
        });

        //set image profile listener
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onButtonClicked(desc, position, ResponseDescriptionCallbackType.CONTACT);
            }
        });

        //set views
        holder.views.setText(String.valueOf(desc.getViews()));

        //set claps
        holder.claps.setText(String.valueOf(desc.getClaps()));

        //set creation date
        holder.date.setText(desc.getUploadDate());

        //TODO: set comments, update web service and data tables
        holder.comments.setText(String.valueOf(desc.getComments()));

        //set menu
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(cxt, holder.menu);
                popup.inflate(R.menu.dare_response_item_menu);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            //menu cases here
                        }
                        return false;
                    }
                });
                popup.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView thumb, user, share, play;
        TextView views, claps, comments, title, date, menu;

        public ViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageView)itemView.findViewById(R.id.dareResponseItemThumb);
            user = (ImageView)itemView.findViewById(R.id.dareResponseItemUser);
            share = (ImageView)itemView.findViewById(R.id.dareResponseItemShare);
            play = (ImageView)itemView.findViewById(R.id.dareResponseItemPlay);
            views = (TextView)itemView.findViewById(R.id.dareResponseItemViews);
            claps = (TextView)itemView.findViewById(R.id.dareResponseItemClaps);
            comments = (TextView)itemView.findViewById(R.id.dareResponseItemComments);
            title = (TextView)itemView.findViewById(R.id.dareResponseItemTitle);
            date = (TextView)itemView.findViewById(R.id.dareResponseItemDate);
            menu = (TextView)itemView.findViewById(R.id.dareResponseItemMenu);
        }
    }

    public interface ResponseDescriptionCallbacks{
        void onButtonClicked(DareResponseDescription description, int position, ResponseDescriptionCallbackType type);
    }

    public enum ResponseDescriptionCallbackType{
        SHARE, PLAY, CONTACT, MENU
    }
}
