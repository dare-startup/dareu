package com.dareu.mobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.DareResponseDescription;

import java.util.List;

/**
 * Created by jose.rubalcaba on 03/21/2017.
 */

public class DareResponseSmallAdapter extends RecyclerView.Adapter<DareResponseSmallAdapter.ViewHolder>{

    private List<DareResponseDescription> descriptions;
    private SmallResponseDescriptionListener listener;


    public DareResponseSmallAdapter(List<DareResponseDescription> descriptions,
                                    SmallResponseDescriptionListener listener) {
        this.descriptions = descriptions;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.dare_response_small_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //load image
        SharedUtils.loadImagePicasso(holder.image, holder.image.getContext(), descriptions.get(position).getThumbUrl());

        //load name
        holder.name.setText(descriptions.get(position).getDare().getName());

        //set listener to button
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonClicked(descriptions.get(position), position, EventType.VIEW);
            }
        });
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        ImageButton view;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView)itemView.findViewById(R.id.responseSmallItemImage);
            name = (TextView)itemView.findViewById(R.id.responseSmallItemName);
            view = (ImageButton)itemView.findViewById(R.id.responseSmallItemView);
        }
    }

    public interface SmallResponseDescriptionListener{
        public void onButtonClicked(DareResponseDescription description, int position, EventType type);
    }

    public enum EventType{
        VIEW,
    }
}
