package com.dareu.mobile.task;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public class GcmTask extends AsyncTask<Void, Void, Void> {

    private Context cxt;
    public GcmTask(Context cxt){
        this.cxt = cxt;
    }

    @Override
    protected Void doInBackground(Void... params) {


        return null;
    }
}
