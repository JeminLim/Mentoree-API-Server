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

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @Embedded
    private History history;

    public Career(History history) {
        this.history = history;
    }

    public void setMember(Member member) {
        this.member = member;
        member.updateCareer(this);
    }
}
