package com.dareu.mobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.data.FriendSearch;

import java.util.List;

/**
 * Created by jose.rubalcaba on 10/15/2016.
 */

public class FriendSearchAdapter extends RecyclerView.Adapter<FriendSearchAdapter.FriendSearchViewModel> {

    private List<FriendSearch> friends;
    private Context cxt;

    public FriendSearchAdapter(Context cxt, List<FriendSearch> list){
        this.cxt = cxt;
        this.friends = list;

    }


    @Override
    public FriendSearchViewModel onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cxt);
        View view = inflater.inflate(R.layout.friend_item, parent, false);
        FriendSearchViewModel vm = new FriendSearchViewModel(view);
        return vm;
    }

    @Override
    public void onBindViewHolder(FriendSearchViewModel holder, int position) {
        FriendSearch search = friends.get(position);
        holder.name.setText(search.getName());
        holder.dares.setText("Created dares\t" + search.getDareCount());
        holder.responses.setText("Uploaded responses\t" + search.getVideoResponsesCount());
        //TODO: load image from here in an async task
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    static class FriendSearchViewModel extends RecyclerView.ViewHolder{

        ImageView imageView;
        ProgressBar progressBar;
        TextView name, dares, responses;

        public FriendSearchViewModel(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.friendItemImage);
            progressBar = (ProgressBar)itemView.findViewById(R.id.friendItemProgressBar);
            name = (TextView)itemView.findViewById(R.id.friendItemName);
            dares = (TextView)itemView.findViewById(R.id.friendItemDares);
            responses = (TextView)itemView.findViewById(R.id.friendItemResponses);
        }
    }
}
