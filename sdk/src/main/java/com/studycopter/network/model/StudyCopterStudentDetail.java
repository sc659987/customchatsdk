package com.studycopter.network.model;

/**
 * Created by Android on 9/23/2017.
 */

public class StudyCopterStudentDetail {

    private String email;
    private String studentid;
    private String courseid;
    private String deviceid;
    private String coursevalidity;
    private String asked;
    private String balance;
    private String startdate;
    private String firebaseToken;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStudentid() {
        return studentid;
    }

    public void setStudentid(String studentid) {
        this.studentid = studentid;
    }

    public String getCourseid() {
        return courseid;
    }

    public void setCourseid(String courseid) {
        this.courseid = courseid;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public void setDeviceid(String deviceid) {
        this.deviceid = deviceid;
    }

    public String getCoursevalidity() {
        return coursevalidity;
    }

    public void setCoursevalidity(String coursevalidity) {
        this.coursevalidity = coursevalidity;
    }

    public String getAsked() {
        return asked;
    }

    public void setAsked(String asked) {
        this.asked = asked;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getFirebaseToken() {
        return firebaseToken;
    }

    public void setFirebaseToken(String firebaseToken) {
        this.firebaseToken = firebaseToken;
    }


}
