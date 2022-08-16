package com.fivefingers.boardrestapi.controller;

import com.fivefingers.boardrestapi.domain.member.Member;
import com.fivefingers.boardrestapi.repository.MemberRepository;
import com.fivefingers.boardrestapi.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {
    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/members")
    public ResponseEntity<CreateMemberDto> createMember(@RequestBody @Valid CreateMemberDto createMemberDto) {
        Member member = Member.from(createMemberDto);
        memberService.join(member);
        return ResponseEntity.created(URI.create("/api/v1/users/" + member.getId())).body(createMemberDto);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/members/{id}")
    public ReadMemberDto readMember(@PathVariable Long id) {
        Member findMember = memberService.findOne(id);
        return ReadMemberDto.from(findMember);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/members")
    public WrappedList<List<ReadMemberDto>> readMemberList() {
        List<ReadMemberDto> memberDtoList = memberRepository.findAll().stream()
                .map(ReadMemberDto::from)
                .collect(Collectors.toList());
        return new WrappedList<>(memberDtoList);
    }

    @PatchMapping("/members/{id}")
    public ResponseEntity<UpdateMemberDto> updateMember(@PathVariable Long id,
                                                        @RequestBody @Valid UpdateMemberDto updateMemberDto) {
        if (!memberService.update(id, updateMemberDto)) {
            //변경 없을 경우 HTTP Status 204 Code
            ResponseEntity.noContent().build();
        }
        return ResponseEntity.created(URI.create("/api/v1/users/" + id)).body(updateMemberDto);
    }


    @DeleteMapping("/members/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
