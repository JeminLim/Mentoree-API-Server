package com.mentoree.domain.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Mission extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mission_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "program_id")
    private Program program;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "member_id")
    private Member writer;

    @Embedded
    private Writing writing;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public Mission(Program program, Member writer, Writing writing, LocalDate dueDate) {
        Assert.notNull(program, "program must not be null");
        Assert.notNull(dueDate, "dueDate must not be null");

        this.program = program;
        this.dueDate = dueDate;
        this.writer = writer;
        this.writing = writing;
    }
}
