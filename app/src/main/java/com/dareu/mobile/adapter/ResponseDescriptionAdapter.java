package com.dareu.mobile.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dareu.mobile.R;
import com.dareu.mobile.utils.PropertyName;
import com.dareu.mobile.utils.SharedUtils;
import com.dareu.web.dto.response.entity.DareResponseDescription;

import java.util.List;

/**
 * Created by jose.rubalcaba on 03/03/2017.
 */

public class ResponseDescriptionAdapter extends RecyclerView.Adapter<ResponseDescriptionAdapter.ViewHolder> {

    private List<DareResponseDescription> descriptions;
    private Context cxt;

    public ResponseDescriptionAdapter(Context cxt, List<DareResponseDescription> page) {
        this.cxt = cxt;
        this.descriptions = page;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dare_response_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DareResponseDescription desc = descriptions.get(position);
        //set title
        holder.title.setText(desc.getDare().getName());
        String host = SharedUtils.getProperty(PropertyName.DEBUG_SERVER, cxt);

        //get context path
        String path = SharedUtils.getProperty(PropertyName.RESPONSE_THUMBNAIL, cxt);

        //create url
        String thumbUrl = String.format(host + path, desc.getId());

        //set thumb image
        SharedUtils.loadImagePicasso(holder.thumb, cxt, thumbUrl);

        //get path
        path = SharedUtils.getProperty(PropertyName.LOAD_IMAGE_PROFILE, cxt);

        //create url
        String userUrl = host + path + desc.getUser().getId();

        //load user image
        SharedUtils.loadImagePicasso(holder.user, cxt, userUrl);

        //set play listener
        holder.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //set share listener
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //set image profile listener
        holder.user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return descriptions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView thumb, user, share, play;
        TextView views, claps, comments, title;

        public ViewHolder(View itemView) {
            super(itemView);
            thumb = (ImageView)itemView.findViewById(R.id.dareResponseItemThumb);
            user = (ImageView)itemView.findViewById(R.id.dareResponseItemUser);
            share = (ImageView)itemView.findViewById(R.id.dareResponseItemShare);
            play = (ImageView)itemView.findViewById(R.id.dareResponseItemPlay);
            views = (TextView)itemView.findViewById(R.id.dareResponseItemViews);
            claps = (TextView)itemView.findViewById(R.id.dareResponseItemClaps);
            comments = (TextView)itemView.findViewById(R.id.dareResponseItemComments);
            title = (TextView)itemView.findViewById(R.id.dareResponseItemTitle);
        }
    }
}
