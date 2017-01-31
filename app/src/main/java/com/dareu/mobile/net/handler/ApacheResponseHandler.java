package com.dareu.mobile.net.handler;

import com.dareu.mobile.net.response.ApacheResponseWrapper;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by jose.rubalcaba on 01/27/2017.
 */

public class ApacheResponseHandler implements ResponseHandler<ApacheResponseWrapper> {

    private String url;

    public ApacheResponseHandler(String contextPath){
        this.url = contextPath;
    }

    @Override
    public ApacheResponseWrapper handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
        ApacheResponseWrapper wrapper = new ApacheResponseWrapper();
        //status code
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        wrapper.setStatusCode(statusCode);

        //entity
        HttpEntity entity = httpResponse.getEntity();

        if(entity != null){
            String json = EntityUtils.toString(entity);
            wrapper.setJsonResponse(json);
        }else wrapper.setJsonResponse("");

        //url
        wrapper.setUrl(url);

        return wrapper;
    }
}
