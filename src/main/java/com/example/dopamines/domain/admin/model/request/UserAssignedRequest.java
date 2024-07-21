package com.example.dopamines.domain.admin.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserAssignedRequest {
    private Long idx;
    private Integer courseNum;
}
