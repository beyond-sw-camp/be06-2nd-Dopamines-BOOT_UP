package com.example.dopamines.domain.chat.model.entity.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatRoomReq {
    private String name;
    private Long receiverIdx;
    private Long marketPostIdx;
}