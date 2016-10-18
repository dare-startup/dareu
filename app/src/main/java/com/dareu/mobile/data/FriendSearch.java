package com.dareu.mobile.data;

/**
 * Created by jose.rubalcaba on 10/15/2016.
 */

public class FriendSearch {
    private String id;
    private String name;

    private Integer dareCount;
    private Integer videoResponsesCount;

    public FriendSearch(){}
    public FriendSearch(final String id, final String name){
        this.id = id;
        this.name = name;
        this.dareCount = 0;
        this.videoResponsesCount = 0;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public Integer getDareCount() {
        return dareCount;
    }
    public void setDareCount(Integer dareCount) {
        this.dareCount = dareCount;
    }
    public Integer getVideoResponsesCount() {
        return videoResponsesCount;
    }
    public void setVideoResponsesCount(Integer videoResponsesCount) {
        this.videoResponsesCount = videoResponsesCount;
    }
}
