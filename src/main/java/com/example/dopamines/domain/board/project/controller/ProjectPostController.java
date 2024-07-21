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
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
            summary = "프로젝트 게시글 생성",
            description = "프로젝트게시판 게시글을 생성합니다.",
            operationId = "createProjectPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "프로젝트 게시글 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ProjectPostRes> create(
            @Parameter(description = "프로젝트 제목", required = true, example = "Boot Up") @RequestParam String  title,
            @Parameter(description = "프로젝트 내용", required = true, example = "한화 시스템 커뮤니티 게시판") @RequestParam String content,
            @Parameter(description = "기수", required = true, example = "6") @RequestParam Integer courseNum,
            @Parameter(description = "팀 인덱스", required = true, example = "5") @RequestParam Long teamIdx,
            @Parameter(description = "프로젝트 이미지", required = false, example = "http://example.com/image1.jpg") @RequestParam MultipartFile[] files,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "프로젝트 게시글 생성 요청", required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProjectPostReq.class),
                        examples = {
                                @ExampleObject(
                                        name = "Success Example",
                                        value = "{" +
                                                "\"title\": \"부트 업\", " +
                                                "\"content\": \"한화 시스템 커뮤니티 게시판\", " +
                                                "\"courseNum\": \"6\"" +
                                                "\"teamIdx\": \"5\"" +
                                                "\"files\": \"[\"http://example.com/image1.jpg\"]\"}"
                                ),
                                @ExampleObject(
                                        name = "Fail Example",
                                        value = "{" +
                                                "\"title\": \"\", " +
                                                "\"content\": \"프로젝트 내용\", " +
                                                "\"courseNum\": \"6기\"" +
                                                "\"teamIdx\": \"팀 인덱스\"" +
                                                "\"files\": \"[\"http://example.com/image1.jpg\"]\"}"
                                )
                        }
                    )
            ) ProjectPostReq req)
                {

            List<String> savedFileName = cloudFileUploadService.uploadImages(files, rootType);
            ProjectPostRes response = projectBoardService.create( title, content, courseNum, teamIdx, savedFileName.get(0));

            return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(response).getResult());

    }

    // 프로젝트 게시판 조회
    @GetMapping("/read")
    @Operation(
            summary = "프로젝트 게시글 조회",
            description = "프로젝트게시판 게시글을 조회합니다.",
            operationId = "readProjectPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 게시글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "프로젝트 게시글 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ProjectPostReadRes> read(
            @Parameter(description = "프로젝트 게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "프로젝트 게시글 조회 요청", required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProjectPostReadRes.class),
                        examples = {
                                @ExampleObject(
                                        name = "Success Example",
                                        value = "{" +
                                                "\"idx\": 1}"
                                ),
                                @ExampleObject(
                                        name = "Fail Example",
                                        value = "{" +
                                                "\"idx\": }"
                                )
                        }
                    )
            ) ProjectPostReadRes req) {
        ProjectPostReadRes response = projectBoardService.read(idx);
        if(response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/read-by-course-num")
    @Operation(
            summary = "프로젝트 기수별 게시글 조회",
            description = "프로젝트게시판 게시글을 조회합니다.",
            operationId = "readProjectPostByCourseNum")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 게시글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "프로젝트 게시글 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<List<ProjectPostReadRes>> readByCourseNum(
            @Parameter(description = "프로젝트 기수", required = true, example = "6") @RequestParam Long courseNum,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "프로젝트 게시글 조회 요청", required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProjectPostReadRes.class),
                        examples = {
                                @ExampleObject(
                                        name = "Success Example",
                                        value = "{" +
                                                "\"courseNum\": 6}"
                                ),
                                @ExampleObject(
                                        name = "Fail Example",
                                        value = "{" +
                                                "\"courseNum\": }"
                                )
                        }
                    )
            ) ProjectPostReadRes req
    ) {

        List<ProjectPostReadRes> response = projectBoardService.readByCourseNum(courseNum);

        if(response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @CheckAuthentication
    @GetMapping("/read-all")
    @Operation(
            summary = "프로젝트 게시글 전체 조회",
            description = "프로젝트게시판 게시글을 전체 조회합니다.",
            operationId = "readAllProjectPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 게시글 전체 조회 성공"),
            @ApiResponse(responseCode = "400", description = "프로젝트 게시글 전체 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<?> readAll(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "프로젝트 게시글 전체 조회 요청", required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProjectPostReadRes.class),
                        examples = {
                                @ExampleObject(
                                        name = "Success Example",
                                        value = "{" +
                                                "\"userDetails\": \"유저 정보\"}"
                                ),
                                @ExampleObject(
                                        name = "Fail Example",
                                        value = "{" +
                                                "\"userDetails\": }"
                                )
                        }
                    )
            ) ProjectPostReadRes req ) {

        List<ProjectPostReadRes> response = projectBoardService.readAll();
        if(response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PatchMapping("/update")
    @Operation(
            summary = "프로젝트 게시글 수정",
            description = "프로젝트게시판 게시글을 수정합니다.",
            operationId = "updateProjectPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "프로젝트 게시글 수정 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<ProjectPostReadRes> update(
            @Parameter(description = "프로젝트 게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "프로젝트 이미지", required = true, example = "http://example.com/image1.jpg") @RequestParam MultipartFile[] files,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "프로젝트 게시글 수정 요청", required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProjectPostUpdateReq.class),
                        examples = {
                                @ExampleObject(
                                        name = "Success Example",
                                        value = "{" +
                                                "\"idx\": 1, " +
                                                "\"sourceUrl\": \"http://example.com/image1.jpg\"}"
                                ),
                                @ExampleObject(
                                        name = "Fail Example",
                                        value = "{" +
                                                "\"sourceUrl\": \"\"}"
                                )
                        }
                    )
            ) ProjectPostUpdateReq req) {
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
    @Operation(
            summary = "프로젝트 게시글 삭제",
            description = "프로젝트게시판 게시글을 삭제합니다.",
            operationId = "deleteProjectPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로젝트 게시글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "프로젝트 게시글 삭제 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<String> delete(
            @Parameter(description = "프로젝트 게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "프로젝트 게시글 삭제 요청", required = true,
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProjectPostReadRes.class),
                        examples = {
                                @ExampleObject(
                                        name = "Success Example",
                                        value = "{" +
                                                "\"idx\": 1}"
                                ),
                                @ExampleObject(
                                        name = "Fail Example",
                                        value = "{" +
                                                "\"idx\": }"
                                )
                        }
                    )
            ) ProjectPostReadRes req) {
        projectBoardService.delete(idx);
        return ResponseEntity.ok("삭제가 완료되었습니다.");
    }
}