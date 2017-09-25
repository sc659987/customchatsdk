package com.studycopter.network.responseprocessor;

import com.studycopter.network.model.StudyCopterStudentDetail;

/**
 * Created by Android on 9/23/2017.
 */

public class ResultProcessor {


    public static StudyCopterStudentDetail fromResponseString(String res) {

        System.out.println(res);
        StudyCopterStudentDetail detail = new StudyCopterStudentDetail();

        String resArray[] = res.split("&");
        for (String resItem : resArray) {
            if (resItem.contains("=")) {
                if (resItem.contains("email")) {
                    detail.setEmail(resItem.split("=")[1]);
                } else if (resItem.contains("studentid")) {
                    detail.setStudentid(resItem.split("=")[1]);
                } else if (resItem.contains("name")) {
                    detail.setName(resItem.split("=")[1]);
                } else if (resItem.contains("courseid")) {
                    detail.setCourseid(resItem.split("=")[1]);
                } else if (resItem.contains("deviceid")) {
                    detail.setDeviceid(resItem.split("=")[1]);
                } else if (resItem.contains("coursevalidity")) {
                    detail.setCoursevalidity(resItem.split("=")[1]);
                } else if (resItem.contains("asked")) {
                    detail.setAsked(resItem.split("=")[1]);
                } else if (resItem.contains("balance")) {
                    detail.setBalance(resItem.split("=")[1]);
                } else if (resItem.contains("startdate")) {
                    detail.setStartdate(resItem);
                } else if (resItem.contains("firebase_token")) {

                }
            }
        }

        return null;
    }
}
