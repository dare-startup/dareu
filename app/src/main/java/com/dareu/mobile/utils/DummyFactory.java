package com.dareu.mobile.utils;

import com.dareu.web.dto.response.entity.DiscoverUserAccount;
import com.dareu.web.dto.response.entity.FriendSearchDescription;
import com.dareu.web.dto.response.entity.CategoryDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by jose.rubalcaba on 10/15/2016.
 */

public class DummyFactory {
    /**public static List<FriendSearch> getFriendSearch(){
        List<FriendSearch> list = new ArrayList<>();
        for(int i = 0; i < 20; i ++){
            FriendSearch search = new FriendSearch(UUID.randomUUID().toString(), "Name " + i);
            search.setDareCount(i);
            search.setVideoResponsesCount(i);
            list.add(search);
        }
        return list;
    }**/


    public static List<DiscoverUserAccount> discoverUserAccounts(){
        List<DiscoverUserAccount> list = new ArrayList();
        DiscoverUserAccount acc;
        for(int i = 0; i < 10; i ++){
            acc = new DiscoverUserAccount("ID " + i, "Name " + i, i, i, "URL " + i);
            acc.setResponses(i);
            acc.setRequestReceived(i % 2 == 0);
            acc.setRequestSent(acc.isRequestReceived() ? false : true);
            acc.setDares(i);
            list.add(acc);
        }

        return list;
    }

    public static List<CategoryDescription> getCategories(){
        List<CategoryDescription> categories = new ArrayList<>();

        for(int i = 0; i < 5; i ++)
            categories.add(new CategoryDescription(UUID.randomUUID().toString(), "Name " + i, "Description " + i));

        return categories;
    }
}
