package com.dareu.mobile.task.request;

import android.net.Uri;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public abstract class MultipartRequest {
    private Uri file;

    public MultipartRequest(Uri file) {
        this.file = file;
    }

    public Uri getFile() {
        return file;
    }

    public void setFile(Uri file) {
        this.file = file;
    }
}
