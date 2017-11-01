package com.example.firebaseprac2;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by 정인섭 on 2017-11-01.
 */

public interface IRetro {
    //리턴타입 함수명(인자)
    @POST("sendNotification")
    Call<ResponseBody> sendNotification(@Body RequestBody postdata); //Annotation에 따라 어디에 담겨가는지가 정해진다.
    //"GET"을 설정하고 Query라고 하면 "sendNotification"에 /postdata 가 담기게 된다.

}
