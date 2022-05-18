package org.triple.ClubMiles.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.triple.ClubMiles.controller.common.ErrorCode;
import org.triple.ClubMiles.domain.*;
import org.triple.ClubMiles.domain.common.ModType;
import org.triple.ClubMiles.dto.EventReqDto;
import org.triple.ClubMiles.dto.PointResDto;
import org.triple.ClubMiles.exception.PlaceNotExistException;
import org.triple.ClubMiles.exception.ReviewAlreadyExistException;
import org.triple.ClubMiles.exception.ReviewNotExistException;
import org.triple.ClubMiles.exception.UserNotExistException;
import org.triple.ClubMiles.repository.*;
import org.triple.ClubMiles.util.IdGeneration;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class EventService {

    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private AttachedPhotoRepository attachedPhotoRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private PointHistoryRepository pointHistoryRepository;
    @Autowired
    private IdGeneration idGeneration;

    /**
     *생성일 경우
     * 1. 첨부사진 승인지하철
     * 2. review 테이블 update
     * 3. point_history 테이블 insert: 첫 리뷰인지 확인
     * 4. user 테이블 point update
     * */
    @Transactional
    public String addEvent(EventReqDto eventReqDto) {

        //review 테이블 update
        Optional<Review> existReview = reviewRepository.findByIdAndIsDelete(eventReqDto.getReviewId(), false);
        if(existReview.isPresent()) {
            log.error("reviewId=[{}], 리뷰가 이미 존재합니다,", eventReqDto.getReviewId());
            throw new ReviewAlreadyExistException(ErrorCode.REVIEW_ALREADY_EXIST, "리뷰가 이미 존재합니다.");
        }

        Optional<User> writeUser = userRepository.findById(eventReqDto.getUserId());
        User user = writeUser.orElseThrow(() -> {
            log.error("userId=[{}], 사용자가 존재하지 않습니다.", eventReqDto.getUserId());
            throw new UserNotExistException(ErrorCode.USER_NOT_EXIST, "사용자가 존재하지 않습니다.");
        });

        Optional<Place> writePlace = placeRepository.findById(eventReqDto.getPlaceId());
        Place place = writePlace.orElseThrow(() -> {
            log.error("placeId=[{}], 장소가 존재하지 않습니다.", eventReqDto.getPlaceId());
            throw new PlaceNotExistException(ErrorCode.PLACE_NOT_EXIST, "장소가 존재하지 않습니다.");
        });

        Review review = Review.builder()
                .id(eventReqDto.getReviewId())
                .user(user)
                .place(place)
                .content(eventReqDto.getContent())
                .isDelete(false)
                .build();
        reviewRepository.save(review);
        log.info(" === 리뷰 추가");

        //첨부사진 승인
        List<AttachedPhoto> attachedPhotoList = new ArrayList<>();
        if(!eventReqDto.getAttachedPhotoIds().isEmpty()){
            attachedPhotoList = attachedPhotoRepository.findByIds(eventReqDto.getAttachedPhotoIds());
            for(var photo : attachedPhotoList){
                photo.updateApproval();
                photo.updateReviewId(review);
                attachedPhotoRepository.save(photo);
                log.info("[{}]번 생성된 첨부사진을 승인하였습니다.", photo.getId());
            }
        }

        //point_history 테이블 insert
        //생성시 가능한 점수
        // 1점: 글
        // 2점: 글+첫리뷰, 글+사진
        // 3점: 글+사진+첫리뷰
        boolean isBonusPoint = checkIsBonusPoint(place);
        int point = calcPoint(isBonusPoint, attachedPhotoList);

        String pointHistoryId = idGeneration.idGenerator();
        PointHistory pointHistory = PointHistory.builder()
                .id(pointHistoryId)
                .review(review)
                .user(user)
                .place(place)
                .action(eventReqDto.getAction())
                .modType(null)
                .point(point)
                .isBonusPoint(isBonusPoint)
                .build();
        pointHistoryRepository.save(pointHistory);
        log.info(" === 포인트 히스토리 추가");

        //user 테이블 point update
        calUserCurrentPoint(user, point);

        return review.getId();
    }

    /**
     *수정일 경우
     * 1. 사진 삭제, 추가 여부 확인 -> attachedPhoto 테이블 update (필요시)
     * 2. review 테이블 update
     * 3. point_history 테이블 insert
     * 4. user 테이블 point update
     * */
    @Transactional
    public String modEvent(EventReqDto eventReqDto) {

        //review 테이블 update
        Optional<Review> existReview = reviewRepository.findById(eventReqDto.getReviewId());
        Review review = existReview.orElseThrow(() -> {
            log.error("reviewId=[{}], 리뷰가 존재하지 않습니다.", eventReqDto.getReviewId());
            throw new ReviewNotExistException(ErrorCode.REVIEW_NOT_EXIST, "리뷰가 존재하지 않습니다.");
        });

        boolean isDeleteAllPhoto = false;
        boolean isUpdateNewPhoto = false;

        //사진 삭제, 추가 여부 확인
        //해당 리뷰에 사진 존재 여부 확인
        List<AttachedPhoto> beforeAttachedPhotoList =
                attachedPhotoRepository.findListByReview_idAndIsApprovalAndIsDelete(eventReqDto.getReviewId());
        List<AttachedPhoto> afterAttachedPhotoList =
                attachedPhotoRepository.findByIds(eventReqDto.getAttachedPhotoIds());

        //기존 사진 미존재 -> 추가 여부 확인
        if(beforeAttachedPhotoList.isEmpty()){
            //추가했을 경우 첨부사진 승인 -> 포인트 지급
            if(!afterAttachedPhotoList.isEmpty()){
                for(var photo : afterAttachedPhotoList){
                    photo.updateApproval();
                    log.info("[{}]번 추가된 첨부사진을 승인하였습니다.", photo.getId());
                }
                isUpdateNewPhoto = true;
                log.info("첨부사진을 새롭게 추가 요정합니다.");
            }
            //추가하지 않았을 경우 pass
        }
        //기존 사진 존재 -> 삭제 or 추가 여부 확인
        else {
            //전체 삭제 -> 포인트 회수
            if(afterAttachedPhotoList.isEmpty()) {
                isDeleteAllPhoto = true;
                log.info("첨부사진을 모두 삭제 요정합니다.");
            }
            //일부 추가 or 삭제했을 경우 -> 승인 여부 update 필요
            else {
                //삭제된 사진 있는지 확인
                //is_delete = true, is_approval = false
                List<AttachedPhoto> existDeletePhotoList =
                        beforeAttachedPhotoList.stream()
                        .filter(e -> !e.isApproval())
                        .filter(e -> e.isDelete())
                        .collect(Collectors.toList());

                for(var existDeletePhoto : existDeletePhotoList){
                    existDeletePhoto.updateApproval();
                    log.info("[{}]번 삭제된 첨부사진을 승인하였습니다.", existDeletePhoto.getId());
                }

                //추가된 사진 있는지 확인
                //is_delete = true, is_approval = false
                List<AttachedPhoto> existAddPhotoList =
                        beforeAttachedPhotoList.stream()
                                .filter(e -> !e.isApproval())
                                .filter(e -> !e.isDelete())
                                .collect(Collectors.toList());

                for(var existAddPhoto : existAddPhotoList){
                    existAddPhoto.updateApproval();
                    log.info("[{}]번 추가된 첨부사진을 승인하였습니다.", existAddPhoto.getId());
                }
            }
        }

        review.updateContent(eventReqDto.getContent());
        log.info(" === [{}]번 리뷰 업데이트", review.getId());

        //point_history 테이블 insert
        //수정시 가능한 점수
        // 1점: 사진 추가
        // -1점: 사진 전체 삭제
        Optional<User> writeUser = userRepository.findById(eventReqDto.getUserId());
        User user = writeUser.orElseThrow(() -> {
            log.error("userId=[{}], 사용자가 존재하지 않습니다.", eventReqDto.getUserId());
            throw new UserNotExistException(ErrorCode.USER_NOT_EXIST, "사용자가 존재하지 않습니다.");
        });

        Optional<Place> writePlace = placeRepository.findById(eventReqDto.getPlaceId());
        Place place = writePlace.orElseThrow(() -> {
            log.error("placeId=[{}], 장소가 존재하지 않습니다.", eventReqDto.getPlaceId());
            throw new PlaceNotExistException(ErrorCode.PLACE_NOT_EXIST, "장소가 존재하지 않습니다.");
        });

        String pointHistoryId = idGeneration.idGenerator();
        PointHistory pointHistory = PointHistory.builder()
                .id(pointHistoryId)
                .review(review)
                .user(user)
                .place(place)
                .action(eventReqDto.getAction())
                .isBonusPoint(false)
                .build();

        //사진 전체 삭제
        if(isDeleteAllPhoto){
            pointHistory.updatePoint(ModType.DELETE_PHOTO);
            pointHistoryRepository.save(pointHistory);
            log.info(" === 포인트 히스토리 추가");
        }
        //사진 추가
        else if (isUpdateNewPhoto){
            pointHistory.updatePoint(ModType.ADD_PHOTO);
            pointHistoryRepository.save(pointHistory);
            log.info(" === 포인트 히스토리 추가");
        }

        //user 테이블 point update
        calUserCurrentPoint(user, pointHistory.getPoint());

        return review.getId();
    }

    /**
     *삭제일 경우
     * 1. review 테이블 update
     * 2. attachedPhoto 테이블 update (필요시)
     * 3. point_history 테이블 insert
     * 4. user 테이블 point update
     * */
    @Transactional
    public String deleteEvent(EventReqDto eventReqDto) {

        //review 테이블 update
        Optional<Review> existReview = reviewRepository.findById(eventReqDto.getReviewId());
        Review review = existReview.orElseThrow(() -> {
            log.error("reviewId=[{}], 리뷰가 존재하지 않습니다.", eventReqDto.getReviewId());
            throw new ReviewNotExistException(ErrorCode.REVIEW_NOT_EXIST, "리뷰가 존재하지 않습니다.");
        });

        review.updateDelete();
        log.info(" === [{}]번 리뷰 업데이트 - isDelete = true", review.getId());

        //attachedPhoto 테이블 update (필요시)
        List<AttachedPhoto> attachedPhotoList =
                attachedPhotoRepository.findByReview_idAndIsApprovalAndIsDelete(eventReqDto.getReviewId(), true, false);

        for(var photo : attachedPhotoList){
            photo.updateDelete();
        }
        log.info(" === 첨부사진 업데이트 - isDelete = true");

        //point_history 테이블 insert
        Optional<User> writeUser = userRepository.findById(eventReqDto.getUserId());
        User user = writeUser.orElseThrow(() -> {
            log.error("userId=[{}], 사용자가 존재하지 않습니다.", eventReqDto.getUserId());
            throw new UserNotExistException(ErrorCode.USER_NOT_EXIST, "사용자가 존재하지 않습니다.");
        });
        Optional<Place> writePlace = placeRepository.findById(eventReqDto.getPlaceId());
        Place place = writePlace.orElseThrow(() -> {
            log.error("placeId=[{}], 장소가 존재하지 않습니다.", eventReqDto.getPlaceId());
            throw new PlaceNotExistException(ErrorCode.PLACE_NOT_EXIST, "장소가 존재하지 않습니다.");
        });

        List<PointHistory> pointHistoryList =
                pointHistoryRepository.findByUserAndPlace(user,place);
        int removePoint = pointHistoryList.stream().mapToInt(PointHistory::getPoint).sum();
        String pointHistoryId = idGeneration.idGenerator();

        PointHistory pointHistory = PointHistory.builder()
                .id(pointHistoryId)
                .review(review)
                .user(user)
                .place(place)
                .action(eventReqDto.getAction())
                .modType(null)
                .point(-removePoint)
                .isBonusPoint(false)
                .build();
        pointHistoryRepository.save(pointHistory);
        log.info(" === 포인트 히스토리 추가");

        //user 테이블 point update
        calUserCurrentPoint(user, -removePoint);

        return review.getId();
    }

    /**
     * user 테이블의 currentPoint를 업데이트
     * */
    private void calUserCurrentPoint(User user, int point) {

        //user 테이블 point update
        int beforePoint = user.getCurrentPoint();
        user.updateCurrentPoint(point);
        int afterPoint = user.getCurrentPoint();
        log.info(" === [{}]번 사용자 포인트 업데이트 - 이전:[{}], 이후:[{}]",
                user.getId(), beforePoint, afterPoint);
    }


    /**
     * 첫 리뷰인지 확인하고 return
     *
     * review 테이블에서 다음 조건으로 검색
     *  1. isDelete = false
     *  2. placeId 
     * */
    private boolean checkIsBonusPoint(Place place) {

        //review 테이블에 place 관련 리뷰가 하나라도 존재할 경우 -> true 반환
        boolean review = reviewRepository.findExistByPlaceAndIsDelete(place,false);
        return !review;
    }

    /**
     * 등록 포인트 계산 후 return
     *
     * 포인트 계산에 필요한 조건
     *  1. 첫 리뷰 여부
     *  2. 사진 첨부 여부
     * */
    private int calcPoint(boolean isBonusPoint, List<AttachedPhoto> attachedPhotoIds) {

        int point = 1;

        if (!attachedPhotoIds.isEmpty()) {
            point += 1;
        }

        if(isBonusPoint){
            point += 1;
        }
        return point;
    }

    /**
     * point 조회
     *
     * userId == null 일 경우, 전체 사용자 조회
     * placeId == null 일 경우, 전체 장소 조회
     * */
    public List<PointResDto> point(String userId) {

        List<PointResDto> result = new ArrayList<>();
        PointResDto resultItem = new PointResDto();
        List<PointResDto.PlacePoint> placePointList = new ArrayList<>();

        //특정 사용자에 대한 포인트 조회일 경우
        if(userId != null){

            log.info("[{}]번 사용자 조회 요청", userId);
            Optional<User> writeUser = userRepository.findById(userId);
            User user = writeUser.orElseThrow(() -> {
                log.error("userId=[{}], 사용자가 존재하지 않습니다.", userId);
                throw new UserNotExistException(ErrorCode.USER_NOT_EXIST, "사용자가 존재하지 않습니다.");
            });

            resultItem = PointResDto.builder()
                    .userId(user.getId())
                    .userName(user.getUserName())
                    .currentPoint(user.getCurrentPoint())
                    .build();
            result.add(resultItem);

            log.info("[{}]번 사용자 장소별 조회 요청", userId);
            List<PointHistory> pointHistoryList =
                    pointHistoryRepository.findByUser_Id(userId);

            Set<Map.Entry<Place, List<PointHistory>>> groupByPlaceMap =
                    pointHistoryList.stream().collect(Collectors.groupingBy(PointHistory::getPlace)).entrySet();

            for(var placeMap : groupByPlaceMap){
                Place place = placeMap.getKey();
                List<PointHistory> pointHistoryListByPlace = placeMap.getValue();
                int placePointSum = pointHistoryListByPlace.stream().mapToInt(PointHistory::getPoint).sum();

                PointResDto.PlacePoint placePoint = PointResDto.PlacePoint.builder()
                        .placeId(place.getId())
                        .placeName(place.getPlaceName())
                        .point(placePointSum)
                        .build();
                placePointList.add(placePoint);
                log.info("사용자=[{}], 장소=[{}], 포인트=[{}]", userId, place.getId(), placePointSum);
            }
            resultItem.setPlacePointList(placePointList);
            result.add(resultItem);
        }
        //전체 사용자에 대한 포인트 조회일 경우
        else {
            log.info("전체 사용자 조회 요청");
            List<User> userList = userRepository.findAll();

            log.info("전체 사용자 장소별 조회 요청");
            List<PointHistory> pointHistoryList = pointHistoryRepository.findAll();

            for (var user : userList) {

                List<PointHistory> groupByUserMap
                        = pointHistoryList.stream().filter(e -> e.getUser().equals(user)).collect(Collectors.toList());

                Set<Map.Entry<Place, List<PointHistory>>> groupByPlaceMap =
                        groupByUserMap.stream().collect(Collectors.groupingBy(PointHistory::getPlace)).entrySet();

                for(var placeMap : groupByPlaceMap){
                    Place place = placeMap.getKey();
                    List<PointHistory> pointHistoryListByPlace = placeMap.getValue();
                    int placePointSum = pointHistoryListByPlace.stream().mapToInt(PointHistory::getPoint).sum();

                    PointResDto.PlacePoint placePoint = PointResDto.PlacePoint.builder()
                            .placeId(place.getId())
                            .placeName(place.getPlaceName())
                            .point(placePointSum)
                            .build();
                    placePointList.add(placePoint);
                    log.info("사용자=[{}], 장소=[{}], 포인트=[{}]", user.getId(), place.getId(), placePointSum);
                }
                resultItem = PointResDto.builder()
                        .userId(user.getId())
                        .userName(user.getUserName())
                        .placePointList(placePointList)
                        .currentPoint(user.getCurrentPoint())
                        .build();
                result.add(resultItem);
                placePointList = new ArrayList<>();;
            }
        }
        return result;
    }
}
