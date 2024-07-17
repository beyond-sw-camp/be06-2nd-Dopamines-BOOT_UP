package com.example.dopamines.domain.board.project.model.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProjectBoardRes {
    private Long idx;

    private String title;
    private String contents;
    private Integer courseNum;
    private String sourceUrl;

    private String teamName;
    private List<String> students;
}
