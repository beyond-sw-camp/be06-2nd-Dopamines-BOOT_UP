package com.example.dopamines.domain.board.community.free.service;

import com.example.dopamines.domain.board.community.free.model.entity.FreePost;
import com.example.dopamines.domain.board.community.free.model.entity.FreeComment;
import com.example.dopamines.domain.board.community.free.model.entity.FreeRecomment;
import com.example.dopamines.domain.board.community.free.model.request.FreeRecommentReq;
import com.example.dopamines.domain.board.community.free.model.request.FreeRecommentUpdateReq;
import com.example.dopamines.domain.board.community.free.repository.FreePostRepository;
import com.example.dopamines.domain.board.community.free.repository.FreeCommentRepository;
import com.example.dopamines.domain.board.community.free.repository.FreeRecommentRepository;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.dopamines.global.common.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
public class FreeRecommentService {
    private final FreeCommentRepository freeCommentRepository;
    private final FreePostRepository freePostRepository;
    private final FreeRecommentRepository freeRecommentRepository;


    @Transactional
    public String create(User user, Long idx, String content) {
        FreeComment freeComment = freeCommentRepository.findById(idx).orElseThrow(()-> new BaseException(COMMUNITY_COMMENT_NOT_FOUND));
        FreePost freePost = freePostRepository.findById(freeComment.getFreePost().getIdx()).orElseThrow(() -> new BaseException(COMMUNITY_BOARD_NOT_FOUND));

        if(content == null){
            throw new BaseException(COMMUNITY_CONTENT_NOT_FOUND);
        }

        freeRecommentRepository.save(FreeRecomment.builder()
                .freeComment(freeComment)
                .content(content)
                .user(user)
                .createdAt(LocalDateTime.now())
                .build()
        );

        return "자유 게시판 대댓글 등록";
    }

    public String update(User user, Long idx, String content) {
        FreeRecomment freeRecomment = freeRecommentRepository.findById(idx).orElseThrow(()-> new BaseException(COMMUNITY_RECOMMENT_NOT_FOUND));
        FreeComment freeComment = freeCommentRepository.findById(freeRecomment.getFreeComment().getIdx()).orElseThrow(()-> new BaseException(COMMUNITY_COMMENT_NOT_FOUND));
        FreePost freePost = freePostRepository.findById(freeComment.getFreePost().getIdx()).orElseThrow(() -> new BaseException(COMMUNITY_BOARD_NOT_FOUND));

        if (freeRecomment.getUser().getIdx() != user.getIdx()){
            throw  new BaseException(COMMUNITY_USER_NOT_AUTHOR);
        }
        else{
            freeRecomment.setContent(content);
            freeRecomment.setCreatedAt(LocalDateTime.now());

            freeRecommentRepository.save(freeRecomment);
            return "대댓글 수정 완료";
        }
    }

    public String delete(User user, Long idx) {
        FreeRecomment freeRecomment = freeRecommentRepository.findById(idx).orElseThrow(()-> new BaseException(COMMUNITY_RECOMMENT_NOT_FOUND));

        if (freeRecomment.getUser().getIdx() != user.getIdx()){
            throw  new BaseException(COMMUNITY_USER_NOT_AUTHOR);
        }
        else{
            freeRecommentRepository.delete(freeRecomment);
            return "대댓글 삭제 완료";
        }
    }
}
