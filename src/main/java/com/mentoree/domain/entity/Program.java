package com.mentoree.domain.entity;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.Assert;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EqualsAndHashCode
public class Program extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private Long id;

    //변경 가능
    private String programName;
    private String description;
    private Integer maxMember;

    private Integer price;
    @Enumerated(EnumType.STRING)
    private ProgramState state;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "category_id")
    private Category category;
    @OneToMany(mappedBy = "program")
    List<Mentor> mentor = new ArrayList<>();

    @OneToMany(mappedBy = "program")
    List<Mentee> mentee = new ArrayList<>();
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    @Builder
    public Program(String programName, String description, Integer maxMember,
                   Integer price, Category category, LocalDate dueDate) {
        Assert.notNull(programName, "program name must not be null");
        Assert.notNull(description, "description must not be null");
        Assert.isTrue(maxMember > 0, "member limit must be greater than 0");

        this.programName = programName;
        this.description = description;
        this.maxMember = maxMember;
        this.price = price;
        this.dueDate = dueDate;
        this.category = category;
        this.state = ProgramState.OPEN;
    }

    //== 비지니스 로직 ==//
    public void update(String programName, String description, Integer maxMember,
                       Integer price, Category category, LocalDate dueDate) {
        this.programName = programName;
        this.description = description;
        this.maxMember = maxMember;
        this.price = price;
        this.dueDate = dueDate;
        this.category = category;
    }
}
