package com.dareu.mobile.utils;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public enum PropertyName {
    DEBUG_SERVER("server"),
    DISCOVER_USERS("discover.users"),
    SIGNIN("signin"),
    SIGNUP("signup"),
    UPDATE_GCM_RE_ID("update.gcm"),
    CATEGORIES("categories"),
    CREATE_DARE("create.dare"),
    FIND_FRIENDS_BY_QUERY("find.friends"),
    FIND_DARE_DESCRIPTION("find.dare.description"),
    DARE_CONFIRMATION("new.dare.confirmation"),
    CONFIRM_CONNECTION("confirm.connection"),
    UPDATE_IMAGE_PROFILE("image.profile"),
    LOAD_IMAGE_PROFILE("load.image.profile"),
    CONNECTION_DETAILS("connection.details"),
    CONNECTION_REQUEST("connection.request"),
    UNACCEPTED_DARE("unaccepted.dare"),
    ACTIVE_DARE("active.dare"),
    FLAG_DARE("flag.dare"),
    UPLOAD_DARE_RESPONSE("upload.dare.response"),
    DARE_EXPIRATION("dare.expiration"),
    RESPONSE_THUMBNAIL("dare.response.thumb"),
    CHANNEL("dare.response.channel");


    String value;
    PropertyName(String name){
        this.value = name;
    }

    @Override
    public String toString(){
        return this.value;
    }
}
