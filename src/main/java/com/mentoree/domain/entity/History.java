package com.mentoree.domain.entity;

import lombok.Getter;

import javax.persistence.Embeddable;
import java.time.LocalDate;

@Embeddable
@Getter
public class History {
    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String position;

    public History() {
    }

    public History(String companyName, LocalDate startDate, LocalDate endDate, String position) {
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
    }
}
