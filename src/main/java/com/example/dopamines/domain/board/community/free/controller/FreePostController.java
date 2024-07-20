package com.example.dopamines.domain.board.community.free.controller;

import com.example.dopamines.domain.board.community.free.model.request.FreePostReq;
import com.example.dopamines.domain.board.community.free.model.request.FreePostUpdateReq;
import com.example.dopamines.domain.board.community.free.model.response.FreePostReadRes;
import com.example.dopamines.domain.board.community.free.model.response.FreePostRes;
import com.example.dopamines.domain.board.community.free.service.FreePostService;
import com.example.dopamines.domain.user.model.entity.User;
import com.example.dopamines.global.common.BaseResponse;
import com.example.dopamines.global.infra.s3.CloudFileUploadService;
import com.example.dopamines.global.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Tag(name = "자유게시판", description = "자유게시판 API")
@RequestMapping("/free/post")
@RequiredArgsConstructor
public class FreePostController {
    private final FreePostService freePostService;
    private final CloudFileUploadService cloudFileUploadService;

    @PostMapping("/create")
    @Operation(
            summary = "게시글 생성",
            description = "자유게시판 게시글을 생성합니다.",
            tags = "자유게시판 게시글 생성",
            operationId = "createFreePost")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "게시글 생성 실패"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<BaseResponse<?>> create(

            @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestPart FreePostReq req, @RequestPart MultipartFile[] files){
        User user = customUserDetails.getUser();
        String rootType ="FREEBOARD";
        List<String> urlLists = cloudFileUploadService.uploadImages(files, rootType);
        String result = freePostService.create(user, req, urlLists);
        return ResponseEntity.ok(new BaseResponse<>(result));
    }

    @GetMapping("/read")
    public ResponseEntity<BaseResponse<?>> read(@AuthenticationPrincipal CustomUserDetails customUserDetails, Long idx){
        User user = customUserDetails.getUser();
        FreePostReadRes response = freePostService.read(idx);

        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @GetMapping("/read-all")
    public ResponseEntity<BaseResponse<?>> readAll(@AuthenticationPrincipal CustomUserDetails customUserDetails,Integer page, Integer size){
        User user = customUserDetails.getUser();
        List<FreePostRes> response = freePostService.readAll(page,size);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @PutMapping("/update")
    public ResponseEntity<BaseResponse<?>> update(@AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestPart FreePostUpdateReq req, @RequestPart MultipartFile[] files){
        User user = customUserDetails.getUser();
        String rootType ="FREEBOARD";
        List<String> urlLists = cloudFileUploadService.uploadImages(files, rootType);
        FreePostRes response = freePostService.update(user,req,urlLists);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<BaseResponse<?>> delete(@AuthenticationPrincipal CustomUserDetails customUserDetails, Long idx){
        User user = customUserDetails.getUser();
        String response = freePostService.delete(user,idx);
        return ResponseEntity.ok(new BaseResponse<>(response));
    }

//    @GetMapping("/search")
//    public ResponseEntity<BaseResponse<?>> search(@AuthenticationPrincipal CustomUserDetails customUserDetails,Integer page, Integer size,String keyword){
//        User user = customUserDetails.getUser();
//        List<FreePostRes> response = freePostService.search(page,size,keyword);
//        return ResponseEntity.ok(new BaseResponse<>(response));
//
//    }
}
