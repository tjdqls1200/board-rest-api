package com.fivefingers.boardrestapi.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BoardDto {
    private Long id;
    private String title;
    private String content;
}
