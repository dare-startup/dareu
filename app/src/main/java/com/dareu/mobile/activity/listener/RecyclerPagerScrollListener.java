package com.dareu.mobile.activity.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;

import com.dareu.web.dto.response.entity.Page;

/**
 * Created by jose.rubalcaba on 03/27/2017.
 */

public class RecyclerPagerScrollListener extends RecyclerView.OnScrollListener {

    private static final String TAG = "PageScroller";
    private boolean loading;
    private RecyclerViewPagerScrollListener listener;
    private int pageSize;
    private int pagesAvailable;
    private int pageNumber = 1; // default 1

    public RecyclerPagerScrollListener(RecyclerViewPagerScrollListener listener, int pageSize, int pagesAvailable){
        this.listener = listener;
        this.pageSize = pageSize;
        this.pagesAvailable = pagesAvailable;

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        LinearLayoutManager layoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        int visibleItemCount = layoutManager.getChildCount();
        int totalItemCount = layoutManager.getItemCount();
        int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

        if (!loading && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= pageSize) {
                listener.onScrolledToBottom();
            }
        }
    }

    private boolean isLastPage(){
        return pageNumber == pagesAvailable;
    }

    /**
     * This method must be called after a new page has been loaded
     * @param pageNumber
     */
    public void setPageNumber(int pageNumber){
        this.pageNumber = pageNumber;
    }

    /**
     * This must be called after a task has been executed
     * @param loading
     */
    public synchronized void setLoading(boolean loading){
        this.loading = loading;
    }


    public interface RecyclerViewPagerScrollListener {
        public void onScrolledToBottom();
    }
}
