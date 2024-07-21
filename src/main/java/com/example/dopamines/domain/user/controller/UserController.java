package com.example.dopamines.domain.user.controller;

import com.example.dopamines.domain.user.model.request.UserSignupReq;
import com.example.dopamines.domain.user.model.response.UserActiveOnRes;
import com.example.dopamines.domain.user.model.response.UserSignupRes;
import com.example.dopamines.domain.user.service.UserEmailService;
import com.example.dopamines.domain.user.service.UserService;
import com.example.dopamines.global.common.BaseResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserEmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<BaseResponse<?>> singup(@Valid @RequestBody UserSignupReq request){

        UserSignupRes signupResult = userService.signup(request);

        // 인증을 할 uuid를 생성하고 일단 저장
        String getUuid = emailService.sendEmail(request);
        emailService.save(request,getUuid);

        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(signupResult));
    }

    @GetMapping("/active")
    public ResponseEntity<BaseResponse<?>> setActiveUser(String email, String uuid){
        //요청 이메일 및 uuid와 서버 uuid 비교
        emailService.verifyUser(email, uuid);
//        if(successVerifying){
        UserActiveOnRes result = userService.setActiveOn(email);
        return ResponseEntity.status(HttpStatus.OK).body(new BaseResponse<>(result));
//        }
//        return ResponseEntity.ok(new BaseResponse<>(BaseResponseStatus.USER_UNABLE_USER_ACCESS));
    }
}
