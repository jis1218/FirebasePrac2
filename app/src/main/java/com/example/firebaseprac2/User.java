package com.example.firebaseprac2;

/**
 * Created by 정인섭 on 2017-10-31.
 */

public class User {
    String userID;
    String token;

    public User(){

    }

    public User(String userID, String token) {
        this.userID = userID;
        this.token = token;
    }
}
