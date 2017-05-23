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
import com.dareu.web.dto.response.entity.ConnectionRequest;
import com.dareu.web.dto.response.entity.Page;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by jose.rubalcaba on 03/10/2017.
 */

public class PendingRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int SENT_VIEW_TYPE = 0;
    private static final int RECEIVED_VIEW_TYPE = 1;

    private boolean sent;
    private List<ConnectionRequest> requests;
    private ViewClickListener listener;
    private Context cxt;

    public PendingRequestsAdapter(Context cxt, List<ConnectionRequest> requests, ViewClickListener listener, boolean sent) {
        this.cxt  = cxt;
        this.listener = listener;
        this.requests = requests;
        this.sent = sent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SENT_VIEW_TYPE)
            return new SentRequestViewHolder(LayoutInflater.from(cxt)
                                    .inflate(R.layout.sent_request_item, parent, false));
        else
            return new ReceivedRequestViewHolder(LayoutInflater.from(cxt)
                                    .inflate(R.layout.received_request_item, parent, false));

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //check if is footer view
            final ConnectionRequest request = requests.get(position);
            if(sent){
                //sent requests adapter
                SentRequestViewHolder sentViewHolder = (SentRequestViewHolder)holder;
                sentViewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.get(position), position, ButtonType.CANCEL, view);
                        requests.remove(position);
                        notifyItemRemoved(position);
                    }
                });
                sentViewHolder.name.setText(request.getUser().getName());
                SharedUtils.loadImagePicasso(sentViewHolder.image, cxt, request.getUser().getImageUrl());
                sentViewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.get(position), position, ButtonType.IMAGE, view);
                    }
                });
            }else{
                ReceivedRequestViewHolder receivedViewHolder = (ReceivedRequestViewHolder)holder;
                SharedUtils.loadImagePicasso(receivedViewHolder.image, cxt, request.getUser().getImageUrl());
                receivedViewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.get(position), position, ButtonType.IMAGE, view);
                    }
                });
                receivedViewHolder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.get(position), position, ButtonType.DECLINE, view);
                        requests.remove(position);
                        notifyItemRemoved(position);
                    }
                });
                receivedViewHolder.name.setText(request.getUser().getName());
                receivedViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.get(position), position, ButtonType.ACCEPT, view);
                        requests.remove(position);
                        notifyItemRemoved(position);
                    }
                });
            }
    }

    @Override
    public int getItemViewType(int position) {
        if(sent)
            return SENT_VIEW_TYPE;
        else
            return RECEIVED_VIEW_TYPE;
    }

    public void remove(int position){
        requests.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class ReceivedRequestViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.receivedRequestItemImage)
        ImageView image;

        @BindView(R.id.receivedRequestItemName)
        TextView name;

        @BindView(R.id.receivedRequestItemDeclineButton)
        Button decline;

        @BindView(R.id.receivedRequestItemAcceptButton)
        Button accept;

        public ReceivedRequestViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class SentRequestViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.sentRequestItemImage)
        ImageView image;

        @BindView(R.id.sentRequestItemName)
        TextView name;

        @BindView(R.id.sentRequestItemCancelButton)
        Button cancelButton;

        public SentRequestViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public static interface ViewClickListener{
        public void onViewClick(ConnectionRequest request, int position, ButtonType type, View view);
    }

    public static enum ButtonType{
        ACCEPT, DECLINE, CANCEL, NAME, IMAGE
    }
}
