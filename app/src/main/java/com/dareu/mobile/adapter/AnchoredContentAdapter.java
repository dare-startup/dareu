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

import butterknife.BindView;
import butterknife.ButterKnife;

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
        holder.date.setText(SharedUtils.getFromDate(desc.getContent().getUploadDate()));

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


        @BindView(R.id.dareResponseItemThumb)
        ImageView thumb;

        @BindView(R.id.dareResponseItemUser)
        ImageView user;

        @BindView(R.id.dareResponseItemShare)
        ImageView share;

        @BindView(R.id.dareResponseItemPlay)
        ImageView play;

        @BindView(R.id.dareResponseItemUnanchore)
        ImageView unanchor;

        @BindView(R.id.dareResponseItemThumbButton)
        ImageView thumbButton;

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

        public AnchoredContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface AnchoredButtonClickListener{
        void onAnchoredContentClick(AnchoredDescription desc, int position, AnchoredDescriptionCallbackType type);
    }

    public enum AnchoredDescriptionCallbackType{
        SHARE, PLAY, CONTACT, UNANCHOR, THUMB
    }
}
