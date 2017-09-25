package com.studycopter.network;

import com.studycopter.network.model.StudyCopterStudentDetail;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Android on 9/23/2017.
 */

public interface StudyCopterService {


    @GET("apnservices/device_auth_v4.php")
    Call<StudyCopterStudentDetail> loginToStudyCopter(@Query("email") String email,
                                                      @Query("pass") String pass,
                                                      @Query("dvcid") String deviceId,
                                                      @Query("os_type") String osType,
                                                      @Query("course_app") String course_app);
}
