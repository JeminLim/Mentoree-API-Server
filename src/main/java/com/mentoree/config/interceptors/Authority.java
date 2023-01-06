package com.mentoree.config.interceptors;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Authority {

    Role role() default Role.PARTICIPANT;

    @Getter
    @RequiredArgsConstructor
    enum Role {
        PARTICIPANT("PARTICIPANT"), // 요구하는 미션, 보드를 타고 엔티티 그래프로 탐색
        WRITER("WRITER"), // WRITER => Mission OR board 요청 판별 => WRITER ID == login ID
        MENTOR("MENTOR"), // MISSION => 해당 참가 + 멘토 회원
        HOST("HOST"), // HOST => ProgramId, MemberId 를 통한 Mentor 검색 후 host 여부 판단
        ADMIN("ADMIN");

        private final String key;
    }
}
