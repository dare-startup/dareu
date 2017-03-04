package com.dareu.mobile.net.request;

import java.io.File;
import java.io.InputStream;

/**
 * Created by jose.rubalcaba on 02/16/2017.
 */

public class UploadDareResponseRequest {
    private String dareid;
    private String comment;
    private InputStream thumbImage;
    private InputStream video;

    public UploadDareResponseRequest() {
    }

    public String getDareid() {
        return dareid;
    }

    public void setDareid(String dareid) {
        this.dareid = dareid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public InputStream getThumbImage() {
        return thumbImage;
    }

    public void setThumbImage(InputStream thumbImage) {
        this.thumbImage = thumbImage;
    }

    public InputStream getVideo() {
        return video;
    }

    public void setVideo(InputStream video) {
        this.video = video;
    }
}
