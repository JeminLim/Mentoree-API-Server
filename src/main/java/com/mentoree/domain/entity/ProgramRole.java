package com.mentoree.domain.entity;

public enum ProgramRole {

    MENTOR("mentor"),
    MENTEE("mentee");

    private String value;

    ProgramRole(String value) { this.value = value;}

    public String getValue() {return value;}

}
