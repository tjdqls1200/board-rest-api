package com.fivefingers.boardrestapi.controller;

import com.fivefingers.boardrestapi.domain.member.*;
import com.fivefingers.boardrestapi.service.MemberService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import static com.fivefingers.boardrestapi.domain.member.MemberDto.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Slf4j
@Api(tags = "Members")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/members")
    public ResponseEntity<ResponseMemberDto> createMember(
            @RequestBody @Valid CreateMemberDto createMemberDto) {
        Long memberId = memberService.join(createMemberDto);
        Member member = memberService.findOne(memberId);

        return ResponseEntity
                .created(URI.create(String.format("/api/v1/members/%d", memberId)))
                .body(ResponseMemberDto.from(member));
    }


    @Operation(summary = "read one member", description = "example")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "NOT FOUND")
    })
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/members/{id}")
    public EntityModel<ReadMemberDto> readMember(@PathVariable Long id) {
        Member findMember = memberService.findOne(id);
        ReadMemberDto readMemberDto = ReadMemberDto.from(findMember);

        // EntityModel<ReadMemberDto> model = new EntityModel<>(readMemberDto);
        EntityModel<ReadMemberDto> model = EntityModel.of(readMemberDto);
        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).readMemberList());
        model.add(linkTo.withRel("all-members"));
        return model;
    }

    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/members")
    public WrappedList<List<ReadMemberDto>> readMemberList() {
        List<ReadMemberDto> memberDtoList = memberService.findAll().stream()
                .map(ReadMemberDto::from)
                .collect(Collectors.toList());
        return new WrappedList<>(memberDtoList);
    }

    @PatchMapping("/members/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Object> updateMember(
            @PathVariable Long id,
            @RequestBody @Valid UpdateMemberDto updateMemberDto) {
        if (!memberService.update(id, updateMemberDto)) {
            //변경 없을 경우 HTTP Status 204 Code
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(URI.create(String.format("/api/v1/members/%d", id)));
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping("/members/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public void deleteMember(@PathVariable Long id) {
        memberService.delete(id);
    }
}
