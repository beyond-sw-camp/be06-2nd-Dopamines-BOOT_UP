package com.example.dopamines.domain.admin.controller;

import com.example.dopamines.domain.admin.model.request.AdminSignupRequest;
import com.example.dopamines.domain.admin.model.request.UserAssignedRequest;
import com.example.dopamines.domain.admin.model.request.UserBlackRequest;
import com.example.dopamines.domain.admin.service.AdminService;
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

@RestController
@Tag(name = "관리자", description = "관리자 API")
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/signup")
    @Operation(
            summary = "관리자 회원가입",
            description = "관리자 회원가입을 합니다.",
            operationId = "signupAdmin"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "관리자 회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminSignupRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{\"" +
                                                    "email\": \"" +
                                                    "password\": \"" +
                                                    "name\": \"" +
                                                    "nickname\": \"" +
                                                    "phoneNumber\": \""
                                            )})
            ),
            @ApiResponse(responseCode = "400", description = "관리자 회원가입 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = AdminSignupRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Failure Example",
                                            value = "{\"" +
                                                    "email\": \"" +
                                                    "password\": \"" +
                                                    "name\": \"" +
                                                    "nickname\": \"" +
                                                    "phoneNumber\": \""
                                            )})
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> signupAdmin(
            @Parameter(description = "이메일", required = true, example = "superAdmin@example.com") @RequestParam String email,
            @Parameter(description = "비밀번호", required = true, example = "1234") @RequestParam String password,
            @Parameter(description = "이름", required = true, example = "admin kim") @RequestParam String name,
            @Parameter(description = "닉네임", required = true, example = "super Admin") @RequestParam String nickname,
            @Parameter(description = "전화번호", required = true, example = "010-1234-5678") @RequestParam String phoneNumber
    ) {
        adminService.signupAdmin(new AdminSignupRequest(email, password, name, nickname, phoneNumber));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/assign")
    @Operation(
            summary = "사용자 권한 부여",
            description = "사용자에게 이용 권한을 부여합니다.",
            operationId = "assign")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 권한 부여 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserAssignedRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{\"" +
                                                    "idx\": 1," +
                                                    "courseNum\": 6" +
                                                    "}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유저 권한 부여 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserAssignedRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Failure Example",
                                            value = "{\"" +
                                                    "idx\": 1," +
                                                    "courseNum\": 0" +
                                                    "}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> assignedUser(
            @Parameter(description = "사용자 인덱스", required = true, example = "1") @RequestParam Long idx,
            @Parameter(description = "사용자 기수", required = true, example = "6") @RequestParam Integer courseNum
            ) {
        adminService.setUserStatus(new UserAssignedRequest(idx, courseNum));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/user/black")
    @Operation(
            summary = "유저 블랙리스트 등록",
            description = "유저를 블랙리스트에 등록합니다.",
            operationId = "blackListUser"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 블랙리스트 등록 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserBlackRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Success Example",
                                            value = "{\"" +
                                                    "idx\": 1" +
                                                    "}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유저 블랙리스트 등록 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserBlackRequest.class),
                            examples = {
                                    @ExampleObject(
                                            name = "Failure Example",
                                            value = "{\"" +
                                                    "idx\": " +
                                                    "}"
                                    )
                            }
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Void> blackListUser(
            @Parameter(description = "유저 인덱스", required = true, example = "1") @RequestParam Long idx
            ) {
        adminService.setBlackList(new UserBlackRequest(idx));
        return new ResponseEntity<>(HttpStatus.OK);
    }
}