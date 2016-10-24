package com.dareu.mobile.utils;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public enum PropertyName {
    DEBUG_SERVER("server"),
    SIGNIN("signin"),
    SIGNUP("signup"),
    UPDATE_GCM_RE_ID("update.gcm"),
    CATEGORIES("categories"),
    CREATE_DARE("create.dare"),
    FIND_FRIENDS_BY_QUERY("find.friends"),
    GET_ACCOUNT_IMAGE("account.image");


    String value;
    PropertyName(String name){
        this.value = name;
    }

    @Override
    public String toString(){
        return this.value;
    }
}
