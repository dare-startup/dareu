package com.dareu.mobile.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.net.AsyncTaskListener;
import com.dareu.mobile.net.account.ConfirmConnectionTask;
import com.dareu.mobile.net.account.LoadProfileImageTask;
import com.dareu.mobile.net.account.RequestConnectionTask;
import com.dareu.web.dto.response.EntityRegistrationResponse;
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DiscoverUsersPendingViewHolder received;
        final DiscoverUsersViewHolder sent;
        LoadProfileImageTask imageTask;
        switch(holder.getItemViewType()){
            case 0:
                //a request has been received
                received = (DiscoverUsersPendingViewHolder)holder;
                received.nameView.setText(list.get(position).getName());
                received.scoreView.setText(String.valueOf(list.get(position).getUscore()));
                received.responsesView.setText(String.valueOf(list.get(position).getResponses()));
                received.acceptButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfirmConnectionTask task = new ConfirmConnectionTask(cxt, list.get(position).getId(), true, new AsyncTaskListener<EntityRegistrationResponse>() {
                            @Override
                            public void onTaskResponse(EntityRegistrationResponse response) {
                                //remove item from list
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, list.size());
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                        task.execute();
                    }
                });
                received.declineButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ConfirmConnectionTask task = new ConfirmConnectionTask(cxt, list.get(position).getId(), false, new AsyncTaskListener<EntityRegistrationResponse>() {
                            @Override
                            public void onTaskResponse(EntityRegistrationResponse response) {
                                //remove item from list
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, list.size());
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        });
                        task.execute();
                    }
                });
                imageTask = new LoadProfileImageTask(cxt, list.get(position).getId(), new AsyncTaskListener<Bitmap>() {
                    @Override
                    public void onTaskResponse(Bitmap response) {
                        if(response == null){
                            received.imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                        }else{
                            received.imageView.setImageBitmap(response);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
                imageTask.execute();
                break;
            case 1:
                //a request has been sent
                sent = (DiscoverUsersViewHolder)holder;
                //TODO:set image bitmap from here
                sent.nameView.setText(list.get(position).getName());
                sent.scoreView.setText(String.valueOf(list.get(position).getUscore()));
                sent.responsesView.setText(String.valueOf(list.get(position).getResponses()));
                sent.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //creates an add friend async call here
                    }
                });
                sent.addButton.setVisibility(View.GONE);
                imageTask = new LoadProfileImageTask(cxt, list.get(position).getId(), new AsyncTaskListener<Bitmap>() {
                    @Override
                    public void onTaskResponse(Bitmap response) {
                        if(response == null){
                            sent.imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                        }else{
                            sent.imageView.setImageBitmap(response);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                });
                imageTask.execute();
                break;
            case 2:
                //user ready to be added
                sent = (DiscoverUsersViewHolder)holder;
                sent.nameView.setText(list.get(position).getName());
                sent.scoreView.setText(String.valueOf(list.get(position).getUscore()));
                sent.responsesView.setText(String.valueOf(list.get(position).getResponses()));
                sent.addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new RequestConnectionTask(cxt, list.get(position).getId(), new AsyncTaskListener<EntityRegistrationResponse>() {
                            @Override
                            public void onTaskResponse(EntityRegistrationResponse response) {
                                list.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, list.size());
                            }

                            @Override
                            public void onError(String errorMessage) {

                            }
                        }).execute();
                    }
                });
                new LoadProfileImageTask(cxt, list.get(position).getId(), new AsyncTaskListener<Bitmap>() {
                    @Override
                    public void onTaskResponse(Bitmap response) {
                        if(response == null){
                            sent.imageView.setImageResource(R.drawable.ic_account_circle_black_24dp);
                        }else{
                            sent.imageView.setImageBitmap(response);
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {

                    }
                }).execute();

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
        TextView responsesView;
        ImageButton addButton;


        DiscoverUsersViewHolder(View view){
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.discoverUserItemImage);
            this.nameView = (TextView)view.findViewById(R.id.discoverUserItemName);
            this.scoreView = (TextView)view.findViewById(R.id.discoverUserItemScore);
            this.responsesView = (TextView)view.findViewById(R.id.discoverUserItemResponses);
            this.addButton = (ImageButton)view.findViewById(R.id.discoverUserItemConnect);
        }
    }

    static class DiscoverUsersPendingViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView nameView;
        TextView scoreView;
        TextView responsesView;
        ImageButton acceptButton, declineButton;

        DiscoverUsersPendingViewHolder(View view){
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.discoverUserPendingItemImage);
            this.nameView = (TextView)view.findViewById(R.id.discoverUserPendingItemName);
            this.scoreView = (TextView)view.findViewById(R.id.discoverUserPendingItemScore);
            this.responsesView = (TextView)view.findViewById(R.id.discoverUserPendingItemResponses);
            this.acceptButton = (ImageButton)view.findViewById(R.id.discoverUserPendingItemAccept);
            this.declineButton = (ImageButton)view.findViewById(R.id.discoverUserPendingItemDecline);
        }
    }
}
