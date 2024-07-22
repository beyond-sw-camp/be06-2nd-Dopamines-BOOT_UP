package com.example.dopamines.domain.board.community.open.controller;

import com.example.dopamines.domain.board.community.open.model.request.OpenRecommentReq;
import com.example.dopamines.domain.board.community.open.model.request.OpenRecommentUpdateReq;
import com.example.dopamines.domain.board.community.open.service.OpenRecommentService;
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
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open/recomment")
@Tag(name = "공개게시판", description = "공개 게시판 관련 API")
@RequiredArgsConstructor
public class OpenRecommentController {
    private final OpenRecommentService openRecommentService;

    @PostMapping("/create")
    @Operation(
            summary = "대댓글 생성",
            description = "공개게시판 대댓글을 생성합니다.",
            operationId = "createOpenRecomment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenRecommentReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"content\": \"대댓글 내용\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "대댓글 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenRecommentReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 0, " +
                                                    "\"content\": \"\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> create(
            @Parameter(description = "댓글 인덱스", required = true, example = "1" ) @RequestParam Long idx,
            @Parameter(description = "대댓글 내용", required = true, example = "대댓글 내용") @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String response = openRecommentService.create(user, idx, content);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @PutMapping("/update")
    @Operation(
            summary = "대댓글 수정",
            description = "공개게시판 대댓글을 수정합니다.",
            operationId = "updateOpenRecomment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenRecommentUpdateReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"content\": \"대댓글 내용\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "대댓글 수정 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenRecommentUpdateReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 0, " +
                                                    "\"content\": \"\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> update(
            @Parameter(description = "대댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "대댓글 내용", required = true, example = "대댓글 내용") @RequestParam String content,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String response = openRecommentService.update(user, idx, content);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "대댓글 삭제",
            description = "공개게시판 대댓글을 삭제합니다.",
            operationId = "deleteOpenRecomment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 삭제 성공",
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
            @ApiResponse(responseCode = "400", description = "대댓글 삭제 실패",
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
    public ResponseEntity<BaseResponse<?>> delete(
            @Parameter(description = "대댓글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String response = openRecommentService.delete(user,idx);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }
}

