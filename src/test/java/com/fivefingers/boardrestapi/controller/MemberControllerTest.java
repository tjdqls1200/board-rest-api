package com.fivefingers.boardrestapi.controller;

import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.service.MemberService;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.BDDMockito.willReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberService memberService;

    @Autowired
    private Gson gson;

    @BeforeEach
    public void init() {
        // 스프링부트가 제공하는 @WebMvcTest를 사용하면 MockMvc도 스프링이 직접 생성
        //mockMvc = MockMvcBuilders.standaloneSetup(memberController).build();
    }

    @DisplayName("회원 가입 성공시 가입 정보 반환 HTTP Created(201)")
    @Test
    public void join() throws Exception {
        //given
        CreateMemberDto createMemberDto = createMemberBuild(1);
        given(memberService.join(any(CreateMemberDto.class))).willReturn(1L);

        //when
        ResultActions resultActions = mockMvc
                .perform(post("/api/v1/members")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(createMemberDto)));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isCreated()).andReturn();
        String responseJsonDto = mvcResult.getResponse().getContentAsString();
        assertThat(responseJsonDto).isEqualTo(gson.toJson(createMemberDto));
    }


    private CreateMemberDto createMemberBuild(int idx) {
        return CreateMemberDto.builder()
                .loginId("mockTest" + idx)
                .password("MockTest123!" + idx)
                .username("mockname" + idx)
                .build();
    }

    @DisplayName("단일 회원 조회 성공 HTTP OK(200)")
    @Test
    public void readMember() throws Exception {
        //given
        Member readMember = Member.from(createMemberBuild(2));
        given(memberService.findOne(1L)).willReturn(readMember);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/members/1"));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        String responseDto = mvcResult.getResponse().getContentAsString();
        assertThat(responseDto).isEqualTo(gson.toJson(ReadMemberDto.from(readMember)));

//        resultActions.andExpect(status().isOk())
//                .andExpect(jsonPath("$.loginId").value(readMember.getLoginId()))
//                .andExpect(jsonPath("$.username").value(readMember.getUsername()));
    }

    @DisplayName("회원 리스트 조회 성공 HTTP OK(200)")
    @Test
    public void readMemberList() throws Exception {
        //given
        List<Member> memberList = createReadMemberList(5);
        given(memberService.findAll()).willReturn(memberList);
        WrappedList<List<ReadMemberDto>> response = responseReadMemberList(memberList);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/members"));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(gson.toJson(response));
    }

    // 멘토 질문
    // 리스트가 생각이랑 다르게 반환
    @DisplayName("회원 리스트 조회시 빈 리스트 반환 HTTP OK(200)")
    @Test
    public void readEmptyMemberList() throws Exception {
        //given
        List<Member> emptyMemberList = Collections.emptyList();
        given(memberService.findAll()).willReturn(emptyMemberList);
        WrappedList<List<ReadMemberDto>> response = responseReadMemberList(emptyMemberList);

        //when
        ResultActions resultActions = mockMvc.perform(get("/api/v1/members"));

        //then
        MvcResult mvcResult = resultActions.andExpect(status().isOk()).andReturn();
        assertThat(mvcResult.getResponse().getContentAsString()).isEqualTo(gson.toJson(response));
    }

    private WrappedList<List<ReadMemberDto>> responseReadMemberList(List<Member> memberList) {
        return new WrappedList<>(memberList.stream()
                .map(ReadMemberDto::from)
                .collect(Collectors.toList()));
    }


    private List<Member> createReadMemberList(int n) {
        List<Member> list = new ArrayList<>();
        for (int i = 0; i < n; i++)
            list.add(Member.from(createMemberBuild(i)));
        return list;
    }

    @DisplayName("회원 정보 변경 완료시 HTTP OK(200)")
    @Test
    public void update() throws Exception {
        //given
        given(memberService.update(any(Long.class), any(UpdateMemberDto.class))).willReturn(true);

        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new UpdateMemberDto("testPass123!", "testName"))));

        //then
        resultActions.andExpect(status().isOk());
    }

    @DisplayName("회원 정보 변경 사항이 없는 경우 HTTP NO_CONTENT(204)")
    @Test
    public void notUpdate() throws Exception {
        //given
        given(memberService.update(any(Long.class), any(UpdateMemberDto.class))).willReturn(false);

        //when
        ResultActions resultActions = mockMvc.perform(patch("/api/v1/members/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(gson.toJson(new UpdateMemberDto("testPass123!", "testName"))));

        //then
        resultActions.andExpect(status().isNoContent());
    }

    @DisplayName("회원 삭제 완료시 HTTP OK(204)")
    @Test
    public void delete() throws Exception {
        //given
        doNothing().when(memberService).delete(any(Long.class));

        //when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/members/1"));

        //then
        resultActions.andExpect(status().isOk());
    }
}