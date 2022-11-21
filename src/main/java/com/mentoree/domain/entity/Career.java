package com.mentoree.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.*;

@Getter
@NoArgsConstructor
@Entity
@ToString(exclude = "member")
@EqualsAndHashCode
public class Career extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "career_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private String companyName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String position;

    @Builder
    public Career(Member member, String companyName, LocalDate startDate, LocalDate endDate, String position) {
        this.member = member;
        this.companyName = companyName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
    }

    public void setMember(Member member) {
        if(this.member != null) {
            this.member.getCareers().remove(this);
        }
        this.member = member;
        member.getCareers().add(this);
    }

}
