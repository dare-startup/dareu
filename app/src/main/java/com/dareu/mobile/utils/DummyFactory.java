package com.dareu.mobile.utils;

import com.dareu.mobile.data.Category;
import com.dareu.mobile.data.FriendSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jose.rubalcaba on 10/15/2016.
 */

public class DummyFactory {
    public static List<FriendSearch> getFriendSearch(){
        List<FriendSearch> list = new ArrayList<>();
        for(int i = 0; i < 20; i ++){
            FriendSearch search = new FriendSearch(UUID.randomUUID().toString(), "Name " + i);
            search.setDareCount(i);
            search.setVideoResponsesCount(i);
            list.add(search);
        }
        return list;
    }


    public static List<Category> getCategories(){
        List<Category> categories = new ArrayList<>();

        for(int i = 0; i < 5; i ++)
            categories.add(new Category(UUID.randomUUID().toString(), "Name " + i, "Description " + i));

        return categories;
    }
}
