package com.example.dopamines.domain.board.community.open.controller;

import com.example.dopamines.domain.board.community.open.model.request.OpenPostReq;
import com.example.dopamines.domain.board.community.open.model.request.OpenPostUpdateReq;
import com.example.dopamines.domain.board.community.open.model.response.OpenPostReadRes;
import com.example.dopamines.domain.board.community.open.model.response.OpenPostRes;
import com.example.dopamines.domain.board.community.open.service.OpenPostService;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseResponse;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/open/post")
@Tag(name = "공개게시판", description = "공개 게시판 관련 API")
@RequiredArgsConstructor
public class OpenPostController {
    private final OpenPostService openPostService;
    private final CloudFileUploadService cloudFileUploadService;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "게시글 생성",
            description = "공개게시판 게시글을 생성합니다.",
            operationId = "createOpenPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{\"message\":\"게시글이 성공적으로 생성되었습니다.\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "게시글 생성 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{\"message\":\"게시글 생성에 실패하였습니다.\"}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> create(
            @Parameter(description = "게시글 제목", required = true) @RequestParam String title,
            @Parameter(description = "게시글 내용", required = true) @RequestParam String content, @RequestPart MultipartFile[] images,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        List<String> uploadImgUrl = cloudFileUploadService.uploadImages(images, "Open");
        String result = openPostService.create(user, title, content, uploadImgUrl.isEmpty() ? null : uploadImgUrl.get(0));
        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping("/read")
    @Operation(
            summary = "게시글 조회",
            description = "공개게시판 게시글을 조회합니다.",
            operationId = "readOpenPost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenPostReadRes.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, "
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "게시글 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenPostReadRes.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> read(
            @Parameter (description = "게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        OpenPostReadRes response = openPostService.read(idx);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @GetMapping("/read-all")
    @Operation(
            summary = "게시글 전체 조회",
            description = "공개게시판 게시글을 전체 조회합니다.",
            operationId = "read")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenPostRes.class)
                    )
            ),
            @ApiResponse(responseCode = "400", description = "게시글 전체 조회 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenPostRes.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> readAll(@AuthenticationPrincipal CustomUserDetails customUserDetails,Integer page, Integer size){
        User user = customUserDetails.getUser();
        List<OpenPostRes> response = openPostService.readAll(page,size);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @PutMapping("/update")
    @Operation(
            summary = "게시글 수정",
            description = "공개게시판 게시글을 수정합니다.",
            operationId = "update")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenPostRes.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"title\": \"성능 개선은 너무 어려워요\", " +
                                                    "\"content\": \"집에 가고 싶어요\", " +
                                                    "\"author\": \"작성자\", " +
                                                    "\"createdAt\": \"2021-08-01 00:00:00\", " +
                                                    "\"likeCount\": 0, " +
                                                    "\"commentList\": []}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "게시글 수정 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OpenPostRes.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"title\": \"성능 개선은 너무 어려워요\", " +
                                                    "\"content\": \"집에 가고 싶어요\", " +
                                                    "\"author\": \"작성자\", " +
                                                    "\"createdAt\": \"2021-08-01 00:00:00\", " +
                                                    "\"likeCount\": 0, " +
                                                    "\"commentList\": []}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> update(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @RequestBody Long idx,
            @Parameter(description = "게시글 제목", required = true, example = "성능 개선은 너무 어려워요") @RequestBody String title,
            @Parameter(description = "게시글 내용", required = true, example = "집에 가고 싶어요") @RequestBody String content,
            @Parameter(description = "게시글 이미지", required = true, example = "게시글 이미지") @RequestBody String image,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        OpenPostRes response = openPostService.update(user,idx,title,content,image);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "게시글 삭제",
            description = "공개게시판 게시글을 삭제합니다.",
            operationId = "delete")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1 }"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "게시글 삭제 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = String.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 1}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> delete(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @RequestBody Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        String response = openPostService.delete(user,idx);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }
}
