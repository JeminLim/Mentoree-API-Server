package com.mentoree.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {

    USER("ROLE_USER", "사용자"),
    MENTEE("ROLE_MENTEE", "멘티"),
    MENTOR("ROLE_MENTOR", "멘토"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String value;

}
