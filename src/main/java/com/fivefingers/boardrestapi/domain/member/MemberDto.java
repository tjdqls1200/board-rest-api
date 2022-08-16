package com.fivefingers.boardrestapi.domain.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MemberDto {
    @Data
    @AllArgsConstructor
    public static class CreateMemberDto {
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]*${6,12}", message = "아이디는 영어와 숫자를 포함한 6 ~ 12자 이내여야 합니다.")
        private String loginId;

        @Pattern(regexp = "(?=.*[A-Z])(?=.*\\W)(?=\\S+$).{10,16}", message = "비밀번호는 대문자, 특수문자를 포함한 10 ~ 16자 이내여야 합니다.")
        @NotBlank
        private String password;

        @NotBlank
        @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10 자리 이내로 입력해야 됩니다.")
        private String username;
    }

    @Data
    @AllArgsConstructor
    public static class UpdateMemberDto {

        @Pattern(regexp = "(?=.*[A-Z])(?=.*\\W)(?=\\S+$).{10,16}",
                message = "비밀번호는 대문자, 특수문자를 포함한 10 ~ 16자 이내여야 합니다.")
        private String newPassword;

        @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10 자리 이내로 입력해야 됩니다.")
        private String username;
    }

    @Data
    public static class DeleteMemberDto {
        private Long id;

        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9]*${6,12}", message = "아이디는 영어와 숫자를 포함한 6 ~ 12자 이내여야 합니다.")
        private String loginId;

        // .* : 하나라도 포함, \\W : 특수문자 포함, \\S+$ : 공백 제거
        @Pattern(regexp = "(?=.*[A-Z])(?=.*\\W)(?=\\S+$).{10,16}", message = "비밀번호는 대문자, 특수문자를 포함한 10 ~ 16자 이내여야 합니다.")
        @NotBlank
        private String password;
    }


    @Data
    @AllArgsConstructor
    public static class ReadMemberDto {
        private String loginId;
        private String username;

        public static ReadMemberDto from(Member member) {
            return new ReadMemberDto(member.getLoginId(), member.getUsername());
        }
    }

    @Data
    @AllArgsConstructor
    public static class WrappedList<T> {
        private T list;
    }

}
