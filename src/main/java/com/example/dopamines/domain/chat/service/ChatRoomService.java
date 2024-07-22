package com.example.dopamines.domain.chat.service;

import com.example.dopamines.domain.board.market.mapper.MarketPostMapper;
import com.example.dopamines.domain.board.market.model.entity.MarketPost;
import com.example.dopamines.domain.board.market.repository.MarketPostRepository;
import com.example.dopamines.domain.chat.mapper.ChatMessageMapper;
import com.example.dopamines.domain.chat.mapper.ChatRoomMapper;
import com.example.dopamines.domain.chat.model.entity.ChatMessage;
import com.example.dopamines.domain.chat.model.entity.ChatRoom;
import com.example.dopamines.domain.chat.model.entity.ParticipatedChatRoom;
import com.example.dopamines.domain.chat.model.entity.request.ChatRoomReq;

import com.example.dopamines.domain.chat.model.response.ChatMessageRes;
import com.example.dopamines.domain.chat.model.response.ChatRoomRes;
import com.example.dopamines.domain.chat.repository.ChatMessageRepository;
import com.example.dopamines.domain.chat.repository.ChatRoomRepository;
import com.example.dopamines.domain.chat.repository.ParticipatedChatRoomRepository;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ParticipatedChatRoomRepository participatedChatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final MarketPostRepository marketPostRepository;

    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;

    private final ChatRoomMapper chatRoomMapper;

    private final ChatMessageMapper chatMessageMapper;
    private final MarketPostMapper marketPostMapper;

    public List<ChatRoomRes> findAll(User user) {
        List<ParticipatedChatRoom> chatRooms = participatedChatRoomRepository.findAllByUser(user);
        List<ChatRoomRes> dto = chatRooms.stream().map((room) ->{
                    Long postIdx = room.getChatRoom().getMarketPost().getIdx();
                    Optional<MarketPost> post = marketPostRepository.findByIdWithImages(postIdx);
                    String author = post.get().getUser().getName();

                    return ChatRoomRes.builder()
                            .idx(room.getChatRoom().getIdx())
                            .name(room.getChatRoom().getName())
                            .product(marketPostMapper.toDto(post.orElse(null), author))
                            .build();

                }

        ).collect(Collectors.toList());

        return dto;
    }


    public ChatRoomRes create(String name, Long receiverIdx, Long marketPostIdx, User sender) {
        // chat room 생성 -> uuid
        User receiver = User.builder()
                .idx(receiverIdx)
                .build();

        MarketPost marketPost = MarketPost.builder()
                .idx(marketPostIdx)
                .build();

        ChatRoom chatRoom = chatRoomMapper.toEntity(name, marketPost);
        chatRoomRepository.save(chatRoom);

        // participate repository에 추가 - sender, receiver 모두
        ParticipatedChatRoom senderRoom = ParticipatedChatRoom.builder()
                .chatRoom(chatRoom)
                .user(sender)
                .build();

        ParticipatedChatRoom receiverRoom = ParticipatedChatRoom.builder()
                .chatRoom(chatRoom)
                .user(receiver)
                .build();

        participatedChatRoomRepository.save(senderRoom);
        participatedChatRoomRepository.save(receiverRoom);

        return chatRoomMapper.toDto(chatRoom);
    }

    public List<ChatMessageRes> getAllMessage(String roomId) {
        List<ChatMessage> messages = chatMessageRepository.findAllById(roomId);
        List<ChatMessageRes> responses = messages.stream().map(
                (message) -> chatMessageMapper.toDto(message, message.getSender().getName())
        ).collect(Collectors.toList());
        return responses;
    }
}