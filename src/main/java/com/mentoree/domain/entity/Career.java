package com.mentoree.domain.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.*;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "career_table")
public class Career extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String companyName;
    private LocalDate start;
    private LocalDate end;
    private String position;

    @Builder
    public Career(Member member, String companyName, LocalDate start, LocalDate end, String position) {
        this.member = member;
        this.companyName = companyName;
        this.start = start;
        this.end = end;
        this.position = position;
    }

    public void addCareer(Member member) {
        this.member = member;
        member.addCareer(this);
    }

}
