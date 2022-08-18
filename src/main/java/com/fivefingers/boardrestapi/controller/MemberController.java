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

    @PostMapping("/members")
    public ResponseEntity<CreateMemberDto> createMember(@RequestBody @Valid CreateMemberDto createMemberDto) {
        // 여기서 테스트 해야될게 있나..?
        // memberService.join()은 서비스에서 테스트할 부분
        //
        Member member = Member.from(createMemberDto);
        Long memberId = memberService.join(member);
        return ResponseEntity.created(URI.create(String.format("/api/v1/members/%d", memberId))).body(createMemberDto);
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
        List<ReadMemberDto> memberDtoList = memberService.findAll().stream()
                .map(ReadMemberDto::from)
                .collect(Collectors.toList());
        return new WrappedList<>(memberDtoList);
    }

    @PatchMapping("/members/{id}")
    public ResponseEntity<Object> updateMember(@PathVariable Long id,
                                                        @RequestBody @Valid UpdateMemberDto updateMemberDto) {
        if (!memberService.update(id, updateMemberDto)) {
            //변경 없을 경우 HTTP Status 204 Code
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(URI.create(String.format("/api/v1/members/%d", id)));
    }

    @DeleteMapping("/members/{id}")
    public ResponseEntity<Object> deleteMember(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.ok().build();
    }
}
