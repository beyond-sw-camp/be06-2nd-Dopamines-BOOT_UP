package com.example.dopamines.domain.board.community.open.controller;

import com.example.dopamines.domain.board.community.open.model.request.OpenCommentReq;
import com.example.dopamines.domain.board.community.open.model.request.OpenCommentUpdateReq;
import com.example.dopamines.domain.board.community.open.model.response.OpenCommentReadRes;
import com.example.dopamines.domain.board.community.open.service.OpenCommentService;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseResponse;
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

import java.util.List;

@RestController
@RequestMapping("/open/comment")
@Tag(name = "공개게시판", description = "공개게시판 댓글 API")
@RequiredArgsConstructor
public class OpenCommentController {
    private final OpenCommentService openCommentService;

    @PostMapping("/create")
    @Operation(
            summary = "댓글 생성",
            description = "공개게시판 댓글을 생성합니다.",
            tags = "공개게시판 댓글 생성",
            operationId = "createOpenComment")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "댓글 생성 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> create(
            @Parameter(description = "공개 게시판 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "댓글 내용", required = true, example = "댓글 내용") @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 생성 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenCommentReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"content\": \"댓글 내용\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 0, " +
                                                    "\"content\": \"댓글 내용\"}"
                                    )
                            }
                    )
            ) OpenCommentReq req){
        User user = customUserDetails.getUser();
        String response = openCommentService.create(user,req);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @GetMapping("/read")
    @Operation(
            summary = "댓글 조회",
            description = "공개게시판 댓글을 조회합니다.",
            tags = "공개게시판 댓글 조회",
            operationId = "readOpenComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> read(
            @Parameter(description = "댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 조회 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenCommentReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1}"
                                    ),
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 0}"
                                    )
                            }
                    )
            ) OpenCommentReq req
            ){
        User user = customUserDetails.getUser();
        List<OpenCommentReadRes> response = openCommentService.read(user,idx);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @PutMapping("/update")
    @Operation(
            summary = "댓글 수정",
            description = "공개게시판 댓글을 수정합니다.",
            tags = "공개게시판 댓글 수정",
            operationId = "updateOpenComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 수정 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> update(
            @Parameter(description = "댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "댓글 내용", required = true, example = "댓글 내용") @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "댓글 수정 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenCommentUpdateReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"content\": \"댓글 내용\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 0, " +
                                                    "\"content\": \"\"}"
                                    )
                            }
                    )
            ) OpenCommentUpdateReq req ){
        User user = customUserDetails.getUser();
        String response = openCommentService.update(user,req);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "댓글 삭제",
            description = "공개게시판 댓글을 삭제합니다.",
            tags = "공개게시판 댓글 삭제",
            operationId = "deleteOpenComment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 삭제 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> delete(
            @Parameter(description = "댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String response = openCommentService.delete(user,idx);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }
}
