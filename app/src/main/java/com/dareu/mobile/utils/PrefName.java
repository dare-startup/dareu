package com.dareu.mobile.utils;

/**
 * Created by jose.rubalcaba on 10/09/2016.
 */

public enum PrefName {
    SIGNIN_TOKEN("com.dareu.mobile.utils.SharedUtils.signinToken"),
    GCM_TOKEN("com.dareu.mobile.utils.SharedUtils.gcmToken"),
    FIRST_TIME("com.dareu.mobile.utils.SharedUtils.isFirstTimeHere"),
    CURRENT_ACTIVE_DARE("com.dareu.mobile.utils.SharedUtils.currentActiveDare"),
    ALREADY_REGISTERED_GCM_TOKEN("com.dareu.mobile.utils.SharedUtils.gcmAlreadyRegistered");

    String value;

    PrefName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.value;
    }
}
