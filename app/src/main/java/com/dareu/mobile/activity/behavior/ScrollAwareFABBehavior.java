package com.dareu.mobile.activity.behavior;

import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Created by jose.rubalcaba on 03/27/2017.
 */

public class ScrollAwareFABBehavior  extends FloatingActionButton.Behavior{

    public ScrollAwareFABBehavior() {
        super();
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton button, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed){
        super.onNestedScroll(coordinatorLayout, button, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
        if(dyConsumed > 0 && button.getVisibility() == View.VISIBLE)
            button.hide();
        else if(dyConsumed < 0 && button.getVisibility() == View.GONE)
            button.show();
    }


    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, FloatingActionButton child, View directTargetChild, View target, int nestedScrollAxes) {
        return nestedScrollAxes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }
}
