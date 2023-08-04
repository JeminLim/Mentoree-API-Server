package com.mentoree.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.api.mock.WithMockCustomUser;
import com.mentoree.config.WebConfig;
import com.mentoree.config.interceptors.AuthorityInterceptor;
import com.mentoree.config.security.JwtFilter;
import com.mentoree.config.security.SecurityConfig;
import com.mentoree.config.utils.JwtUtils;
import com.mentoree.config.utils.JwtUtilsImpl;
import com.mentoree.domain.entity.History;
import com.mentoree.service.MemberService;
import com.mentoree.service.dto.MemberProfileDto;
import com.mentoree.service.dto.MemberSignUpRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MemberApiController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {WebConfig.class, AuthorityInterceptor.class})
})
@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
public class MemberApiControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("이메일 중복 체크")
    @WithMockCustomUser
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
    @WithMockCustomUser
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
    @WithMockCustomUser
    void singUpTest() throws Exception {

        MemberSignUpRequestDto signUpRequest = MemberSignUpRequestDto.builder()
                .email("member@email.com")
                .password("1234QWer!@")
                .nickname("memberA")
                .build();

        doNothing().when(memberService).signUp(any());

        mockMvc.perform(
                        post("/api/members/join").with(csrf())
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
    @WithMockCustomUser
    void getProfileTest() throws Exception {

        MemberProfileDto memberProfileDto = MemberProfileDto.builder().id(1L)
                .username("memberName")
                .nickname("memberNickname")
                .email("memberA@email.com")
                .histories(List.of(new History("testCompany",
                        LocalDate.of(2022, 1, 20),
                        LocalDate.of(2022, 07, 18),
                        "Backend")))
                .build();

        when(memberService.getProfile(anyLong())).thenReturn(memberProfileDto);

        mockMvc.perform(
                        get("/api/members/profiles/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("member.id").value(1L))
                .andExpect(jsonPath("member.email").value(memberProfileDto.getEmail()))
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
                                        fieldWithPath("member.histories[]").description("Member career history").optional(),
                                        fieldWithPath("member.histories[].companyName").description("company name"),
                                        fieldWithPath("member.histories[].startDate").description("Career start date"),
                                        fieldWithPath("member.histories[].endDate").description("Career end date"),
                                        fieldWithPath("member.histories[].position").description("Duty of career")
                                )
                        )
                );
    }

    @Test
    @DisplayName("프로필 정보 업데이트")
    @WithMockCustomUser
    void updateProfileTest() throws Exception {

        MemberProfileDto memberProfileDto = MemberProfileDto.builder()
                .id(1L)
                .username("memberName")
                .nickname("newNickname")
                .email("memberA@email.com")
                .histories(List.of(new History("newCompany",
                        LocalDate.of(2021, 1, 20),
                        LocalDate.of(2022, 7, 18),
                        "Backend")))
                .build();
        String requestBody = objectMapper.writeValueAsString(memberProfileDto);
        when(memberService.updateProfile(any())).thenReturn(memberProfileDto);

        mockMvc.perform(
                        post("/api/members/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("member.id").value(1L))
                .andExpect(jsonPath("member.nickname").value(memberProfileDto.getNickname()))
                .andDo(
                        document("post-member-profile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("id").description("Member primary key - unchangeable"),
                                        fieldWithPath("email").description("Member email - unchangeable"),
                                        fieldWithPath("nickname").description("New nickname"),
                                        fieldWithPath("username").description("Member name - one off change").optional(),
                                        fieldWithPath("histories[]").description("New member career history").optional(),
                                        fieldWithPath("histories[].companyName").description("New company name"),
                                        fieldWithPath("histories[].startDate").description("New career start date"),
                                        fieldWithPath("histories[].endDate").description("New career end date"),
                                        fieldWithPath("histories[].position").description("New duty of career")
                                ), responseFields(
                                        fieldWithPath("member").description("Member information for request"),
                                        fieldWithPath("member.id").description("Member primary key"),
                                        fieldWithPath("member.email").description("Member email"),
                                        fieldWithPath("member.nickname").description("Member nickname"),
                                        fieldWithPath("member.username").description("Member name").optional(),
                                        fieldWithPath("member.histories[]").description("Member career history").optional(),
                                        fieldWithPath("member.histories[].companyName").description("company name"),
                                        fieldWithPath("member.histories[].startDate").description("Career start date"),
                                        fieldWithPath("member.histories[].endDate").description("Career end date"),
                                        fieldWithPath("member.histories[].position").description("Duty of career")
                                )
                        )
                );
    }

    @Test
    @DisplayName("멘토 회원 전환 요청")
    @WithMockCustomUser
    void memberTransformTest() throws Exception {

        MemberProfileDto memberProfileDto = MemberProfileDto.builder()
                .id(1L)
                .username("memberName")
                .nickname("newNickname")
                .email("memberA@email.com")
                .histories(List.of(new History("newCompany",
                        LocalDate.of(2021, 1, 20),
                        LocalDate.of(2022, 7, 18),
                        "Backend")))
                .build();
        String requestBody = objectMapper.writeValueAsString(memberProfileDto);
        doNothing().when(memberService).transformMember(any());

        mockMvc.perform(
                        post("/api/members/transform")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody).with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("result").value("success"))
                .andDo(
                        document("post-member-transform",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("id").description("Member primary key - unchangeable"),
                                        fieldWithPath("email").description("Member email - unchangeable"),
                                        fieldWithPath("nickname").description("New nickname"),
                                        fieldWithPath("username").description("Member name - one off change").optional(),
                                        fieldWithPath("histories[]").description("New member career history").optional(),
                                        fieldWithPath("histories[].companyName").description("New company name"),
                                        fieldWithPath("histories[].startDate").description("New career start date"),
                                        fieldWithPath("histories[].endDate").description("New career end date"),
                                        fieldWithPath("histories[].position").description("New duty of career")
                                ), responseFields(
                                        fieldWithPath("result").description("Result for request")
                                )
                        )
                );
    }

    @Test
    @DisplayName("회원 탈퇴 요청")
    @WithMockCustomUser
    void WithdrawTest() throws Exception {

        doNothing().when(memberService).withdrawMember(any());

        mockMvc.perform(
                        post("/api/members/withdraw/{id}", 1L).with(csrf()))
                .andExpect(status().isOk())
                .andDo(
                        document("post-member-withdraw",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("id").description("Id for request user to withdraw")
                                ),
                                responseFields(
                                        fieldWithPath("result").description("Result of request")
                                )
                        )
                );
    }


}
