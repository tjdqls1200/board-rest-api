package com.fivefingers.boardrestapi.controller;

import com.fivefingers.boardrestapi.config.SecurityConfig;
import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.service.MemberService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = MemberController.class,
        excludeAutoConfiguration = SecurityAutoConfiguration.class,
        excludeFilters = {@ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = SecurityConfig.class
                )
        })
class MemberControllerTest {

    // 스프링 부트가 제공하는 @WebMvcTest를 사용하면 스프링이 직접 MockMvc 생성
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private Gson gson;

    @BeforeEach
    public void init() {

        //mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }


    @DisplayName("회원 가입 성공시 가입 정보 반환 HTTP Created(201)")
    @Test
    public void join() throws Exception {
        //given
        CreateMemberDto createMemberDto = CreateMemberDto.builder()
                .loginId("mockTest")
                .password("MockTest123!")
                .username("mockname")
                .build();
        Member member = Member.builder()
                .loginId("mockTest")
                .password("MockTest123!")
                .username("mockname")
                .build();

        given(memberService.join(any(CreateMemberDto.class))).willReturn(1L);
        given(memberService.findOne(anyLong())).willReturn(member);

        //when
        ResultActions result = mockMvc
                .perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(createMemberDto)));

        //then
        ResponseMemberDto responseMemberDto = ResponseMemberDto.from(member);
        ResponseMemberDto.builder().loginId("mockTest").username("mockname").build();
        result.andExpect(status().isCreated())
                .andExpect(jsonPath("$.loginId").value(responseMemberDto.getLoginId()))
                .andExpect(jsonPath("$.username").value(responseMemberDto.getUsername()))
                .andDo(print());
    }


    @DisplayName("단일 회원 조회 성공 HTTP OK(200)")
    @Test
    public void readMember() throws Exception {
        //given
        Member member = Member.builder()
                .loginId("mockTest")
                .password("MockTest123!")
                .username("mockname")
                .build();
        given(memberService.findOne(anyLong())).willReturn(member);

        //when
        ResultActions result = mockMvc.perform(get("/api/v1/members/1"));

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.loginId").value("mockTest"))
                .andExpect(jsonPath("$.username").value("mockname"))
                .andDo(print());
    }

    @DisplayName("회원 리스트 조회 성공 HTTP OK(200)")
    @Test
    public void readMemberList() throws Exception {
        //given
        Member member = Member.builder()
                .loginId("mockTest1")
                .password("MockTest123!")
                .username("mockname1")
                .build();
        List<Member> memberList = new ArrayList<>();
        memberList.add(member);

        given(memberService.findAll()).willReturn(memberList);
        WrappedList<List<ReadMemberDto>> response = new WrappedList<>(memberList.stream()
                .map(ReadMemberDto::from)
                .collect(Collectors.toList()));
        //when
        ResultActions result = mockMvc.perform(get("/api/v1/members"));

        //then
        result
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.list").isArray())
                .andDo(print());
    }

    @DisplayName("회원 정보 변경 완료시 HTTP OK(200)")
    @Test
    public void update() throws Exception {
        //given
        UpdateMemberDto updateMemberDto = new UpdateMemberDto("testPass123!", "testName");
        given(memberService.update(anyLong(), any(UpdateMemberDto.class))).willReturn(true);

        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updateMemberDto)));

        //then
        result
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("회원 정보 변경 사항이 없는 경우 HTTP NO_CONTENT(204)")
    @Test
    public void notUpdate() throws Exception {
        //given
        UpdateMemberDto updateMemberDto = new UpdateMemberDto("testPass123!", "testName");
        given(memberService.update(anyLong(), any(UpdateMemberDto.class))).willReturn(false);

        //when
        ResultActions result = mockMvc.perform(patch("/api/v1/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(updateMemberDto)));

        //then
        result.andExpect(status().isNoContent());
    }

    @DisplayName("회원 삭제 완료시 HTTP OK(204)")
    @Test
    public void delete() throws Exception {
        //given
        doNothing().when(memberService).delete(any(Long.class));

        //when
        ResultActions result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/members/1"));

        //then
        result.andExpect(status().isOk());
    }
}