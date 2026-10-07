package com.example.sim_registration.mno;

public enum MnoNumberStatus {

    //Not yet assigned to anyone
    AVAILABLE,

    //Picked for a registration that is still in progress
    RESERVED,

    //Registration completed , number is now ACTIVE
    ASSIGNED
}
