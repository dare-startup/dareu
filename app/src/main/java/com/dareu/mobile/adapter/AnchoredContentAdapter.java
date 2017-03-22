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
import com.dareu.web.dto.response.entity.AnchoredDescription;
import com.dareu.web.dto.response.entity.DareResponseDescription;

import java.util.List;

/**
 * Created by jose.rubalcaba on 03/20/2017.
 */

public class AnchoredContentAdapter extends RecyclerView.Adapter<AnchoredContentAdapter.AnchoredContentViewHolder>{

    private List<AnchoredDescription> descriptions;
    private AnchoredButtonClickListener listener;
    private Context cxt;

    public AnchoredContentAdapter(List<AnchoredDescription> list, Context cxt,
                                  AnchoredButtonClickListener listener) {
        this.descriptions = list;
        this.listener = listener;
        this.cxt = cxt;
    }

    @Override
    public AnchoredContentViewHolder onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anchored_content_item, parent, false);
        return new AnchoredContentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AnchoredContentViewHolder holder, final int position) {
        final AnchoredDescription desc = descriptions.get(position);
        //set title
        holder.title.setText(desc.getContent().getDare().getName());

        //set thumb image
        SharedUtils.loadImagePicasso(holder.thumb, cxt, desc.getContent().getThumbUrl());

        //load user image
        SharedUtils.loadImagePicasso(holder.user, cxt, desc.getContent().getUser().getImageUrl());

        if(desc.getContent().isClapped()){
            holder.thumbButton.setColorFilter(cxt.getResources().getColor(R.color.colorPrimary));
        }else{
            holder.thumbButton.setColorFilter(cxt.getResources().getColor(android.R.color.darker_gray));
        }
        //set thumb up listener
        holder.thumbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAnchoredContentClick(desc, position, AnchoredDescriptionCallbackType.THUMB);
            }
        });

        //set play listener
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //go to response activity, (oh god..)
                listener.onAnchoredContentClick(desc, position, AnchoredDescriptionCallbackType.PLAY);
            }
        });

        //set share listener
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnchoredContentClick(desc, position, AnchoredDescriptionCallbackType.SHARE);
            }
        });

        //set image profile listener
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onAnchoredContentClick(desc, position, AnchoredDescriptionCallbackType.CONTACT);
            }
        });

        holder.unanchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onAnchoredContentClick(desc, position, AnchoredDescriptionCallbackType.UNANCHOR);
            }
        });
        //set views
        holder.views.setText(String.valueOf(desc.getContent().getViews()));

        //set claps
        holder.claps.setText(String.valueOf(desc.getContent().getClaps()));

        //set creation date
        holder.date.setText(desc.getContent().getUploadDate());

        //set comments
        holder.comments.setText(String.valueOf(desc.getContent().getComments()));
    }

    public void remove(int position){
        descriptions.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    public void clapResponse(int position, boolean clap){
        AnchoredDescription desc = descriptions.get(position);
        desc.getContent().setClapped(clap);
        desc.getContent().setClaps(clap ? desc.getContent().getClaps() + 1 : desc.getContent().getClaps() - 1);
        notifyItemChanged(position);

    }

    static class AnchoredContentViewHolder extends RecyclerView.ViewHolder{

        ImageView thumb, user, share, play, unanchor, thumbButton;
        TextView views, claps, comments, title, date;

        public AnchoredContentViewHolder(View itemView) {
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
            unanchor = (ImageView)itemView.findViewById(R.id.dareResponseItemUnanchore);
            thumbButton = (ImageView)itemView.findViewById(R.id.dareResponseItemThumbButton);
        }
    }

    public interface AnchoredButtonClickListener{
        void onAnchoredContentClick(AnchoredDescription desc, int position, AnchoredDescriptionCallbackType type);
    }

    public enum AnchoredDescriptionCallbackType{
        SHARE, PLAY, CONTACT, UNANCHOR, THUMB
    }
}
