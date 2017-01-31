package com.dareu.mobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.web.dto.response.entity.DiscoverUserAccount;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

/**
 * Created by jose.rubalcaba on 01/29/2017.
 */

public class DiscoverUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiscoverUserAccount> list;
    private Context cxt;

    public DiscoverUsersAdapter(Context cxt, List<DiscoverUserAccount> list){
        this.list = list;
        this.cxt = cxt;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        DiscoverUsersViewHolder sent;
        DiscoverUsersPendingViewHolder received;
        switch(viewType){
            case 0:
                //a request has been received
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_users_pending_item, parent, false);
                received = new DiscoverUsersPendingViewHolder(view);
                return received;
            case 1:
                //a request has been sent
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_user_item, parent, false);
                sent = new DiscoverUsersViewHolder(view);
                return sent;
            case 2:
                //user ready to be added
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_user_item, parent, false);
                sent = new DiscoverUsersViewHolder(view);
                return sent;
            default:
                return null;
        }
    }

    @Override
    public int getItemViewType(int position) {
        DiscoverUserAccount account = list.get(position);
        if(account.isRequestReceived())
            return 0;
        else if(account.isRequestSent())
            return 1;
        else return 2;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        DiscoverUsersPendingViewHolder received;
        DiscoverUsersViewHolder sent;
        switch(holder.getItemViewType()){
            case 0:
                //a request has been received
                received = (DiscoverUsersPendingViewHolder)holder;
                //TODO:set image bitmap from here
                received.nameView.setText(list.get(position).getName());
                received.scoreView.setText(String.valueOf(list.get(position).getUscore()));
                received.coinsView.setText(String.valueOf(list.get(position).getCoins()));
                received.responsesView.setText(String.valueOf(list.get(position).getResponses()));
                received.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //set accepted to true and make an async call to rest service
                    }
                });
                received.declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //set accepted to false and make an async call to rest service
                    }
                });
                break;
            case 1:
                //a request has been sent
                sent = (DiscoverUsersViewHolder)holder;
                //TODO:set image bitmap from here
                sent.nameView.setText(list.get(position).getName());
                sent.scoreView.setText(String.valueOf(list.get(position).getUscore()));
                sent.coinsView.setText(String.valueOf(list.get(position).getCoins()));
                sent.responsesView.setText(String.valueOf(list.get(position).getResponses()));
                sent.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //creates an add friend async call here
                    }
                });
                sent.addButton.setVisibility(View.GONE);
                break;
            case 2:
                //user ready to be added
                sent = (DiscoverUsersViewHolder)holder;
                //TODO:set image bitmap from here
                sent.nameView.setText(list.get(position).getName());
                sent.scoreView.setText(String.valueOf(list.get(position).getUscore()));
                sent.coinsView.setText(String.valueOf(list.get(position).getCoins()));
                sent.responsesView.setText(String.valueOf(list.get(position).getResponses()));
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class DiscoverUsersViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView nameView;
        TextView scoreView;
        TextView coinsView;
        TextView responsesView;
        CircularImageView addButton;


        DiscoverUsersViewHolder(View view){
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.discoverUserItemImage);
            this.nameView = (TextView)view.findViewById(R.id.discoverUserItemName);
            this.scoreView = (TextView)view.findViewById(R.id.discoverUserItemScore);
            this.coinsView = (TextView)view.findViewById(R.id.discoverUserItemCoins);
            this.responsesView = (TextView)view.findViewById(R.id.discoverUserItemResponses);
            this.addButton = (CircularImageView)view.findViewById(R.id.discoverUserItemConnect);
        }
    }

    static class DiscoverUsersPendingViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView nameView;
        TextView scoreView;
        TextView coinsView;
        TextView responsesView;
        CircularImageView acceptButton, declineButton;

        DiscoverUsersPendingViewHolder(View view){
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.discoverUserPendingItemImage);
            this.nameView = (TextView)view.findViewById(R.id.discoverUserPendingItemName);
            this.scoreView = (TextView)view.findViewById(R.id.discoverUserPendingItemScore);
            this.coinsView = (TextView)view.findViewById(R.id.discoverUserPendingItemCoins);
            this.responsesView = (TextView)view.findViewById(R.id.discoverUserPendingItemResponses);
            this.acceptButton = (CircularImageView)view.findViewById(R.id.discoverUserPendingItemAccept);
            this.declineButton = (CircularImageView)view.findViewById(R.id.discoverUserPendingItemDecline);
        }
    }
}
