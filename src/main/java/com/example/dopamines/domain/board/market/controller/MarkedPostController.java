package com.example.dopamines.domain.board.market.controller;

import com.example.dopamines.domain.board.market.model.response.MarketReadRes;
import com.example.dopamines.domain.board.market.service.MarkedService;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.annotation.CheckAuthentication;
import com.example.dopamines.global.security.CustomUserDetails;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/marked")
@Tag(name = "중고마켓", description = "찜한 장터 게시글 API")
@RequiredArgsConstructor
public class MarkedPostController {

    private final MarkedService markedService;

    @PutMapping("/{idx}")
    @Operation(
            summary = "찜하기",
            description = "해당 장터 게시글을 찜합니다.",
            operationId = "create"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "찜하기 성공",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "찜하기 성공",
                                    value =  "{\"message\":\"찜 성공\"}"
                    )
            )),
            @ApiResponse(responseCode = "400", description = "찜하기 실패",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "찜하기 실패",
                                    value = "{\"message\":\"찜하기 실패\"}"
                            )
            ))}
    )
    public ResponseEntity<BaseResponse<?>> create(
            @Parameter(description = "찜할 게시글의 idx", required = true)  @PathVariable("idx") Long idx,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        String result = markedService.create(user, idx);
        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping
    @Operation(
            summary = "찜한 게시글 조회",
            description = "찜한 장터 게시글을 조회합니다.",
            operationId = "findAll"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "찜한 게시글 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "찜한 게시글 조회 성공",
                                    value = "{\n  " +
                                            "\"data\": [\n    " +
                                            "{\n      " +
                                            "\"idx\": 1,\n      " +
                                            "\"title\": \"게시글 제목\",\n      " +
                                            "\"price\": 10000,\n      " +
                                            "\"status\": true,\n      " +
                                            "\"createdAt\": \"2021-08-01T00:00:00\"\n    " +
                                            "}\n  ]\n}"
                            )
            )),
            @ApiResponse(responseCode = "400", description = "찜한 게시글 조회 실패",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @io.swagger.v3.oas.annotations.media.ExampleObject(
                                    name = "찜한 게시글 조회 실패",
                                    value = "{\"message\":\"찜한 게시글 조회 실패\"}"
            )))}
    )
    public ResponseEntity<BaseResponse<List<MarketReadRes>>> findAll(
            @Parameter(description = "페이지 번호", required = true) Integer page,
            @Parameter(description = "페이지 크기", required = true) Integer size,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        User user = customUserDetails.getUser();
        List<MarketReadRes> posts = markedService.findAll(user, page, size);
        return ResponseEntity.ok(new BaseResponse(posts));
    }
}