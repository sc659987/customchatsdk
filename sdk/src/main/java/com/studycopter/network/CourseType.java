package com.studycopter.network;

/**
 * Created by Android on 9/23/2017.
 */

public enum CourseType {

    IBPS("IBPS"), GMAT("GMAT "), RRB("RRB");

    private String typeName;

    CourseType(String typeName) {
        this.typeName = typeName;
    }

    @Override
    public String toString() {
        return this.typeName;
    }
}
