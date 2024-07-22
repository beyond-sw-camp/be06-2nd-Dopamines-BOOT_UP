package com.example.dopamines.domain.chat.controller;


import com.example.dopamines.domain.chat.model.request.ChatMessageReq;
import com.example.dopamines.domain.chat.service.MessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;


@Slf4j
@Controller
@RequiredArgsConstructor
@Tag(name = "중고마켓", description = "채팅 관련 API")
public class ChatController {

    private final KafkaTemplate<String, ChatMessageReq> kafkaTemplate;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final ObjectMapper objectMapper;

    @MessageMapping("/chat.sendMessage/{roomId}")
    @Operation(
            summary = "메시지 전송",
            description = "채팅방에 메시지를 전송합니다.",
            operationId = "sendMessage"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 전송 성공",
                    content = @Content(
                            schema = @Schema(implementation = ChatMessageReq.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "메시지 전송 성공",
                                    value = "{\n" +
                                            "  \"roomId\": \"string\",\n" +
                                            "  \"content\": \"string\",\n" +
                                            "  \"sender\": \"string\",\n" +
                                            "  \"type\": \"CHAT\",\n" +
                                            "  \"createdAt\": \"2021-08-31\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "메시지 전송 실패",
                    content = @Content(
                            schema = @Schema(implementation = ChatMessageReq.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "메시지 전송 실패",
                                    value = "{\"message\":\"예약 실패\"}"
                            )
                    )
            )
    })
    public ChatMessageReq sendMessage(
            @Parameter(description = "채팅방 ID", required = true, example = "1") @RequestParam String roomId,
            @Header("Authorization") String authHeader ,
            @Parameter(description = "채팅 메시지", required = true, example = "살래요") @RequestParam ChatMessageReq chatMessage) {
        log.info("[SENDER - {}] messages : {}", chatMessage.getSender(), chatMessage.getContent());
        String bearerToken = authHeader.split(" ")[1];
        chatMessage = messageService.sendMessage(bearerToken, chatMessage); // db 저장
        //kafkaTemplate.send("chat-room", chatMessage); // 카프카에 메시지 전송
        return chatMessage;
    }



    @KafkaListener(topicPattern = "chat-room")
    @Operation(
            summary = "메시지 수신",
            description = "채팅방에서 메시지를 수신합니다.",
            operationId = "consumeMessage"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 수신 성공",
                    content = @Content(
                            schema = @Schema(implementation = ChatMessageReq.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "메시지 수신 성공",
                                    value = "{\n" +
                                            "  \"roomId\": \"string\",\n" +
                                            "  \"content\": \"string\",\n" +
                                            "  \"sender\": \"string\",\n" +
                                            "  \"type\": \"CHAT\",\n" +
                                            "  \"createdAt\": \"2021-08-31\"\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "400", description = "메시지 수신 실패",
                    content = @Content(
                            schema = @Schema(implementation = ChatMessageReq.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "메시지 수신 실패",
                                    value = "{\"message\":\"예약 실패\"}"
                            )
                    )
            )
    })
    public void consumeMessage(ConsumerRecord<String, Object> record) throws JsonProcessingException {
        String message = objectMapper.writeValueAsString(record.value());
        ChatMessageReq sendMessageReq = objectMapper.readValue(message, ChatMessageReq.class);
        messagingTemplate.convertAndSend("/sub/room/" + sendMessageReq.getRoomId(), sendMessageReq);
    }
}