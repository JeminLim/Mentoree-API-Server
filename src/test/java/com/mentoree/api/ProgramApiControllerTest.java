package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.domain.entity.*;
import com.mentoree.generator.DummyDataBuilder;
import com.mentoree.service.ProgramService;
import com.mentoree.service.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ProgramApiController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class ProgramApiControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ProgramService programService;

    DummyDataBuilder builder = new DummyDataBuilder();

    @Test
    @DisplayName("프로그램 생성 요청")
    void ProgramCreateTest() throws Exception {

        ProgramCreateRequestDto createRequest = ProgramCreateRequestDto.builder()
                .programName("testProgram")
                .price(10000)
                .dueDate(LocalDate.now().plusDays(5))
                .categoryName("JAVA")
                .description("test program description")
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequest);

        MentorDto responseBody = MentorDto.builder()
                .programId(1L)
                .memberId(1L)
                .host(true)
                .username("memberA")
                .programName("testProgram").build();

        when(programService.createProgram(anyLong(), any(ProgramCreateRequestDto.class))).thenReturn(responseBody);

        mockMvc.perform(
                        post("/api/programs/create")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("mentor.memberId").value(1L))
                .andExpect(jsonPath("mentor.programId").value(1L))
                .andExpect(jsonPath("mentor.programName").value("testProgram"))
                .andExpect(jsonPath("mentor.username").value("memberA"))
                .andExpect(jsonPath("mentor.host").value(true))
                .andDo(
                        document("post-program-create",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("programName").description("Program name to create"),
                                        fieldWithPath("description").description("Program curriculum"),
                                        fieldWithPath("price").description("Price for participating"),
                                        fieldWithPath("categoryName").description("Category for program"),
                                        fieldWithPath("dueDate").description("Recruitment due date"),
                                        fieldWithPath("maxMember").description("Target number of recruits")
                                ),
                                responseFields(
                                        fieldWithPath("mentor.memberId").description("Host member pk"),
                                        fieldWithPath("mentor.programId").description("Created program pk"),
                                        fieldWithPath("mentor.username").description("Host user name"),
                                        fieldWithPath("mentor.programName").description("Name of program"),
                                        fieldWithPath("mentor.host").description("Program host")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 수정 요청")
    void ProgramUpdateTest() throws Exception {
        //given
        ProgramCreateRequestDto updateRequest = ProgramCreateRequestDto.builder()
                .programName("newProgram")
                .price(15000)
                .dueDate(LocalDate.now().plusDays(5))
                .categoryName("JAVA")
                .description("test program description")
                .maxMember(5)
                .build();
        String requestBody = objectMapper.writeValueAsString(updateRequest);
        ProgramInfoDto responseBody = ProgramInfoDto.builder()
                .id(1L)
                .maxMember(5)
                .description(updateRequest.getDescription())
                .category(updateRequest.getCategoryName())
                .programName(updateRequest.getProgramName())
                .dueDate(updateRequest.getDueDate())
                .price(updateRequest.getPrice())
                .mentorList(List.of(MentorDto.builder()
                        .memberId(1L)
                        .programId(1L)
                        .username("memberA")
                        .programName(updateRequest.getProgramName())
                        .host(true).build())
                ).build();

        when(programService.updateProgram(anyLong(), any(ProgramCreateRequestDto.class))).thenReturn(responseBody);

        //when
        mockMvc.perform(
                        post("/api/programs/update/{programId}", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("program.programName").value("newProgram"))
                .andExpect(jsonPath("program.mentorList.size()").value(1))
                .andDo(
                        document("post-program-update",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("program id for update")
                                ),
                                requestFields(
                                        fieldWithPath("programName").description("Program name to update"),
                                        fieldWithPath("description").description("Program curriculum to update"),
                                        fieldWithPath("price").description("Price for participating"),
                                        fieldWithPath("categoryName").description("Category for program"),
                                        fieldWithPath("dueDate").description("Recruitment due date"),
                                        fieldWithPath("maxMember").description("Target number of recruits")
                                ),
                                responseFields(
                                        fieldWithPath("program.id").description("Updated program pk "),
                                        fieldWithPath("program.programName").description("Updated program name "),
                                        fieldWithPath("program.description").description("Updated program curriculum"),
                                        fieldWithPath("program.price").description("Updated price for participating"),
                                        fieldWithPath("program.category").description("Updated category for program"),
                                        fieldWithPath("program.dueDate").description("Updated recruitment due date"),
                                        fieldWithPath("program.maxMember").description("Updated target number of recruits"),
                                        fieldWithPath("program.mentorList[]").description("Mentor list of program"),
                                        fieldWithPath("program.mentorList[].memberId").description("Mentor's member id"),
                                        fieldWithPath("program.mentorList[].programId").description("Program id"),
                                        fieldWithPath("program.mentorList[].username").description("Mentor's name"),
                                        fieldWithPath("program.mentorList[].programName").description("Program title"),
                                        fieldWithPath("program.mentorList[].host").description("Whether mentor is host or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 참가 신청")
    void applyProgramTest() throws Exception {

        ProgramApplyDto applyRequest = ProgramApplyDto.builder()
                .role("MENTEE")
                .message("wanna apply")
                .programId(1L)
                .build();
        String requestBody = objectMapper.writeValueAsString(applyRequest);
        doNothing().when(programService).applyProgram(anyLong(), any(ProgramApplyDto.class));

        mockMvc.perform(
                        post("/api/programs/apply")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value("success"))
                .andDo(
                        document("post-program-apply",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("programId").description("Program pk"),
                                        fieldWithPath("message").description("Apply message"),
                                        fieldWithPath("role").description("Program role(MENTEE or MENTOR)")

                                ),
                                responseFields(
                                        fieldWithPath("result").description("Result of request")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 신청자 목록 요청")
    void getApplicantsTest() throws Exception {
        Member member = Member.builder()
                .email("test@email.com")
                .nickname("tester")
                .oAuthId("FORM")
                .userPassword("1234Qwer!@")
                .role(UserRole.MENTOR)
                .build();
        Program program = Program.builder()
                .price(10000)
                .programName("testProgram")
                .maxMember(4)
                .dueDate(LocalDate.now().plusDays(5))
                .category(Category.builder().categoryName("JAVA").build())
                .description("test program")
                .build();
        ReflectionTestUtils.setField(member, "id", 1L);
        ReflectionTestUtils.setField(program, "id", 1L);
        List<Applicant> applicantList = List.of(Applicant.builder()
                .program(program)
                .member(member)
                .message("Want to join in")
                .role(ProgramRole.MENTEE)
                .build());
        List<ApplicantDto> response = applicantList.stream().map(ApplicantDto::of).collect(Collectors.toList());

        when(programService.getApplicantList(anyLong())).thenReturn(response);

        mockMvc.perform(
                        get("/api/programs/applicants/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("applicants.size()").value(1))
                .andDo(
                        document("get-program-applicants",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("Program pk")
                                ),
                                responseFields(
                                        fieldWithPath("applicants[]").description("Applicant list"),
                                        fieldWithPath("applicants[].id").description("Applicant pk"),
                                        fieldWithPath("applicants[].nickname").description("Applicant nickname"),
                                        fieldWithPath("applicants[].role").description("Role that wants to participate as"),
                                        fieldWithPath("applicants[].message").description("Apply message")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 참가 신청 승인")
    void acceptApplicantTest() throws Exception {

        doNothing().when(programService).acceptApplicant(anyLong());

        mockMvc.perform(
                        post("/api/programs/applicants/accept/{applicantId}", 1L))
                .andExpect(status().isOk())
                .andDo(
                        document("post-program-applicant-accept",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("applicantId").description("applicant pk for accept request")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 참가 신청 거절")
    void rejectApplicantTest() throws Exception {

        doNothing().when(programService).rejectApplicant(anyLong());

        mockMvc.perform(
                        delete("/api/programs/applicants/reject/{applicantId}", 1L))
                .andExpect(status().isOk())
                .andDo(
                        document("delete-program-applicant-reject",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("applicantId").description("applicant pk for reject request")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로ㅡ램 정보 열람")
    void getProgramInfoTest() throws Exception {
        Member member = builder.generateMember("memberA", UserRole.MENTOR);
        Category category = builder.generateCategory("Programming", "JAVA");
        Program program = builder.generateProgram("test", category);
        ReflectionTestUtils.setField(program, "id", 1L);
        Mentor mentor = builder.generateMentor(member, program, true);
        ReflectionTestUtils.setField(program, "mentor", List.of(mentor));

        ProgramInfoDto expectResponse = ProgramInfoDto.of(program);

        when(programService.getProgramInfo(anyLong())).thenReturn(expectResponse);

        mockMvc.perform(
                        get("/api/programs/{programId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("program.id").value(1))
                .andExpect(jsonPath("program.programName").value("testProgram"))
                .andDo(
                        document("delete-program-applicant-reject",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Target program id")
                                ),
                                responseFields(
                                        fieldWithPath("program.id").description("Program pk"),
                                        fieldWithPath("program.programName").description("Program title"),
                                        fieldWithPath("program.description").description("Program description"),
                                        fieldWithPath("program.maxMember").description("Program maximum number of mentee"),
                                        fieldWithPath("program.price").description("Program price to join"),
                                        fieldWithPath("program.dueDate").description("Program recruitment period"),
                                        fieldWithPath("program.category").description("Program's category"),
                                        fieldWithPath("program.mentorList[]").description("Mentor list of program"),
                                        fieldWithPath("program.mentorList[].memberId").description("Mentor member pk"),
                                        fieldWithPath("program.mentorList[].programId").description("Program pk"),
                                        fieldWithPath("program.mentorList[].username").description("Mentor name"),
                                        fieldWithPath("program.mentorList[].programName").description("Program title"),
                                        fieldWithPath("program.mentorList[].host").description("Whether mentor is host or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 중도 폐지 신청")
    void withdrawProgramTest() throws Exception {

        doNothing().when(programService).withdrawProgram(anyLong());

        mockMvc.perform(
                        post("/api/programs/withdraw/{programId}", 1L))
                .andExpect(status().isOk())
                .andDo(
                        document("post-program-withdraw",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Program pk to withdraw")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 리스트 요청 - 전체")
    void getProgramListNoFilter() throws Exception {

        List<Program> dummyPrograms = generateProgramList();
        List<ProgramInfoDto> infoDtos = dummyPrograms.stream().map(ProgramInfoDto::of)
                .limit(8).collect(Collectors.toList());
        Map<String, Object> expected = new HashMap<>();
        expected.put("programList", infoDtos);
        expected.put("next", true);

        when(programService.getProgramList(any(), any(), any(), any())).thenReturn(expected);

        mockMvc.perform(
                    get("/api/programs/list")
                        .param("maxId", "0")
                        .param("minId", "0")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("programList.size()").value(8))
                .andDo(
                        document("get-program-list-nonefilter",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("maxId").description("Maximum id for program list"),
                                        parameterWithName("minId").description("Minimum id for program list")
                                ),
                                responseFields(
                                        fieldWithPath("programList[]").description("Program list"),
                                        fieldWithPath("programList[].id").description("Program pk"),
                                        fieldWithPath("programList[].programName").description("Program title"),
                                        fieldWithPath("programList[].description").description("Program description"),
                                        fieldWithPath("programList[].maxMember").description("Program maximum number of mentee"),
                                        fieldWithPath("programList[].price").description("Program price to join"),
                                        fieldWithPath("programList[].dueDate").description("Program recruitment period"),
                                        fieldWithPath("programList[].category").description("Program's category"),
                                        fieldWithPath("programList[].mentorList[]").description("Mentor list of program"),
                                        fieldWithPath("programList[].mentorList[].memberId").description("Mentor member pk"),
                                        fieldWithPath("programList[].mentorList[].programId").description("Program pk"),
                                        fieldWithPath("programList[].mentorList[].username").description("Mentor name"),
                                        fieldWithPath("programList[].mentorList[].programName").description("Program title"),
                                        fieldWithPath("programList[].mentorList[].host").description("Whether mentor is host or not"),
                                        fieldWithPath("next").description("Whether data more exists or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 리스트 요청 - 대분류")
    void getProgramListFirstFilter() throws Exception {

        List<Program> dummyPrograms = generateProgramList();
        List<ProgramInfoDto> infoDtos = dummyPrograms.stream()
                .filter(p -> p.getCategory().getParent().getCategoryName().equals("Programming"))
                .map(ProgramInfoDto::of).collect(Collectors.toList());

        Map<String, Object> expected = new HashMap<>();
        expected.put("programList", infoDtos);
        expected.put("next", true);

        when(programService.getProgramList(any(), any(),any(),any())).thenReturn(expected);

        mockMvc.perform(
                        get("/api/programs/list")
                        .param("maxId", "0")
                        .param("minId", "0")
                        .param("first", "Programming")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("programList.size()").value(5))
                .andDo(
                        document("get-program-list-firstfilter",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("maxId").description("Maximum id for program list"),
                                        parameterWithName("minId").description("Minimum id for program list"),
                                        parameterWithName("first").description("First category to filtering")
                                ),
                                responseFields(
                                        fieldWithPath("programList[]").description("Program list"),
                                        fieldWithPath("programList[].id").description("Program pk"),
                                        fieldWithPath("programList[].programName").description("Program title"),
                                        fieldWithPath("programList[].description").description("Program description"),
                                        fieldWithPath("programList[].maxMember").description("Program maximum number of mentee"),
                                        fieldWithPath("programList[].price").description("Program price to join"),
                                        fieldWithPath("programList[].dueDate").description("Program recruitment period"),
                                        fieldWithPath("programList[].category").description("Program's category"),
                                        fieldWithPath("programList[].mentorList[]").description("Mentor list of program"),
                                        fieldWithPath("programList[].mentorList[].memberId").description("Mentor member pk"),
                                        fieldWithPath("programList[].mentorList[].programId").description("Program pk"),
                                        fieldWithPath("programList[].mentorList[].username").description("Mentor name"),
                                        fieldWithPath("programList[].mentorList[].programName").description("Program title"),
                                        fieldWithPath("programList[].mentorList[].host").description("Whether mentor is host or not"),
                                        fieldWithPath("next").description("Whether data more exists or not")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로그램 리스트 요청 - 소분류")
    void getProgramListSecondFilter() throws Exception {

        List<Program> dummyPrograms = generateProgramList();
        List<ProgramInfoDto> infoDtos = dummyPrograms.stream()
                .filter(p -> p.getCategory().getCategoryName().equals("JAVA"))
                .map(ProgramInfoDto::of).collect(Collectors.toList());
        Map<String, Object> expected = new HashMap<>();
        expected.put("programList", infoDtos);
        expected.put("next", true);

        when(programService.getProgramList(any(), any(), any(), any())).thenReturn(expected);

        mockMvc.perform(
                        get("/api/programs/list")
                            .param("maxId", "0")
                            .param("minId", "0")
                            .param("first", "Programming")
                            .param("second", "JAVA")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("programList.size()").value(3))
                .andDo(
                        document("get-program-list-secondfilter",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("maxId").description("Maximum id for program list"),
                                        parameterWithName("minId").description("Minimum id for program list"),
                                        parameterWithName("first").description("First category to filtering"),
                                        parameterWithName("second").description("Second categories to filtering, maximum 5 types")
                                ),
                                responseFields(
                                        fieldWithPath("programList[]").description("Program list"),
                                        fieldWithPath("programList[].id").description("Program pk"),
                                        fieldWithPath("programList[].programName").description("Program title"),
                                        fieldWithPath("programList[].description").description("Program description"),
                                        fieldWithPath("programList[].maxMember").description("Program maximum number of mentee"),
                                        fieldWithPath("programList[].price").description("Program price to join"),
                                        fieldWithPath("programList[].dueDate").description("Program recruitment period"),
                                        fieldWithPath("programList[].category").description("Program's category"),
                                        fieldWithPath("programList[].mentorList[]").description("Mentor list of program"),
                                        fieldWithPath("programList[].mentorList[].memberId").description("Mentor member pk"),
                                        fieldWithPath("programList[].mentorList[].programId").description("Program pk"),
                                        fieldWithPath("programList[].mentorList[].username").description("Mentor name"),
                                        fieldWithPath("programList[].mentorList[].programName").description("Program title"),
                                        fieldWithPath("programList[].mentorList[].host").description("Whether mentor is host or not"),
                                        fieldWithPath("next").description("Whether data more exists or not")
                                )
                        )
                );
    }

    private List<Program> generateProgramList() {
        Member member = builder.generateMember("member", UserRole.MENTOR);

        int num = 1;
        List<Program> list = new ArrayList<>();
        Category javaCategory = builder.generateCategory("Programming", "JAVA");
        while(num <= 3) {
            Program program = builder.generateProgram("test" + num, javaCategory);
            builder.generateMentor(member, program, true);
            list.add(program);
            num++;
        }

        Category pyCategory = builder.generateCategory("Programming", "PYTHON");
        while(num <= 5) {
            Program program = builder.generateProgram("test" + num, pyCategory);
            builder.generateMentor(member, program, true);
            list.add(program);
            num++;
        }

        Category lifeCategory = builder.generateCategory("Music", "GUITAR");
        while(num <= 10) {
            Program program = builder.generateProgram("test" + num, lifeCategory);
            builder.generateMentor(member, program, true);
            list.add(program);
            num++;
        }

        return list;
    }

}
