package com.example.dopamines.domain.user.controller;

import com.example.dopamines.domain.user.model.request.UserSignupRequest;
import com.example.dopamines.domain.user.model.response.UserSignupResponse;
import com.example.dopamines.domain.user.service.UserEmailService;
import com.example.dopamines.domain.user.service.UserService;
import com.example.dopamines.global.common.BaseException;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.BaseResponseStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "유저", description = "유저 API")
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserEmailService emailService;

    @PostMapping("/signup")
    @Operation(
            summary = "유저 회원가입",
            description = "유저 회원가입을 합니다.",
            tags = {"유저 회원가입"},
            operationId = "signupUser"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유저 회원가입 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> signup(

            @Valid @RequestBody UserSignupRequest request){

        UserSignupResponse signupResult = userService.signup(request);

        // 인증을 할 uuid를 생성하고 일단 저장
        String getUuid = emailService.sendEmail(request);
        emailService.save(request,getUuid);

        return ResponseEntity.ok(new BaseResponse<>(signupResult));
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse<?>> setActiveUser(String email, String uuid){
        //요청 이메일 및 uuid와 서버 uuid 비교
        emailService.verifyUser(email, uuid);
//        if(successVerifying){
        userService.setActiveOn(email);
        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.USER_ACCESS_SUCCESS));
//        }
//        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.USER_UNABLE_USER_ACCESS));
    }

    @GetMapping("/test")
    public void test(){
        userService.insertUserAndTeam();
    }
}
