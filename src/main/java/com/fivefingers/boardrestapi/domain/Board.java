package com.fivefingers.boardrestapi.domain;

import lombok.Getter;

@Getter
public class Board {
    private Long id;
    private String title;
    private String content;


    public static Board createBoard(Long id, String title, String content) {
        Board board = new Board();
        board.id = id;
        board.title = title;
        board.content = content;
        return board;
    }
}
