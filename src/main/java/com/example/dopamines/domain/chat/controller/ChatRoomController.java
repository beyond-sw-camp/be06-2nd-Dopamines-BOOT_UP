package com.example.dopamines.domain.chat.controller;

import com.example.dopamines.domain.chat.model.entity.request.ChatRoomReq;
import com.example.dopamines.domain.chat.model.response.ChatMessageRes;
import com.example.dopamines.domain.chat.model.response.ChatRoomRes;
import com.example.dopamines.domain.chat.service.ChatRoomService;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.annotation.CheckAuthentication;
import com.example.dopamines.global.security.CustomUserDetails;
import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
@Tag(name = "중고마켓", description = "채팅 관련 API")
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    // 채팅방을 만들어서 반환하는 기능
    @PostMapping(value = "/room")
    @Operation(
            summary = "채팅방 생성",
            description = "채팅방을 생성합니다.",
            operationId = "createRoom"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "채팅방 생성 성공",
                    content = @Content(
                            schema = @Schema(implementation = ChatRoomRes.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "채팅방 생성 성공",
                                    value = "{\n" +
                                            "  \"idx\": 1,\n" +
                                            "  \"name\": \"string\",\n" +
                                            "  \"product\": {\n" +
                                            "    \"idx\": 1,\n" +
                                            "    \"title\": \"string\",\n" +
                                            "    \"price\": 0,\n" +
                                            "    \"images\": [\n" +
                                            "      \"string\"\n" +
                                            "    ]\n" +
                                            "  }\n" +
                                            "}"
                    ))
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = "{\n" +
                                            "  \"status\": 401,\n" +
                                            "  \"message\": \"Unauthorized\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "유저 없음",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "유저 없음",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"message\": \"User Not Found\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = "{\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"message\": \"Internal Server Error\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            )
    })
    public ResponseEntity<BaseResponse<ChatRoomRes>> createRoom(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @Parameter(description = "채팅방 이름", required = false, example = "딸기쨈 구매") @RequestParam String name,
            @Parameter(description = "수신자 인덱스", required = true, example = "7") @RequestParam Long receiverIdx,
            @Parameter(description = "구매 인덱스", required = true, example = "4") @RequestParam Long marketPostIdx) {
        User sender = customUserDetails.getUser();
        ChatRoomRes res = chatRoomService.create(name, receiverIdx, marketPostIdx, sender);
        return ResponseEntity.ok(new BaseResponse<>(res));
    }

    // 현재 접속한 유저가 속한 채팅룸 리스트 받아오는 기능
    @GetMapping("/rooms")
    @Operation(
            summary = "채팅방 리스트",
            description = "현재 접속한 유저가 속한 채팅방 리스트를 받아옵니다.",
            operationId = "getChatRoomList"
    )
    @ApiResponses( value = {
            @ApiResponse(responseCode = "200", description = "채팅방 리스트 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ChatRoomRes.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "채팅방 리스트 조회 성공",
                                    value = "[\n" +
                                            "  {\n" +
                                            "    \"idx\": 1,\n" +
                                            "    \"name\": \"string\",\n" +
                                            "    \"product\": {\n" +
                                            "      \"idx\": 1,\n" +
                                            "      \"title\": \"string\",\n" +
                                            "      \"price\": 0,\n" +
                                            "      \"images\": [\n" +
                                            "        \"string\"\n" +
                                            "      ]\n" +
                                            "    }\n" +
                                            "  }\n" +
                                            "]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = "{\n" +
                                            "  \"status\": 401,\n" +
                                            "  \"message\": \"Unauthorized\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "유저 없음",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "유저 없음",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"message\": \"User Not Found\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = "{\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"message\": \"Internal Server Error\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            )
    }
    )
    public ResponseEntity<BaseResponse<List<ChatRoomRes>>> getChatRoomList(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        User user = customUserDetails.getUser();
        List<ChatRoomRes> participatedRooms = chatRoomService.findAll(user);
        return ResponseEntity.ok(new BaseResponse<>(participatedRooms));
    }

    // 채팅방 메시지들을 받아오는 기능
    @GetMapping("/rooms/{roomId}/messages")
    @Operation(
            summary = "메시지 리스트",
            description = "채팅방의 메시지 리스트를 받아옵니다.",
            operationId = "getMessages"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "메시지 리스트 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = ChatMessageRes.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "메시지 리스트 조회 성공",
                                    value = "[\n" +
                                            "  {\n" +
                                            "    \"idx\": 1,\n" +
                                            "    \"content\": \"string\",\n" +
                                            "    \"sender\": \"string\",\n" +
                                            "    \"type\": \"CHAT\",\n" +
                                            "    \"createdAt\": \"2021-08-31\"\n" +
                                            "  }\n" +
                                            "]"
                            )
                    )
            ),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "인증 실패",
                                    value = "{\n" +
                                            "  \"status\": 401,\n" +
                                            "  \"message\": \"Unauthorized\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "메시지 없음",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "메시지 없음",
                                    value = "{\n" +
                                            "  \"status\": 404,\n" +
                                            "  \"message\": \"Message Not Found\",\n" +
                                            "  \"data\": null\n" +
                                            "}"
                            )
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(
                            schema = @Schema(implementation = BaseResponse.class),
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "서버 오류",
                                    value = "{\n" +
                                            "  \"status\": 500,\n" +
                                            "  \"message\": \"Internal Server Error\",\n" +
                                            "  \"data\": null\n"
                            )
                    )
            )
    })
    public ResponseEntity<BaseResponse<List<ChatMessageRes>>> getMessages(
            @Parameter(description = "채팅방 인덱스", required = true, example = "3") @PathVariable String roomId) {
        List<ChatMessageRes> messages = chatRoomService.getAllMessage(roomId);
        return ResponseEntity.ok(new BaseResponse<>(messages));
    }
}