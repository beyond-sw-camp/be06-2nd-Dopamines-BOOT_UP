package com.example.dopamines.domain.user.controller;

import com.example.dopamines.domain.user.model.request.UserSignupRequest;
import com.example.dopamines.domain.user.model.response.UserSignupResponse;
import com.example.dopamines.domain.user.service.UserEmailService;
import com.example.dopamines.domain.user.service.UserService;
import com.example.dopamines.global.common.BaseException;
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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "회원", description = "회원 API")
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserEmailService emailService;

    @PostMapping("/signup")
    @Operation(
            summary = "사용자 회원가입",
            description = "사용자 회원가입을 합니다.",
            operationId = "signupUser"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 회원가입 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSignupResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    value = "{\n" +
                                            "\"userIdx\": 1\n" +
                                            "\"email\": \"user1@example.com\"" +
                                            "\"password\": \"password1234\"" +
                                            "\"name\": \"홍길동\"" +
                                            "\"nickname\": \"길동쓰\"" +
                                            "\"teamName\": \"호형호제즈\"" +
                                            "\"phoneNumber\": \"010-1234-5678\"" +
                                            "\"address\": \"서울시 강남구\"" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "유저 회원가입 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseException.class),
                            examples = @ExampleObject(
                                    name = "Fail Example",
                                    value = "{\n" +
                                            " \"userIdx\": 1\n" +
                                            "\"email\": \"user1@example.com\"" +
                                            "\"password\": \"password1234\"" +
                                            "\"name\": \"홍길동\"" +
                                            "\"nickname\": \"길동쓰\"" +
                                            "\"teamName\": \"호형호제즈\"" +
                                            "\"phoneNumber\": \"010-1234-5678\"" +
                                            "\"address\": \"서울시 강남구\"" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> signup(
            @Parameter(description = "이메일", required = true, example = "user1@example.com") @Email @RequestParam String email,
            @Parameter(description = "비밀번호", required = true, example = "password1234")@RequestParam String password,
            @Parameter(description = "이름", required = true, example = "홍길동")@RequestParam String name,
            @Parameter(description = "닉네임", required = true, example = "길동쓰")@RequestParam String nickname,
            @Parameter(description = "팀 이름", required = true, example = "호형호제즈")@RequestParam String teamName,
            @Parameter(description = "휴대폰 번호", required = true, example = "010-1234-5678")@RequestParam String phoneNumber,
            @Parameter(description = "주소", required = true, example = "서울시 강남구")@RequestParam String address
            ){

        UserSignupResponse signupResult = userService.signup(email, password, name, nickname, teamName, phoneNumber, address);

        // 인증을 할 uuid를 생성하고 일단 저장
        String getUuid = emailService.sendEmail(email);
        emailService.save(email, getUuid);

        return ResponseEntity.ok(new BaseResponse<>(signupResult));
    }

    @GetMapping("/active")
    @Operation(
            summary = "사용자 인증",
            description = "사용자 인증을 합니다.",
            operationId = "active")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 인증 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    value = "{\n" +
                                            "\"message\": \"사용자 인증 성공\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "사용자 인증 실패",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BaseResponse.class),
                            examples = @ExampleObject(
                                    name = "Fail Example",
                                    value = "{\n" +
                                            "\"message\": \"사용자 인증 실패\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> setActiveUser(
            @Parameter(description = "이메일", required = true, example = "user00@example.com") @RequestBody String email,
            @Parameter(description = "uuid", required = true, example = "1234") @RequestBody String uuid){
        emailService.verifyUser(email, uuid);
        userService.setActiveOn(email);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.USER_ACCESS_SUCCESS));
    }

}
