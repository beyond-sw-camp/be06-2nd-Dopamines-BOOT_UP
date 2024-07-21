package com.example.dopamines.domain.board.notice.service;

import com.example.dopamines.domain.board.notice.model.entity.Notice;
import com.example.dopamines.domain.board.notice.model.request.NoticeReq;
import com.example.dopamines.domain.board.notice.model.response.NoticeRes;
import com.example.dopamines.domain.board.notice.repository.NoticeRepository;
import com.example.dopamines.domain.board.notice.repository.NoticeRepositoryImpl;
import com.example.dopamines.global.common.BaseException;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.common.BaseResponseStatus;
import com.example.dopamines.global.infra.s3.CloudFileUploadService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticeRepositoryImpl noticeRepositoryImpl;
    private final CloudFileUploadService fileUploadService;

    // 공지사항 생성
    @Transactional
    public BaseResponse<NoticeRes> saveNotice(String title, String content, String category, Boolean isPrivate, String imageUrls ) {
        try {
            Notice notice = Notice.builder()
                    .title(title)
                    .content(content)
                    .date(LocalDateTime.now())
                    .category(category)
                    .isPrivate(isPrivate)
                    .imageUrls(List.of(imageUrls))
                    .build();
            Notice savedNotice = noticeRepository.save(notice);
            return new BaseResponse<>(new NoticeRes(savedNotice));
        } catch (Exception e) {
            return new BaseResponse<>(BaseResponseStatus.NOTICE_SAVE_FAILED);
        }
    }

    // 공지사항 조회
    public NoticeRes getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND));
        return new NoticeRes(notice);
    }

    public List<Notice> getAllNotices() {
        return noticeRepository.findAll();
    }

    public Page<Notice> getAllPublicNotices(Pageable pageable) {
        return noticeRepository.findByIsPrivateFalseOrderByDateDesc(pageable);
    }

    public Page<Notice> getNoticesByDateRange(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        return noticeRepository.findByIsPrivateFalseAndDateBetweenOrderByDateDesc(startDate, endDate, pageable);
    }

    public Page<Notice> getNoticesByCategory(String category, Pageable pageable) {
        return noticeRepository.findByIsPrivateFalseAndCategoryOrderByDateDesc(category, pageable);
    }

    public Page<Notice> getAllPrivateNotices(Pageable pageable) {
        return noticeRepository.findByIsPrivateTrueOrderByDateDesc(pageable);
    }

    // 공지사항 검색
    public Page<Notice> findNoticesByCriteria(Boolean isPrivate, String category, int page, int size) {
        return noticeRepositoryImpl.findNoticesByCriteria(isPrivate, category, page, size);
    }

    public Page<Notice> findNoticesByTitleAndContent(String title, String content, Pageable pageable) {
        return noticeRepositoryImpl.findNoticesByTitleAndContent(title, content, pageable);
    }



    // 공지사항 수정
    @Transactional
    public NoticeRes updateNotice(Long id, String title, String content, String category, Boolean isPrivate, String imageUrls) {
        try {

        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND));
        updateNoticeDetails(id, title, content, category, isPrivate, imageUrls);
        Notice savedNotice = noticeRepository.save(notice);
        return convertToNoticeResponseDto(savedNotice);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.NOTICE_UPDATE_FAILED);
        }
    }

    private NoticeRes convertToNoticeResponseDto(Notice notice) {
        return new NoticeRes(
                notice.getId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getDate(),
                notice.getCategory(),
                notice.isPrivate(),
                notice.getImageUrls());
    }

    private NoticeRes updateNoticeDetails(Long id, String title, String content, String category, Boolean isPrivate, String imageUrls) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND));
        notice.setTitle(title);
        notice.setContent(content);
        notice.setCategory(category);
        notice.setPrivate(isPrivate);
        notice.setImageUrls(List.of(imageUrls));
        Notice savedNotice = noticeRepository.save(notice);
        return new NoticeRes(savedNotice);
    }


    // 공지사항 삭제
    public void deleteNotice(Long id) {
        try {
            noticeRepository.deleteById(id);
        } catch (EntityNotFoundException e) {
            throw new BaseException(BaseResponseStatus.NOTICE_NOT_FOUND);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.NOTICE_DELETE_FAILED);
        }
    }
}