package com.dareu.mobile.retro;

import com.dareu.web.dto.request.SigninRequest;
import com.dareu.web.dto.response.AuthenticationResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by jose.rubalcaba on 03/03/2017.
 */

public interface OpenService {

    @POST("security/authenticate")
    Call<AuthenticationResponse> signin(@Body SigninRequest request);
}
