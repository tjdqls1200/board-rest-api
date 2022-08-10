package com.fivefingers.boardrestapi.controller;

import com.fivefingers.boardrestapi.domain.Board;
import com.fivefingers.boardrestapi.domain.BoardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/board")
public class BoardController {
    @GetMapping("/{id}")
    public ResponseEntity<BoardDto> readPost(@PathVariable long id) {
        // Service.findById(id)
        Board board = Board.createBoard(id, "제목", "내용");
        BoardDto boardDto = BoardDto.builder()
                .id(board.getId())
                .title(board.getTitle())
                .content(board.getContent())
                .build();
        // test
        return ResponseEntity.ok().body(boardDto);
    }
}
