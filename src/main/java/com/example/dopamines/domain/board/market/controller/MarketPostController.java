package com.example.dopamines.domain.board.market.controller;

import static com.example.dopamines.global.common.BaseResponseStatus.UNAUTHORIZED_ACCESS;

import com.example.dopamines.domain.board.market.model.request.MarketCreateReq;
import com.example.dopamines.domain.board.market.model.response.MarketDetailRes;
import com.example.dopamines.domain.board.market.model.response.MarketReadRes;
import com.example.dopamines.domain.board.market.service.MarkedService;
import com.example.dopamines.domain.board.market.service.MarketService;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.annotation.CheckAuthentication;
import com.example.dopamines.global.infra.s3.CloudFileUploadService;
import com.example.dopamines.global.security.CustomUserDetails;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/market")
@Tag(name = "중고마켓", description = "거래 게시글 관련 API")
@RequiredArgsConstructor
public class MarketPostController {

    private final String BOARD_TYPE = "MARKET";
    private final MarketService marketService;
    private final MarkedService markedService;
    private final CloudFileUploadService cloudFileUploadService;


    @PostMapping
    @Operation(
            summary = "게시글 작성",
            description = "중고마켓 게시글을 작성합니다.",
            operationId = "create"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 작성 성공"),
                    @ApiResponse(responseCode = "400", description = "게시글 작성 실패"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "게시글 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    @CheckAuthentication
    public ResponseEntity<BaseResponse<?>> create(
            @Parameter(description = "이미지 url", required = false, example = "image.jpg") @RequestParam MultipartFile[] images,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "제목", required = true, example = "딸기쨈 팝니다") @RequestParam String title,
            @Parameter(description = "내용", required = true, example = "맛있어요") @RequestParam String content,
            @Parameter(description = "가격", required = true, example = "10000") @RequestParam Integer price
    ) {
        User user = customUserDetails.getUser();
        List<String> imagePathes = cloudFileUploadService.uploadImages(images, BOARD_TYPE);
        MarketReadRes post = marketService.add(imagePathes,  title, content, price, user);

        return ResponseEntity.ok(new BaseResponse(post));
    }

    @GetMapping
    @Operation(
            summary = "게시글 조회",
            description = "중고마켓 게시글을 조회합니다.",
            operationId = "findAll"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "게시글 조회 실패"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "게시글 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<BaseResponse<List<MarketReadRes>>> findAll(
            @Parameter(description = "페이지", required = true, example = "1") @RequestParam Integer page,
            @Parameter(description = "사이즈", required = true, example = "10") @RequestParam Integer size
    ) {
        List<MarketReadRes> posts = marketService.findAll(page, size);
        return ResponseEntity.ok(new BaseResponse(posts));
    }

    @GetMapping("/{idx}")
    @Operation(
            summary = "게시글 상세 조회",
            description = "중고마켓 게시글을 상세 조회합니다.",
            operationId = "findOne"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 상세 조회 성공"),
                    @ApiResponse(responseCode = "400", description = "게시글 상세 조회 실패"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "게시글 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<BaseResponse<MarketDetailRes>> findOne(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "페이지 인덱스", required = true, example = "1") @PathVariable("idx") Long idx
    ) {
        User user = customUserDetails.getUser();
        MarketDetailRes post = marketService.findById(idx);

        Boolean isMarked = markedService.checkMarked(user, idx);
        post.setMarked(isMarked);

        return ResponseEntity.ok(new BaseResponse(post));
    }

    @GetMapping("/search")
    @Operation(
            summary = "게시글 검색",
            description = "중고마켓 게시글을 검색합니다.",
            operationId = "search"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 검색 성공"),
                    @ApiResponse(responseCode = "400", description = "게시글 검색 실패"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "게시글 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<BaseResponse<List<MarketReadRes>>> search(Integer page, Integer size, String keyword, Integer minPrice, Integer maxPrice) {
        List<MarketReadRes> result = marketService.search(page, size, keyword, minPrice, maxPrice);
        return ResponseEntity.ok(new BaseResponse(result));
    }

    @PatchMapping("/{idx}/status")
    @Operation(
            summary = "게시글 상태 변경",
            description = "중고마켓 게시글의 상태를 변경합니다.",
            operationId = "updateStatus"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 상태 변경 성공"),
                    @ApiResponse(responseCode = "400", description = "게시글 상태 변경 실패"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "게시글 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity updateStatus(
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @PathVariable("idx") Long idx) {
        marketService.updateStatus(idx);
        return ResponseEntity.ok("");
    }

    @DeleteMapping("/{idx}")
    @Operation(
            summary = "게시글 삭제",
            description = "중고마켓 게시글을 삭제합니다.",
            operationId = "delete"
    )
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "게시글 삭제 실패"),
                    @ApiResponse(responseCode = "401", description = "인증 실패"),
                    @ApiResponse(responseCode = "403", description = "권한 없음"),
                    @ApiResponse(responseCode = "404", description = "게시글 없음"),
                    @ApiResponse(responseCode = "500", description = "서버 오류")
            }
    )
    public ResponseEntity<?> delete(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "게시글 인덱스", required = true, example = "1") @PathVariable("idx") Long idx) {
        boolean hasRoleAdmin = customUserDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!hasRoleAdmin) {
            return ResponseEntity.badRequest().body(new BaseResponse(UNAUTHORIZED_ACCESS));
        }

        marketService.delete(idx);
        return ResponseEntity.ok(new BaseResponse(""));
    }
}
