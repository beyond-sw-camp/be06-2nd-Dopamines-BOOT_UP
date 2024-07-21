package com.example.dopamines.domain.board.community.free.controller;

import com.example.dopamines.domain.board.community.free.model.request.FreeCommentReq;
import com.example.dopamines.domain.board.community.free.model.request.FreeCommentUpdateReq;
import com.example.dopamines.domain.board.community.free.model.request.FreeRecommentReq;
import com.example.dopamines.domain.board.community.free.model.request.FreeRecommentUpdateReq;
import com.example.dopamines.domain.board.community.free.model.response.FreeCommentReadRes;
import com.example.dopamines.domain.board.community.free.service.FreeRecommentService;
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
@RequestMapping("/free/recomment")
@Tag(name = "자유게시판", description = "자유게시판 대댓글 API")
@RequiredArgsConstructor
public class FreeRecommentController {
    private final FreeRecommentService freeRecommentService;

    @PostMapping("/create")
    @Operation(
            summary = "대댓글 생성",
            description = "자유게시판 대댓글을 생성합니다.",
            tags = "자유게시판 대댓글 생성",
            operationId = "createFreeRecomment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "대댓글 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> create(
            @Parameter(description = "댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "대댓글 내용", required = true, example = "대댓글 내용") @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "대댓글 생성 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FreeRecommentReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"content\": \"대댓글 내용!\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 0, " +
                                                    "\"content\": \"\"}"
                                    )
                            }
                    )
            ) FreeRecommentReq req){
        User user = customUserDetails.getUser();
        String response = freeRecommentService.create(user,req);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @PutMapping("/update")
    @Operation(
            summary = "대댓글 수정",
            description = "자유게시판 대댓글을 수정합니다.",
            tags = "자유게시판 대댓글 수정",
            operationId = "updateFreeRecomment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "대댓글 수정 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> update(
            @Parameter(description = "대댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "대댓글 내용", required = true, example = "대댓글 내용") @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String response = freeRecommentService.update(user,idx,content);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "대댓글 삭제",
            description = "자유게시판 대댓글을 삭제합니다.",
            tags = "자유게시판 대댓글 삭제",
            operationId = "deleteFreeRecomment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "대댓글 삭제 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> delete(
            @Parameter(description = "대댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String response = freeRecommentService.delete(user,idx);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
}

