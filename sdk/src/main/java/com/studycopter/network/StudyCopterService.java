package com.studycopter.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Android on 9/23/2017.
 */

public interface StudyCopterService {

    @FormUrlEncoded
    @POST("apnservices/device_auth_v3.php")
    Call<String> loginToStudyCopter(@Field("email") String email,
                                    @Field("pass") String pass,
                                    @Field("dvcid") String deviceId,
                                    @Field("os_type") String osType,
                                    @Field("course_app") String course_app);
}
