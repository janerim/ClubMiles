package org.triple.ClubMiles.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.triple.ClubMiles.controller.common.ErrorCode;
import org.triple.ClubMiles.controller.common.Response;
import org.triple.ClubMiles.domain.User;
import org.triple.ClubMiles.dto.PhotoResDto;
import org.triple.ClubMiles.dto.PointHistoryResDto;
import org.triple.ClubMiles.exception.UserNotExistException;
import org.triple.ClubMiles.repository.UserRepository;
import org.triple.ClubMiles.service.EventService;
import org.triple.ClubMiles.service.SupportService;
import org.triple.ClubMiles.util.IdGeneration;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(produces = "지원 Controller")
public class SupportController {
    @Autowired
    private SupportService supportService;

    @Autowired
    private IdGeneration idGeneration;

    @GetMapping("/reviewId")
    @ApiOperation(value = "리뷰 ID 생성", notes = "")
    public Response<String> generatorReviewId(){
        return Response.OK("", idGeneration.idGenerator());
    }

    @GetMapping("/photo")
    @ApiOperation(value = "첨부사진 리스트 조회", notes = "" +
            "- reveiwId 존재: 특정 리뷰에 업로드된 모든 첨부사진"+
            "- reveiwId 미존재: 전체 첨부사진")
    public Response<List<PhotoResDto>> photoList(String reviewId){
        return Response.OK("", supportService.photoList(reviewId));
    }

    @GetMapping("/pointHistory")
    @ApiOperation(value = "포인트 히스토리 조회", notes = "")
    public Response<List<PointHistoryResDto>> pointHistory(){
        return Response.OK("", supportService.pointHistory());
    }


    @PostMapping("/photo")
    @ApiOperation(value = "첨부 사진 업로드", notes = ""
            + "- 사진 형식은 jpg, png만 가능\n")
    public Response<String> photo(@RequestPart(value = "file") MultipartFile file) throws IOException {
        return Response.OK("", supportService.photoUpload(file));
    }

    @DeleteMapping("/photo")
    @ApiOperation(value = "첨부 사진 삭제", notes = "")
    public Response<String> photo(@RequestParam String photoId){
        return Response.OK("", supportService.photoDelete(photoId));
    }


}
