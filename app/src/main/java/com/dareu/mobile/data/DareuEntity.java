package com.dareu.mobile.data;

/**
 * Created by jose.rubalcaba on 10/12/2016.
 */

public abstract class DareuEntity {
    private String id;

    public DareuEntity(String id) {
        this.id = id;
    }

    public DareuEntity() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
