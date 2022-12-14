package com.mentoree.domain.entity;

import lombok.*;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
@Table(name = "Applicants")
public class Applicant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicant_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @Enumerated(EnumType.STRING)
    @Column(name = "program_role")
    private ProgramRole role;

    private String message;
    private Boolean approval;

    @Builder
    public Applicant(Member member, Program program, ProgramRole role, String message) {
        this.member = member;
        this.program = program;
        this.role = role;
        this.message = message;
        this.approval = false;
    }
    //== 비지니스 로직 ==//
    public void approve() {
        this.approval = true;
    }
}
