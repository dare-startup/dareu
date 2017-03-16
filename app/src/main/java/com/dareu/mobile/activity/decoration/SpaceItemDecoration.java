package com.dareu.mobile.activity.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jose.rubalcaba on 10/20/2016.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    private int space = 30;

    public SpaceItemDecoration(){
    }

    public SpaceItemDecoration(int space){
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1)
            outRect.bottom = space;
    }
}
