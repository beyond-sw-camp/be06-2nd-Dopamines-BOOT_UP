package com.example.dopamines.domain.board.community.free.controller;

import com.example.dopamines.domain.board.community.free.model.request.FreePostReq;
import com.example.dopamines.domain.board.community.free.model.request.FreePostUpdateReq;
import com.example.dopamines.domain.board.community.free.model.response.FreePostReadRes;
import com.example.dopamines.domain.board.community.free.model.response.FreePostRes;
import com.example.dopamines.domain.board.community.free.service.FreePostService;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "자유게시판", description = "자유게시판 API")
@RequestMapping("/free/post")
@RequiredArgsConstructor
public class FreePostController {
    private final FreePostService freePostService;
    private final CloudFileUploadService cloudFileUploadService;

    @PostMapping("/create")
    @Operation(
            summary = "게시글 생성",
            description = "자유게시판 게시글을 생성합니다.",
            operationId = "createFreePost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> create(
            @Parameter(description = "게시글 제목", required = true, example = "게시글 제목") @RequestParam String title,
            @Parameter(description = "게시글 내용", required = true, example = "게시글 내용") @RequestParam String content,
            @Parameter(description = "게시글 이미지", required = false, example = "http://example.com/image2.jpg") @RequestParam MultipartFile[] files,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 생성 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FreePostReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"title\": \"게시글 제목\", " +
                                                    "\"content\": \"게시글 내용\"" +
                                                    "\"files\": \"http://example.com/image2.jpg\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"title\": \"\", " +
                                                    "\"content\": \"\"," +
                                                    "\"files\": \"\"}"
                                    )
                            }
                    )
            ) FreePostReq req){
        User user = customUserDetails.getUser();
        String rootType ="FREEBOARD";
        List<String> urlLists = cloudFileUploadService.uploadImages(files, rootType);
        String result = freePostService.create(user, req, urlLists);
        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping("/read")
    @Operation(
            summary = "게시글 조회",
            description = "자유게시판 게시글을 조회합니다.",
            operationId = "readFreePost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> read(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 조회 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FreePostReadRes.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{\"" +
                                                    "idx\": 1}"
                                    ),
                                    @ExampleObject(
                                            name = "Failure Example",
                                            value = "{\"" +
                                                    "idx\": 0}"
                                    )
                            }
                    )
            ) FreePostReadRes req){
        User user = customUserDetails.getUser();
        FreePostReadRes response = freePostService.read(idx);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @GetMapping("/read-all")
    @Operation(
            summary = "게시글 전체 조회",
            description = "자유게시판 게시글을 전체 조회합니다.",
            operationId = "readAllFreePost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 전체 조회 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 전체 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> readAll(
            @Parameter(description = "페이지", required = true, example = "1") @RequestParam Integer page,
            @Parameter(description = "사이즈", required = true, example = "10") @RequestParam Integer size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 전체 조회 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FreePostRes.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{\"" +
                                                    "page\": 1, " +
                                                    "size\": 10}"
                                    ),
                                    @ExampleObject(
                                            name = "Failure Example",
                                            value = "{\"" +
                                                    "page\": , " +
                                                    "size\": 10}"
                                    )
                            }
                    )
            ) FreePostRes req){
        User user = customUserDetails.getUser();
        List<FreePostRes> response = freePostService.readAll(page,size);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @PutMapping("/update")
    @Operation(
            summary = "게시글 수정",
            description = "자유게시판 게시글을 수정합니다.",
            operationId = "updateFreePost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 수정 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> update(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "게시글 제목", required = true, example = "게시글 제목") @RequestParam String title,
            @Parameter(description = "게시글 내용", required = true, example = "게시글 내용") @RequestParam String content,
            @Parameter(description = "게시글 이미지", required = false, example = "http://example.com/image2.jpg") @RequestPart MultipartFile[] files,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 수정 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FreePostUpdateReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"title\": \"게시글 제목\", " +
                                                    "\"content\": \"게시글 내용\"" +
                                                    "\"files\": \"http://example.com/image2.jpg\"}"
                                    ),
                                    @ExampleObject(
                                            name = "Fail Example",
                                            value = "{" +
                                                    "\"idx\": 1, " +
                                                    "\"title\": \"\", " +
                                                    "\"content\": \"\"," +
                                                    "\"files\": \"\"}"
                                    )
                            }
                    )
            ) FreePostUpdateReq req){
        User user = customUserDetails.getUser();
        String rootType ="FREEBOARD";
        List<String> urlLists = cloudFileUploadService.uploadImages(files, rootType);
        FreePostRes response = freePostService.update(user,req,urlLists);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @DeleteMapping("/delete")
    @Operation(
            summary = "게시글 삭제",
            description = "자유게시판 게시글을 삭제합니다.",
            operationId = "deleteFreePost")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "게시글 삭제 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> delete(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @RequestParam Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "게시글 삭제 요청",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = FreePostUpdateReq.class),
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
            ) FreePostUpdateReq req
            ){
        User user = customUserDetails.getUser();
        String response = freePostService.delete(user,idx);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }


}
