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
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.client.AccountClientService;
import com.dareu.web.dto.client.factory.RetroFactory;
import com.dareu.web.dto.response.entity.DiscoverUserAccount;

import java.util.List;

/**
 * Created by jose.rubalcaba on 01/29/2017.
 */

public class DiscoverUsersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<DiscoverUserAccount> list;
    private Context cxt;
    private OnButtonClicked listener;
    private AccountClientService accountService;

    public DiscoverUsersAdapter(Context cxt, List<DiscoverUserAccount> list, OnButtonClicked listener){
        this.list = list;
        this.cxt = cxt;
        this.listener = listener;
        this.accountService = RetroFactory.getInstance()
                .create(AccountClientService.class);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        //a request has been sent
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discover_user_item, parent, false);
        return new DiscoverUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final DiscoverUsersViewHolder sent;
        //user ready to be added
        sent = (DiscoverUsersViewHolder)holder;
        sent.nameView.setText(list.get(position).getName());
        sent.dares.setText(String.valueOf(list.get(position).getDares()));
        sent.uploads.setText(String.valueOf(list.get(position).getResponses()));
        sent.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverUserAccount acc = list.get(position);
                list.remove(position);
                notifyItemRemoved(position);
                listener.onButtonClicked(acc, ButtonType.ADD);
            }
        });
        SharedUtils.loadImagePicasso(sent.imageView, cxt, list.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    static class DiscoverUsersViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView nameView;
        Button addButton;
        TextView uploads, dares;

        DiscoverUsersViewHolder(View view){
            super(view);
            this.imageView = (ImageView)view.findViewById(R.id.discoverUserItemImage);
            this.nameView = (TextView)view.findViewById(R.id.discoverUserItemName);
            this.addButton = (Button)view.findViewById(R.id.discoverUserItemConnect);
            this.uploads = (TextView)view.findViewById(R.id.discoverUserItemUploads);
            this.dares = (TextView)view.findViewById(R.id.discoverUserItemDares);
        }
    }

    public interface OnButtonClicked{
        public void onButtonClicked(DiscoverUserAccount account, ButtonType type);
    }

    public enum ButtonType{
        ADD
    }
}
