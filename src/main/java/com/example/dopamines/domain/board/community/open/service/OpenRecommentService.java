package com.example.dopamines.domain.board.community.open.service;

import com.example.dopamines.domain.board.community.open.model.entity.OpenPost;
import com.example.dopamines.domain.board.community.open.model.entity.OpenComment;
import com.example.dopamines.domain.board.community.open.model.entity.OpenRecomment;
import com.example.dopamines.domain.board.community.open.model.request.OpenRecommentReq;
import com.example.dopamines.domain.board.community.open.model.request.OpenRecommentUpdateReq;
import com.example.dopamines.domain.board.community.open.repository.OpenPostRepository;
import com.example.dopamines.domain.board.community.open.repository.OpenCommentRepository;
import com.example.dopamines.domain.board.community.open.repository.OpenRecommentRepository;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.dopamines.global.common.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class OpenRecommentService {
    private final OpenCommentRepository openCommentRepository;
    private final OpenPostRepository openPostRepository;
    private final OpenRecommentRepository openRecommentRepository;


    @Transactional
    public String create(User user, Long idx, String content) {
        OpenComment openComment = openCommentRepository.findById(idx).orElseThrow(()-> new BaseException(COMMUNITY_COMMENT_NOT_FOUND));
        OpenPost openPost = openPostRepository.findById(openComment.getOpenPost().getIdx()).orElseThrow(() -> new BaseException(COMMUNITY_BOARD_NOT_FOUND));

        if(content == null){
            throw new BaseException(COMMUNITY_CONTENT_NOT_FOUND);
        }

        openRecommentRepository.save(OpenRecomment.builder()
                .openComment(openComment)
                .content(content)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build()
        );

        return "자유 게시판 대댓글 등록";
    }

    public String update(User user, Long idx, String content) {
        OpenRecomment openRecomment = openRecommentRepository.findById(idx).orElseThrow(()-> new BaseException(COMMUNITY_RECOMMENT_NOT_FOUND));
        OpenComment openComment = openCommentRepository.findById(openRecomment.getOpenComment().getIdx()).orElseThrow(()-> new BaseException(COMMUNITY_COMMENT_NOT_FOUND));
        OpenPost openPost = openPostRepository.findById(openComment.getOpenPost().getIdx()).orElseThrow(() -> new BaseException(COMMUNITY_BOARD_NOT_FOUND));

        if (openRecomment.getUser().getIdx() != user.getIdx()){
            throw  new BaseException(COMMUNITY_USER_NOT_AUTHOR);
        }
        else{
            openRecomment.setContent(content);
            openRecomment.setCreatedAt(LocalDateTime.now());

            openRecommentRepository.save(openRecomment);
            return "대댓글 수정 완료";
        }
    }

    public String delete(User user, Long idx) {
        OpenRecomment openRecomment = openRecommentRepository.findById(idx).orElseThrow(()-> new BaseException(COMMUNITY_RECOMMENT_NOT_FOUND));

        if (openRecomment.getUser().getIdx() != user.getIdx()){
            throw  new BaseException(COMMUNITY_USER_NOT_AUTHOR);
        }
        else{
            openRecommentRepository.delete(openRecomment);
            return "대댓글 삭제 완료";
        }
    }
}
