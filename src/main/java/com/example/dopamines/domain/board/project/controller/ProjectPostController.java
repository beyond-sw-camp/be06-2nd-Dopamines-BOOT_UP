package com.example.dopamines.domain.board.project.controller;

import static com.example.dopamines.global.common.BaseResponseStatus.UNAUTHORIZED_ACCESS;


import com.example.dopamines.domain.board.project.model.request.ProjectPostReq;
import com.example.dopamines.domain.board.project.model.request.ProjectPostUpdateReq;
import com.example.dopamines.domain.board.project.model.response.ProjectPostReadRes;
import com.example.dopamines.domain.board.project.model.response.ProjectPostRes;
import com.example.dopamines.domain.board.project.service.ProjectPostService;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.annotation.CheckAuthentication;
import com.example.dopamines.global.infra.s3.CloudFileUploadService;
import com.example.dopamines.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "프로젝트게시판", description = "프로젝트게시판 API")
@RequestMapping("/project")
@RequiredArgsConstructor
public class ProjectPostController {

    private String rootType = "PROJECT";
    private final ProjectPostService projectBoardService;

    private final CloudFileUploadService cloudFileUploadService;

    @PostMapping("/create")
    @Operation(
            summary = "게시글 생성",
            description = "프로젝트게시판 게시글을 생성합니다.",
            tags = "프로젝트게시판 게시글 생성",
            operationId = "createProjectPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "프로젝트 게시글 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ProjectPostRes> create(
            @Parameter(description = "프로젝트 제목", required = true, example = "프로젝트 제목") @RequestPart ProjectPostReq title,
            @Parameter(description = "프로젝트 내용", required = true, example = "프로젝트 내용") @RequestPart ProjectPostReq content,
            @Parameter(description = "기수", required = true, example = "6기") @RequestPart ProjectPostReq courseNum,
            @Parameter(description = "프로젝트 이미지", required = true, example = "프로젝트 이미지") @RequestPart MultipartFile[] files,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "프로젝트 게시글 생성 요청",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProjectPostReq.class),
                        examples = {
                                @ExampleObject(
                                        name = "Success Example",
                                        value = "{" +
                                                "\"title\": \"프로젝트 제목\", " +
                                                "\"content\": \"프로젝트 내용\", " +
                                                "\"courseNum\": \"6기\"}"
                                ),
                                @ExampleObject(
                                        name = "Fail Example",
                                        value = "{" +
                                                "\"title\": \"\", " +
                                                "\"content\": \"프로젝트 내용\", " +
                                                "\"courseNum\": \"6기\"}"
                                )
                        }
                ) ProjectPostReq req) {

            List<String> savedFileName = cloudFileUploadService.uploadImages(files, rootType);
            ProjectPostRes response = projectBoardService.create(req, savedFileName.get(0));

            return ResponseEntity.ok(response);

    }
    @GetMapping("/read")
    public ResponseEntity<ProjectPostReadRes> read(Long idx) {

        ProjectPostReadRes response = projectBoardService.read(idx);

        if(response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/read-by-course-num")
    public ResponseEntity<List<ProjectPostReadRes>> readByCourseNum(Long courseNum) {

        List<ProjectPostReadRes> response = projectBoardService.readByCourseNum(courseNum);

        if(response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @CheckAuthentication
    @GetMapping("/read-all")
    public ResponseEntity<?> readAll(@AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean hasAdminRole = userDetails.getAuthorities().stream().anyMatch(authority->authority.getAuthority().equals("ROLE_ADMIN"));
        if (!hasAdminRole) {
            return ResponseEntity.badRequest().body(new BaseResponse(UNAUTHORIZED_ACCESS));
        }

        List<ProjectPostReadRes> response = projectBoardService.readAll();
        if(response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PatchMapping("/update")
    public ResponseEntity<ProjectPostReadRes> update(@RequestPart ProjectPostUpdateReq req, @RequestPart MultipartFile[] files) {
        ProjectPostReadRes response = null;
        if(!req.getSourceUrl().isEmpty()) {
            response = projectBoardService.update(req, req.getSourceUrl());
        } else {
            List<String> savedFileName = cloudFileUploadService.uploadImages(files, "PROJECT"); // TODO : "PROJECT" 지우기
            response = projectBoardService.update(req, savedFileName.get(0));
        }

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> delete(Long idx) {

        projectBoardService.delete(idx);

        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }
}