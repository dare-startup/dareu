package com.dareu.mobile.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.FriendSearchDescription;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

/**
 * Created by jose.rubalcaba on 03/21/2017.
 */

public class UserSmallAdapter extends RecyclerView.Adapter<UserSmallAdapter.UserViewHolder>{

    private List<FriendSearchDescription> descriptions;
    private UserSmallAdapterListener listener;

    public UserSmallAdapter(List<FriendSearchDescription> descriptions, UserSmallAdapterListener listener) {
        this.descriptions = descriptions;
        this.listener = listener;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_small_item, parent, false));
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, final int position) {

        //load image
        SharedUtils.loadImagePicasso(holder.image, holder.image.getContext(), descriptions.get(position).getImageUrl());

        //load name
        holder.name.setText(descriptions.get(position).getName());

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonClickListener(descriptions.get(position), position, UserSmallAdapterEventType.IMAGE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder{

        CircularImageView image;
        TextView name;

        public UserViewHolder(View itemView) {
            super(itemView);
            image = (CircularImageView)itemView.findViewById(R.id.userSmallImage);
            name = (TextView)itemView.findViewById(R.id.userSmallName);
        }
    }

    public interface UserSmallAdapterListener{
        public void onButtonClickListener(FriendSearchDescription description, int position, UserSmallAdapterEventType type);
    }

    public enum UserSmallAdapterEventType{
        IMAGE
    }
}
