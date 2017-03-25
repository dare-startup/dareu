package com.dareu.mobile.activity.decoration;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by jose.rubalcaba on 10/20/2016.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {

    public static final int EXTRA_LARGE_SPACE = 45;
    public static final int LARGE_SPACE = 30;
    public static final int MEDIUM_SPACE = 15;
    public static final int SMALL_SPACE = 5;
    public static final int NO_SPACE = 0;

    private int currentSpaceType = MEDIUM_SPACE;

    private boolean horizontal = false;

    public SpaceItemDecoration(){
    }

    public SpaceItemDecoration(int spaceType, boolean horizontal) {
        this.horizontal = horizontal;
        this.currentSpaceType = spaceType;
    }

    public SpaceItemDecoration(boolean horizontal) {
        this.horizontal = horizontal;
    }

    public SpaceItemDecoration(int spaceType){
        this.currentSpaceType = spaceType;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view) != parent.getAdapter().getItemCount() - 1){
            if(horizontal){
                outRect.right = currentSpaceType;
            }else{
                outRect.bottom = currentSpaceType;
            }
        }

    }
}
