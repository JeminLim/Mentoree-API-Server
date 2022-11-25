package com.mentoree.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.*;

@Getter
@NoArgsConstructor
@Entity
public class Career {

    @Id @GeneratedValue
    @Column(name = "career_id")
    private Long id;
    private History history;

    public Career(History history) {
        this.history = history;
    }
}
