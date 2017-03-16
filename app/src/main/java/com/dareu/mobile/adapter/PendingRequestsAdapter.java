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

/**
 * Created by jose.rubalcaba on 03/10/2017.
 */

public class PendingRequestsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int SENT_VIEW_TYPE = 0;
    private static final int RECEIVED_VIEW_TYPE = 1;
    private static final int FOOTER_VIEW_TYPE = 2;

    private boolean sent;
    private Page<ConnectionRequest> requests;
    private ViewClickListener listener;
    private Context cxt;

    public PendingRequestsAdapter(Context cxt, Page<ConnectionRequest> requests, ViewClickListener listener, boolean sent) {
        this.cxt  =cxt;
        this.listener = listener;
        this.requests = requests;
        this.sent = sent;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == SENT_VIEW_TYPE)
            return new SentRequestViewHolder(LayoutInflater.from(cxt)
                                    .inflate(R.layout.sent_request_item, parent, false));
        else if(viewType == RECEIVED_VIEW_TYPE)
            return new ReceivedRequestViewHolder(LayoutInflater.from(cxt)
                                    .inflate(R.layout.received_request_item, parent, false));
        else return new FooterViewHolder(LayoutInflater.from(cxt)
                                    .inflate(R.layout.footer_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //check if is footer view
        if(position == requests.getItems().size()){
            FooterViewHolder footerViewHolder = (FooterViewHolder)holder;
            if(requests.getPageNumber() < requests.getPagesAvailable()){
                footerViewHolder.text.setText("Load more");
                footerViewHolder.text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO: load more here
                    }
                });
            }else{
                footerViewHolder.text.setText("There are no more items");
            }
        }else{
            final ConnectionRequest request = requests.getItems().get(position);
            if(sent){
                //sent requests adapter
                SentRequestViewHolder sentViewHolder = (SentRequestViewHolder)holder;
                sentViewHolder.cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.getItems().get(position), ButtonType.CANCEL);
                        requests.getItems().remove(position);
                        notifyItemRemoved(position);
                    }
                });
                sentViewHolder.name.setText(request.getUser().getName());
                SharedUtils.loadImagePicasso(sentViewHolder.image, cxt, request.getUser().getImageUrl());
                sentViewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.getItems().get(position), ButtonType.IMAGE);
                    }
                });
            }else{
                ReceivedRequestViewHolder receivedViewHolder = (ReceivedRequestViewHolder)holder;
                SharedUtils.loadImagePicasso(receivedViewHolder.image, cxt, request.getUser().getImageUrl());
                receivedViewHolder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.getItems().get(position), ButtonType.IMAGE);
                    }
                });
                receivedViewHolder.decline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.getItems().get(position), ButtonType.DECLINE);
                        requests.getItems().remove(position);
                        notifyItemRemoved(position);
                    }
                });
                receivedViewHolder.name.setText(request.getUser().getName());
                receivedViewHolder.accept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onViewClick(requests.getItems().get(position), ButtonType.ACCEPT);
                        requests.getItems().remove(position);
                        notifyItemRemoved(position);
                    }
                });
            }
        }

    }

    @Override
    public int getItemViewType(int position) {
        if(position == requests.getItems().size())
            return FOOTER_VIEW_TYPE;
        else if(sent)
            return SENT_VIEW_TYPE;
        else
            return RECEIVED_VIEW_TYPE;
    }

    @Override
    public int getItemCount() {
        return requests.getItems().size() + 1; //+1 for footer view
    }

    static class ReceivedRequestViewHolder extends RecyclerView.ViewHolder{

        ImageView image;
        TextView name;
        Button decline, accept;

        public ReceivedRequestViewHolder(View view) {
            super(view);
            image = (ImageView)view.findViewById(R.id.receivedRequestItemImage);
            name = (TextView)view.findViewById(R.id.receivedRequestItemName);
            decline = (Button)view.findViewById(R.id.receivedRequestItemDeclineButton);
            accept = (Button)view.findViewById(R.id.receivedRequestItemAcceptButton);
        }
    }

    static class SentRequestViewHolder extends RecyclerView.ViewHolder{
        ImageView image;
        TextView name;
        Button cancelButton;
        public SentRequestViewHolder(View view) {
            super(view);
            image = (ImageView)view.findViewById(R.id.sentRequestItemImage);
            name = (TextView)view.findViewById(R.id.sentRequestItemName);
            cancelButton = (Button)view.findViewById(R.id.sentRequestItemCancelButton);
        }
    }

    static class FooterViewHolder extends RecyclerView.ViewHolder{
        TextView text;
        public FooterViewHolder(View view) {
            super(view);
            text = (TextView)view.findViewById(R.id.message);
        }
    }

    public static interface ViewClickListener{
        public void onViewClick(ConnectionRequest request, ButtonType type);
    }

    public static enum ButtonType{
        ACCEPT, DECLINE, CANCEL, NAME, IMAGE
    }
}
