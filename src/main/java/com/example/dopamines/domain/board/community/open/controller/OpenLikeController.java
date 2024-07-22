package com.example.dopamines.domain.board.community.open.controller;

import com.example.dopamines.domain.board.community.open.service.OpenLikeService;
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
@RequestMapping("/open/like")
@Tag(name = "공개게시판", description = "공개 게시판 관련 API")
@RequiredArgsConstructor
public class OpenLikeController {
    private final OpenLikeService openLikeService;

    @GetMapping("/post")
    @Operation(
            summary = "게시글 좋아요",
            description = "공개게시판 게시글 좋아요를 생성합니다.",
            operationId = "createOpenPostLike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 좋아요 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "1"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "게시글 좋아요 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "0"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> createOpenPostLike(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
            
    ){
        User user = customUserDetails.getUser();
        String result = openLikeService.createOpenPostLike(user,idx);

        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping("/comment")
    @Operation(
            summary = "댓글 좋아요",
            description = "공개게시판 댓글 좋아요를 생성합니다.",
            operationId = "createCommentLike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 좋아요 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "1"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "댓글 좋아요 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "0"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> createCommentLike(
            @Parameter(description = "댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String result = openLikeService.createCommentLike(user,idx);

        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping("/recomment")
    @Operation(
            summary = "대댓글 좋아요",
            description = "공개게시판 대댓글 좋아요를 생성합니다.",
            operationId = "createRecommentLike")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 좋아요 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "1"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "대댓글 좋아요 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Long.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "0"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> createRecommentLike(
            @Parameter(description = "대댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String result = openLikeService.createRecommentLike(user,idx);

        return ResponseEntity.ok(new BaseResponse<>(result));
    }
}
