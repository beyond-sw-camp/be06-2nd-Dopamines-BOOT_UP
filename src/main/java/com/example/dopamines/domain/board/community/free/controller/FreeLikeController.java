package com.example.dopamines.domain.board.community.free.controller;

import com.example.dopamines.domain.board.community.free.model.request.FreePostLikeReq;
import com.example.dopamines.domain.board.community.free.service.FreeLikeService;
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

@RestController
@Tag(name = "자유게시판", description = "자유게시판 좋아요 API")
@RequestMapping("/free/like")
@RequiredArgsConstructor
public class FreeLikeController {
    private final FreeLikeService freeLikeService;

    @GetMapping("/post")
    @Operation(
            summary = "게시글 좋아요",
            description = "자유게시판 게시글 좋아요를 생성합니다.",
            tags = "자유게시판 게시글 좋아요 생성",
            operationId = "createFreePostLike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 생성 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 좋아요 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> createFreePostLike(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 좋아요 생성 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation =FreePostLikeReq.class),
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
            ) FreePostLikeReq req){
        User user = customUserDetails.getUser();
        String result = freeLikeService.createFreePostLike(user,idx);
        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping("/comment")
    @Operation(
            summary = "댓글 좋아요",
            description = "자유게시판 댓글 좋아요를 생성합니다.",
            tags = "자유게시판 댓글 좋아요 생성",
            operationId = "createFreeCommentLike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 좋아요 생성 성공"),
            @ApiResponse(responseCode = "400", description = "댓글 좋아요 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> createCommentLike(
            @Parameter(description = "댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "댓글 좋아요 생성 요청", required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation =FreePostLikeReq.class),
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
    ) FreePostLikeReq req){
        User user = customUserDetails.getUser();
        String result = freeLikeService.createCommentLike(user,idx);
        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping("/recomment")
    @Operation(
            summary = "대댓글 좋아요",
            description = "자유게시판 대댓글 좋아요를 생성합니다.",
            tags = "자유게시판 대댓글 좋아요 생성",
            operationId = "createRecommentLike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 좋아요 생성 성공"),
            @ApiResponse(responseCode = "400", description = "대댓글 좋아요 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> createRecommentLike(
            @Parameter(description = "대댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "대댓글 좋아요 생성 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation =FreePostLikeReq.class),
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
            ) FreePostLikeReq req){
        User user = customUserDetails.getUser();
        String result = freeLikeService.createRecommentLike(user,idx);

        return ResponseEntity.ok(new BaseResponse<>(result));
    }
}
