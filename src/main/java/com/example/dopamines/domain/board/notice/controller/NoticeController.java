package com.example.dopamines.domain.board.notice.controller;

import com.example.dopamines.domain.board.notice.model.entity.Notice;
import com.example.dopamines.domain.board.notice.model.request.NoticeReq;
import com.example.dopamines.domain.board.notice.model.response.NoticeRes;
import com.example.dopamines.domain.board.notice.service.NoticeService;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.BaseResponseStatus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import io.swagger.v3.oas.annotations.Operation;

@RestController
@Tag(name = "공지사항", description = "공지사항 관련 API")
@RequestMapping("/notices")
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    // 공지사항 생성
    @PostMapping("/create")
    @Operation(
            summary = "공지사항 생성",
            description = "공지사항을 생성합니다.",
            tags = "create",
            operationId = "createNotice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "공지사항 생성 성공"),
            @ApiResponse(responseCode = "400", description = "공지사항 생성 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<BaseResponse<NoticeRes>>> createNotice(
            @Parameter(description = "제목", required = true, example = "주말 코딩 테스트 응시 안내") @RequestParam String title,
            @Parameter(description = "내용", required = true, example = "아래 폼을 작성하면 응시 확인이 가능합니다.") @RequestParam String content,
            @Parameter(description = "카테고리", required = true, example = "행사 안내") @RequestParam String category,
            @Parameter(description = "공개 여부", required = true, example = "true") @RequestParam Boolean isPrivate,
            @Parameter(description = "이미지 URL", required = false, example = "http://example.com/image1.jpg") @RequestParam(required = false) List<String> imageUrls,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "공지사항 생성 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{" +
                                                    "\"title\": \"주말 특강 공지\", " +
                                                    "\"content\": \"다음 주말 헥사고널 아키텍쳐에 대한 특강이 개설됩니다..\", " +
                                                    "\"category\": \"특강 공지\", " +
                                                    "\"isPrivate\": false, " +
                                                    "\"imageUrls\": [\"http://example.com/image1.jpg\", \"http://example.com/image2.jpg\"]}"
                                    ),
                                    @ExampleObject(
                                            name = "Failure Example",
                                            value = "{\"" +
                                                    "title\": \"\"," +
                                                    " \"content\": \"\"," +
                                                    " \"category\": \"\"," +
                                                    " \"isPrivate\": false," +
                                                    " \"imageUrls\": []}"
                                    )
                            }
                    )
            ) NoticeReq noticeRequestDto) {

        BaseResponse<NoticeRes> createdNotice = noticeService.saveNotice(title, content, category, isPrivate, imageUrls);
        return ResponseEntity.status(HttpStatus.CREATED).body(new BaseResponse<>(createdNotice));
    }

    // 공지사항 조회
    @GetMapping("/{id}")
    @Operation(
            summary = "공지사항 조회",
            description = "공지사항을 조회합니다.",
            tags = "get",
            operationId = "getNotice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 조회 성공"),
            @ApiResponse(responseCode = "404", description = "공지사항 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<NoticeRes>> getNotice(
            @Parameter(description = "공지사항 ID", required = true, example = "1")
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 조회 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(name = "Success Example",
                                            value = "{" +
                                                    "\"id\": 1" +
                                                    "}"
                                    ),
                                    @ExampleObject(name = "Failure Example",
                                            value = "{\"" +
                                                    "id\": 0" +
                                                    "}"
                                    )
                            }
                    )
            )
            @PathVariable Long id) {
        Optional<NoticeRes> noticeOptional = Optional.ofNullable(noticeService.getNotice(id));
        if (noticeOptional.isPresent()) {
            BaseResponse<NoticeRes> response = new BaseResponse<>(noticeOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } else {
            BaseResponse<NoticeRes> response = new BaseResponse<>(BaseResponseStatus.NOTICE_NOT_FOUND);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/private")
    @Operation(
            summary = "비공개 공지사항 조회",
            description = "비공개 처리된 공지사항을 조회합니다.",
            tags = "get",
            operationId = "getAllPrivateNotices")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비공개 공지사항 조회 성공"),
            @ApiResponse(responseCode = "404", description = "비공개 공지사항 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<Page<Notice>>> getAllPrivateNotices(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "비공개 공지사항 조회 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(name = "Success Example",
                                            value = "{" +
                                                    "\"page\": 0, " +
                                                    "\"size\": 10" +
                                                    "}"
                                    ),
                                    @ExampleObject(name = "Failure Example",
                                            value = "{\"" +
                                                    "page\": 0," +
                                                    " \"size\": 0" +
                                                    "}"
                                    )
                            }
                    )
            )
            Pageable pageable
    ) {
        Page<Notice> notices = noticeService.getAllPrivateNotices(pageable);
        return ResponseEntity.ok(new BaseResponse<>(notices));
    }


    @GetMapping("/category")
    @Operation(summary = "카테고리별 공지사항 조회", description = "카테고리별 공지사항을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "카테고리별 공지사항 조회 성공"),
            @ApiResponse(responseCode = "404", description = "카테고리별 공지사항 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<Page<Notice>>> getNoticesByCategory(
            @Parameter(description = "카테고리", required = true, example = "행사 안내") @RequestParam String category,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "카테고리별 공지사항 조회 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(name = "Success Example",
                                            value = "{" +
                                                    "\"category\": \"행사 안내\", " +
                                                    "\"page\": 0, " +
                                                    "\"size\": 10" +
                                                    "}"
                                    ),
                                    @ExampleObject(name = "Failure Example",
                                            value = "{\"" +
                                                    "category\": \"\"," +
                                                    " \"page\": 0," +
                                                    " \"size\": 0" +
                                                    "}"
                                    )
                            }
                    )
            )
            Pageable pageable) {
        Page<Notice> notices = noticeService.getNoticesByCategory(category, pageable);
        return ResponseEntity.ok(new BaseResponse<>(notices));
    }

    @GetMapping("/date")
    @Operation(summary = "날짜별 공지사항 조회", description = "날짜별 공지사항을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "날짜별 공지사항 조회 성공"),
            @ApiResponse(responseCode = "404", description = "날짜별 공지사항 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<Page<Notice>>> getNoticesByDateRange(
            @Parameter(description = "시작 날짜", required = true, example = "2022-01-01T00:00:00")
            @RequestParam String startDate, @RequestParam String endDate,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "날짜별 공지사항 조회 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(name = "Success Example",
                                            value = "{" +
                                                    "\"startDate\": \"2022-01-01T00:00:00\", " +
                                                    "\"endDate\": \"2022-01-31T23:59:59\", " +
                                                    "\"page\": 0, " +
                                                    "\"size\": 10" +
                                                    "}"
                                    ),
                                    @ExampleObject(name = "Failure Example",
                                            value = "{\"" +
                                                    "startDate\": \"\"," +
                                                    " \"endDate\": \"\"," +
                                                    " \"page\": 0," +
                                                    " \"size\": 0" +
                                                    "}"
                                    )
                            }
                    )
            )
            Pageable pageable) {
        LocalDateTime start = LocalDateTime.parse(startDate);
        LocalDateTime end = LocalDateTime.parse(endDate);
        Page<Notice> notices = noticeService.getNoticesByDateRange(start, end, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(notices));
    }

    // 공지사항 검색
    @GetMapping("/notices/criteria")
    @Operation(
            summary = "검색 조건에 따른 공지사항 조회",
            description = "검색 조건에 따라 공지사항을 조회합니다.",
            tags = "search",
            operationId = "findNoticesByCriteria")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "검색 조건에 따른 공지사항 조회 성공"),
            @ApiResponse(responseCode = "404", description = "검색 조건에 따른 공지사항 조회 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public Page<Notice> findNoticesByCriteria(
            @Parameter(description = "비공개 여부", required = false, example = "true")
            @RequestParam(required = false) Boolean isPrivate,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "검색 조건에 따른 공지사항 조회 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(name = "Success Example",
                                            value = "{" +
                                                    "\"isPrivate\": true, " +
                                                    "\"category\": \"행사 안내\", " +
                                                    "\"page\": 0, " +
                                                    "\"size\": 10" +
                                                    "}"
                                    ),
                                    @ExampleObject(name = "Failure Example",
                                            value = "{\"" +
                                                    "isPrivate\": false," +
                                                    " \"category\": \"\"," +
                                                    " \"page\": 0," +
                                                    " \"size\": 0" +
                                                    "}"
                                    )
                            }
                    )
            ) Pageable pageable) {
        return noticeService.findNoticesByCriteria(isPrivate, category, page, size);
    }

    @GetMapping("/notices/search")
    @Operation(
            summary = "제목 및 내용 검색",
            description = "제목 및 내용으로 공지사항을 검색합니다.",
            tags = "search",
            operationId = "findNoticesByTitleAndContent")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "제목 및 내용 검색 성공"),
            @ApiResponse(responseCode = "404", description = "제목 및 내용 검색 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public Page<Notice> findNoticesByTitleAndContent(
            @Parameter(description = "제목", required = false, example = "주말 특강 공지")
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "제목 및 내용 검색 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(name = "Success Example",
                                            value = "{" +
                                                    "\"title\": \"주말 특강 공지\", " +
                                                    "\"content\": \"다음 주말 헥사고널 아키텍쳐에 대한 특강이 개설됩니다.\", " +
                                                    "\"page\": 0, " +
                                                    "\"size\": 10" +
                                                    "}"
                                    ),
                                    @ExampleObject(name = "Failure Example",
                                            value = "{\"" +
                                                    "title\": \"\"," +
                                                    " \"content\": \"\"," +
                                                    " \"page\": 0," +
                                                    " \"size\": 0" +
                                                    "}"
                                    )
                            }
                    )
            )
            Pageable pageable) {
        return noticeService.findNoticesByTitleAndContent(title, content, pageable);
    }

    // 공지사항 수정
    @PutMapping("/{id}")
    @Operation(
            summary = "공지사항 수정",
            description = "공지사항을 수정합니다.",
            tags = "update",
            operationId = "updateNotice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "공지사항 수정 성공"),
            @ApiResponse(responseCode = "400", description = "공지사항 수정 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<Notice>> updateNotice(
            @Parameter(description = "공지사항 ID", required = true, example = "1") @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 수정 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = @ExampleObject(name = "Update Example",
                                    value = "{" +
                                            "\"title\": \"Updated Title\", " +
                                            "\"content\": \"Updated content details.\", " +
                                            "\"category\": \"Updated Category\", " +
                                            "\"isPrivate\": false, " +
                                            "\"imageUrls\": [\"http://example.com/updatedimage1.jpg\", " +
                                            "\"http://example.com/updatedimage2.jpg\"]}"
                            )
                    )
            ) @RequestBody NoticeReq noticeRequestDto
    ) {
        Notice updatedNotice = noticeService.updateNotice(id, noticeRequestDto).toEntity();
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(updatedNotice));
    }

    // 공지사항 삭제
    @DeleteMapping("/{id}")
    @Operation(
            summary = "공지사항 삭제",
            description = "공지사항을 삭제합니다.",
            tags = "delete",
            operationId = "deleteNotice")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "공지사항 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "공지사항 삭제 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<Void>> deleteNotice(
            @Parameter(description = "공지사항 ID", required = true, example = "1")
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "공지사항 삭제 요청", required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = NoticeReq.class),
                            examples = {
                                    @ExampleObject(name = "Success Example",
                                            value = "{" +
                                                    "\"id\": 1" +
                                                    "}"
                                    ),
                                    @ExampleObject(name = "Failure Example",
                                            value = "{\"" +
                                                    "id\": 0" +
                                                    "}"
                                    )
                            }
                    )
            ) NoticeReq noticeRequestDto) {
        noticeService.deleteNotice(id);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(BaseResponseStatus.SUCCESS_NO_CONTENT));
    }
}