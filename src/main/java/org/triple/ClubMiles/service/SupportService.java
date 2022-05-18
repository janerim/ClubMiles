package org.triple.ClubMiles.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.triple.ClubMiles.controller.common.ErrorCode;
import org.triple.ClubMiles.domain.AttachedPhoto;
import org.triple.ClubMiles.domain.PointHistory;
import org.triple.ClubMiles.dto.PhotoResDto;
import org.triple.ClubMiles.dto.PointHistoryResDto;
import org.triple.ClubMiles.exception.AttachedPhotoNotExistException;
import org.triple.ClubMiles.exception.FileExtensionValidException;
import org.triple.ClubMiles.repository.*;
import org.triple.ClubMiles.util.IdGeneration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class SupportService {

    @Value("${triple.review.photo.upload}")
    private String FILE_ROOT_PATH;

    @Autowired
    private AttachedPhotoRepository attachedPhotoRepository;
    @Autowired
    private IdGeneration idGeneration;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    /**
     * 사진을 업로드할 때마다 아이디를 생성 - 미승인
     * 해당 게시글을 submit 할 때 해당 사진등을 승인 처리
     * */
    public String photoUpload(MultipartFile file) throws IOException {

        String photoId = idGeneration.idGenerator();
        String upload = file.getOriginalFilename();
        int index = upload.lastIndexOf(".");
        String ext = index == -1 ? "" : upload.substring(index + 1);

        //이미지 파일 형식만 업로드 가능
        if(!(ext.equals("jpg") || ext.equals("png"))){
            throw new FileExtensionValidException(ErrorCode.FILE_NOT_VALID, "jpg, png 형식의 이미지만 업로드 가능합니다.");
        }

        String stored = UUID.randomUUID() + (index == -1 ? "" : "." + ext);
        log.info("업로드 이름=[{}], 저장한 이름=[{}], 경로=[{}], 확장자=[{}], 크기={}",
                upload, stored, FILE_ROOT_PATH, ext, file.getSize());

        file.transferTo(new File(getFullPathFilename(stored)));

        AttachedPhoto attachedPhoto = AttachedPhoto.builder()
                .id(photoId)
                .review(null)
                .fileExtension(ext)
                .filePath(FILE_ROOT_PATH)
                .fileName(upload)
                .storedFile(stored)
                .isDelete(false)
                .isApproval(false)
                .build();
        attachedPhotoRepository.save(attachedPhoto);

        return photoId;
    }

    private String getFullPathFilename(String filename) {
        File path = new File(FILE_ROOT_PATH);
        if (!path.exists()) {
            boolean done = path.mkdirs();
            log.info("{} 를 만들려고 시도한 결과=[{}]", path.getAbsoluteFile(), done);
        }

        String ret = FILE_ROOT_PATH + "/" + filename;
        log.info("{} 를 생성합니다.", ret);
        return ret;
    }

    /**
     * 업로드한 사진을 삭제할 경우
     * */
    public String photoDelete(String photoId) {

        Optional<AttachedPhoto> existAttachedPhoto = attachedPhotoRepository.findById(photoId);
        AttachedPhoto attachedPhoto = existAttachedPhoto.orElseThrow(() -> {
            log.error("photoId=[{}], 첨부사진이 존재하지 않습니다.", photoId);
            throw new AttachedPhotoNotExistException(ErrorCode.PHOTO_NOT_EXIST, "첨부사진이 존재하지 않습니다.");
        });

        attachedPhoto.updateDelete();
        attachedPhotoRepository.save(attachedPhoto);
        log.info("=== 첨부사진 삭제");

        File file = new File(attachedPhoto.getFilePath() + "/" + attachedPhoto.getStoredFile());
        if (file.exists() && file.delete()) {
            log.info("{} 을 삭제했습니다.", attachedPhoto.getFileName());
        }

        return photoId;
    }

    public List<PhotoResDto> photoList(String reviewId) {

        List<PhotoResDto> result = new ArrayList<>();
        PhotoResDto resultItem = new PhotoResDto();

        List<AttachedPhoto> attachedPhotoList = new ArrayList<>();
        if(reviewId == null){
            attachedPhotoList = attachedPhotoRepository.findAll();
        }
        else {
            attachedPhotoList = attachedPhotoRepository.findByReview_id(reviewId);
        }

        for(var photo : attachedPhotoList){
            resultItem = PhotoResDto.builder()
                    .attachedPhotoId(photo.getId())
                    .isDelete(photo.isDelete())
                    .isApproval(photo.isApproval())
                    .reviewId(reviewId)
                    .build();
            result.add(resultItem);
        }

        return result;
    }

    public List<PointHistoryResDto> pointHistory() {

        List<PointHistoryResDto> result = new ArrayList<>();
        PointHistoryResDto resultItem = new PointHistoryResDto();

        List<PointHistory> pointHistoryList = pointHistoryRepository.findAll();

        for(var pointHistory: pointHistoryList){
            resultItem = PointHistoryResDto.builder()
                    .userId(pointHistory.getUser().getId())
                    .userName(pointHistory.getUser().getUserName())
                    .placeId(pointHistory.getPlace().getId())
                    .placeName(pointHistory.getPlace().getPlaceName())
                    .reviewId(pointHistory.getReview().getId())
                    .action(pointHistory.getAction())
                    .modType(pointHistory.getModType())
                    .point(pointHistory.getPoint())
                    .isBonusPoint(pointHistory.isBonusPoint())
                    .build();
            result.add(resultItem);
        }
        return result;
    }
}
