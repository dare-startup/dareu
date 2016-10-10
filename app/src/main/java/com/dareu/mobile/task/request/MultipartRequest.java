package com.dareu.mobile.task.request;

import java.net.URI;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public abstract class MultipartRequest {
    private URI file;

    public MultipartRequest(URI file) {
        this.file = file;
    }

    public URI getFile() {
        return file;
    }

    public void setFile(URI file) {
        this.file = file;
    }
}
