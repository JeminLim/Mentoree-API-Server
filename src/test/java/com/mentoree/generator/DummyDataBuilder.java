package com.mentoree.generator;

import com.mentoree.domain.entity.*;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor
public class DummyDataBuilder {

    public Category generateCategory(String parent, String child) {
        Category parentCat = Category.builder().categoryName(parent).build();
        Category cat = Category.builder().categoryName(child).build();
        cat.setParent(parentCat);
        return cat;
    }

    public Program generateProgram(String dummy, Category category) {
        return Program.builder()
                .programName(dummy + "Program")
                .description(dummy + " program description")
                .price(10000)
                .dueDate(LocalDate.now().plusDays(7))
                .category(category)
                .maxMember(5)
                .build();
    }

    public Member generateMember(String dummy, UserRole role) {
        return Member.builder()
                .email(dummy + "@email.com")
                .nickname(dummy + "nickname")
                .username(dummy)
                .oAuthId("FORM")
                .role(role)
                .userPassword("1234Qwer!@")
                .build();
    }

    public Mentor generateMentor(Member member, Program program, boolean host) {
        Mentor mentor = Mentor.builder()
                .member(member)
                .host(host)
                .build();
        mentor.setProgram(program);
        return mentor;
    }

    public Mentee generateMentee(Member member, Program program) {
        return Mentee.builder().member(member).program(program)
                .build();
    }

    public Applicant generateApplicant(Member member, Program program, ProgramRole role) {
        return Applicant.builder()
                .member(member)
                .program(program)
                .role(role)
                .message(member.getNickname() + " test apply")
                .build();
    }


}
