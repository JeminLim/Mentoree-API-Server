package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.service.MemberService;
import com.mentoree.service.dto.MemberProfile;
import com.mentoree.service.dto.MemberSignUpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static com.mentoree.service.dto.MemberProfile.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberAPIController.class)
@AutoConfigureRestDocs
@AutoConfigureMockMvc
public class MemberAPIControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {

    }

    @Test
    @DisplayName("이메일 중복 체크")
    void duplicateEmailCheck() throws Exception {
        //given
        String testEmail = "memberA@email.com";
        //when
        when(memberService.duplicateEmailCheck(any())).thenReturn(false);

        //then
        mockMvc.perform(
                        get("/api/members/join/check/email")
                                .param("email", testEmail))
                .andExpect(status().isOk())
                .andDo(
                        document("get-member-email-check",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("email").description("Email that wants to check")
                                ),
                                responseFields(
                                        fieldWithPath("result").description("true - Duplicate email exists, false - Able to sign up")
                                )
                        )
                );
    }

    @Test
    @DisplayName("닉네임 중복 체크")
    void duplicateNicknameCheck() throws Exception {
        //given
        String testNickname = "memberA";
        //when
        when(memberService.duplicateNicknameCheck(any())).thenReturn(false);

        //then
        mockMvc.perform(
                        get("/api/members/join/check/nickname")
                                .param("nickname", testNickname))
                .andExpect(status().isOk())
                .andDo(
                        document("get-member-nickname-check",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("nickname").description("Nickname that wants to check")
                                ),
                                responseFields(
                                        fieldWithPath("result").description("true - Duplicate nickname exists," +
                                                " false - Able to sign up")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원 가입")
    void singUpTest() throws Exception {

        MemberSignUpRequest signUpRequest = MemberSignUpRequest.builder()
                .email("member@email.com")
                .password("1234QWer!@")
                .nickname("memberA")
                .build();

        doNothing().when(memberService).signUp(any());

        mockMvc.perform(
                        post("/api/members/join")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signUpRequest))
                ).andExpect(status().isCreated())
                .andDo(
                        document("post-member-join",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("email").description("User email"),
                                        fieldWithPath("password").description("User password"),
                                        fieldWithPath("nickname").description("User nickname")
                                )
                        )
                );
    }

    @Test
    @DisplayName("멤버 정보 열람")
    void getProfileTest() throws Exception {

        MemberProfile memberProfile = MemberProfile.builder().id(1L)
                .username("memberName")
                .nickname("memberNickname")
                .email("memberA@email.com")
                .careerList(Arrays.asList(new History("testCompany",
                        LocalDate.of(2022, 1, 20),
                        LocalDate.of(2022, 07, 18),
                        "Backend")))
                .build();

        when(memberService.getProfile(anyLong())).thenReturn(memberProfile);

        mockMvc.perform(
                        get("/api/members/profiles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("member.id").value(1L))
                .andExpect(jsonPath("member.email").value(memberProfile.getEmail()))
                .andDo(
                        document("get-member-profile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("Member primary key")
                                ), responseFields(
                                        fieldWithPath("member").description("Member information for request"),
                                        fieldWithPath("member.id").description("Member primary key"),
                                        fieldWithPath("member.email").description("Member email"),
                                        fieldWithPath("member.nickname").description("Member nickname"),
                                        fieldWithPath("member.username").description("Member name").optional(),
                                        fieldWithPath("member.careerList[]").description("Member career history").optional(),
                                        fieldWithPath("member.careerList[].companyName").description("company name"),
                                        fieldWithPath("member.careerList[].start").description("Career start date"),
                                        fieldWithPath("member.careerList[].end").description("Career end date"),
                                        fieldWithPath("member.careerList[].position").description("Duty of career")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로필 정보 업데이트")
    void updateProfileTest() throws Exception {

        MemberProfile memberProfile = MemberProfile.builder()
                .id(1L)
                .username("memberName")
                .nickname("newNickname")
                .email("memberA@email.com")
                .careerList(Arrays.asList(new History("newCompany",
                        LocalDate.of(2021, 1, 20),
                        LocalDate.of(2022, 7, 18),
                        "Backend")))
                .build();
        String requestBody = objectMapper.writeValueAsString(memberProfile);
        when(memberService.updateProfile(any())).thenReturn(memberProfile);

        mockMvc.perform(
                        post("/api/members/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("member.id").value(1L))
                .andExpect(jsonPath("member.nickname").value(memberProfile.getNickname()))
                .andDo(
                        document("post-member-profile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("id").description("Member primary key - unchangeable"),
                                        fieldWithPath("email").description("Member email - unchangeable"),
                                        fieldWithPath("nickname").description("New nickname"),
                                        fieldWithPath("username").description("Member name - one off change").optional(),
                                        fieldWithPath("careerList[]").description("New member career history").optional(),
                                        fieldWithPath("careerList[].companyName").description("New company name"),
                                        fieldWithPath("careerList[].start").description("New career start date"),
                                        fieldWithPath("careerList[].end").description("New career end date"),
                                        fieldWithPath("careerList[].position").description("New duty of career")
                                ), responseFields(
                                        fieldWithPath("member").description("Member information for request"),
                                        fieldWithPath("member.id").description("Member primary key"),
                                        fieldWithPath("member.email").description("Member email"),
                                        fieldWithPath("member.nickname").description("Member nickname"),
                                        fieldWithPath("member.username").description("Member name").optional(),
                                        fieldWithPath("member.careerList[]").description("Member career history").optional(),
                                        fieldWithPath("member.careerList[].companyName").description("company name"),
                                        fieldWithPath("member.careerList[].start").description("Career start date"),
                                        fieldWithPath("member.careerList[].end").description("Career end date"),
                                        fieldWithPath("member.careerList[].position").description("Duty of career")
                                )
                        )
                );


    }
}
