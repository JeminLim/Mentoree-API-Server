package com.mentoree.domain.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProgramState {

    OPEN,
    HOLD_PAYMENT,
    TUTORING,
    HOLD_TERMINATION,
    END,
    WITHDRAWAL;
}
