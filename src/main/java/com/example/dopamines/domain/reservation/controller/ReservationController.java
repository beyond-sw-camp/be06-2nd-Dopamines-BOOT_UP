package com.example.dopamines.domain.reservation.controller;

import com.example.dopamines.domain.reservation.model.request.ReservationReserveReq;
import com.example.dopamines.domain.reservation.model.request.SeatReadDetailReq;
import com.example.dopamines.domain.reservation.model.response.ReservationReadByUserRes;
import com.example.dopamines.domain.reservation.model.response.ReservationReadRes;
import com.example.dopamines.domain.reservation.model.response.ReservationReserveRes;
import com.example.dopamines.domain.reservation.model.response.SeatReadRes;
import com.example.dopamines.domain.reservation.service.ReservationService;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.BaseResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("reservation")
@Tag(name = "스터디 공간 예약", description = "예약 API")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping("/reserve")
    @Operation(
            summary = "예약하기",
            description = "예약하기",
            operationId = "reserve"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReservationReserveRes.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succee Example",
                                    value =  "{\"message\":\"예약 성공\"}"
                    ))),
            @ApiResponse(responseCode = "400", description = "예약 실패",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Failure Example",
                                    value = "{\"message\":\"예약 실패\"}"
                                    )
                    )
            )
    })
    public ResponseEntity<BaseResponse<?>> reserve(
            @Parameter(description = "사용자 인덱스", required = true, example = "1") @RequestParam Long userIdx,
            @Parameter(description = "좌석 인덱스", required = true, example = "1") @RequestParam Long seatIdx
    ) {
        ReservationReserveRes response = reservationService.reserve(ReservationReserveReq.builder()
                .userIdx(userIdx)
                .seatIdx(seatIdx)
                .build());
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }

    @GetMapping("/reservation-list")
    @Operation(
            summary = "예약 리스트",
            description = "예약 리스트",
            operationId = "reservationList"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 리스트 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReservationReadRes.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succee Example",
                                    value = "{\n" +
                                            "  \"idx\": \"1\",\n" +
                                            "  \"time\": \"2021-08-01 12:00:00\",\n" +
                                            "  \"status\": \"true\"\n"
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "예약 리스트 조회 실패",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Failure Example",
                                    value = "{\"message\":\"조회 실패\"}"
                                    )
                    )
            )
    })
    public ResponseEntity<BaseResponse<List<?>>> reservationMyList(
            @Parameter(description = "사용자 인덱스", required = true, example = "1") @RequestParam Long userIdx
    ) {
        List<ReservationReadByUserRes> response = reservationService.reservationMyList(userIdx);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }

    @GetMapping("/seat-list/{floor}")
    @Operation(
            summary = "층별 좌석 리스트",
            description = "층별 좌석 리스트",
            operationId = "seatList"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "층별 좌석 리스트 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = SeatReadRes.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succee Example",
                                    value = "{\"floor\": \"1\", \"section\": \"A\", \"status\": \"available\"}"
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "층별 좌석 리스트 조회 실패",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Failure Example",
                                    value = "{\"message\":\"조회 실패\"}"
                                    )
                    )
            )
    })
    public ResponseEntity<BaseResponse<List<?>>> seatList(
            @Parameter(description = "층", required = true, example = "1") @PathVariable("floor") Integer floor
    ) {
        List<SeatReadRes> response = reservationService.seatList(floor);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }

    @GetMapping("/seat-list-detail")
    @Operation(
            summary = "층별 좌석 리스트 상세",
            description = "층별 좌석 리스트 상세",
            operationId = "seatListDetail"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "층별 좌석 리스트 상세 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ReservationReadRes.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succee Example",
                                    value = "{\n" +
                                            "  \"idx\": \"1\",\n" +
                                            "  \"time\": \"2021-08-01 12:00:00\",\n" +
                                            "  \"status\": \"true\"\n"
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "층별 좌석 리스트 상세 조회 실패",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Failure Example",
                                    value = "{\"message\":\"조회 실패 \"}"
                                    )
                    )
            )
    })
    public ResponseEntity<BaseResponse<List<?>>> seatListDetail(
            @Parameter(description = "층", required = true, example = "1") @RequestParam Integer floor,
            @Parameter(description = "섹션", required = true, example = "A") @RequestParam String section) {
        List<ReservationReadRes> response = reservationService.seatListDetail(floor, section);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(response));
    }

    @DeleteMapping("/cancel/{idx}")
    @Operation(
            summary = "예약 취소",
            description = "예약 취소",
            operationId = "cancel"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "예약 취소 성공",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Succee Example",
                                    value = "{\"message\":\"예약 취소 성공\"}"
                            )
                    )),
            @ApiResponse(responseCode = "400", description = "예약 취소 실패",
                    content = @Content(
                            schema = @Schema(
                                    implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Failure Example",
                                    value = "{\"message\":\"예약 취소 실패\"}"
                                    )
                    )
            )
    })
    public ResponseEntity<BaseResponse<?>> cancel(
            @Parameter(description = "예약 인덱스", required = true, example = "1") @PathVariable Long idx
    ) {
        reservationService.cancel(idx);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(BaseResponseStatus.SUCCESS_NO_CONTENT));
    }
}