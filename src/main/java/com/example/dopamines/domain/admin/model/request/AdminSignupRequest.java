package com.example.dopamines.domain.admin.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminSignupRequest {
    private String email;
    private String password;
    private String name;
    private String nickname;
    private String phoneNumber;
}
