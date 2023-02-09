package com.mentoree.config.utils;

import lombok.Getter;

@Getter
public enum ContentType {

    JPEG("jpeg", "image/jpeg"),
    JPG("jpg", "image/jpg"),
    PNG("png", "image/png"),
    GIF("gif", "image/gif"),

    ;

    private String type;
    private String contentType;

    ContentType(String type, String contentType) {
        this.type = type;
        this.contentType = contentType;
    }
}
