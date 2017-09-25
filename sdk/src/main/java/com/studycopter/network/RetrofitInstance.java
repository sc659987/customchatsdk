package com.studycopter.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Android on 9/23/2017.
 */
public class RetrofitInstance {

    private Retrofit retrofit;

    private StudyCopterService studyCopterServiceInstance;

    public RetrofitInstance(String baseUrl) {
        this.retrofit = new Retrofit.Builder().baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.studyCopterServiceInstance = retrofit.create(StudyCopterService.class);
    }


    public StudyCopterService getStudyCopterServiceInstance() {
        return this.studyCopterServiceInstance;
    }

}
