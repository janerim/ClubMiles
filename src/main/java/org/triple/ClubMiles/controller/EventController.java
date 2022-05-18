package org.triple.ClubMiles.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.triple.ClubMiles.controller.common.ErrorCode;
import org.triple.ClubMiles.controller.common.Response;
import org.triple.ClubMiles.domain.common.Action;
import org.triple.ClubMiles.dto.*;
import org.triple.ClubMiles.exception.ReviewNotExistException;
import org.triple.ClubMiles.service.EventService;
import org.triple.ClubMiles.util.IdGeneration;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Api(produces = "이벤트 Controller")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping("/events")
    @ApiOperation(value = "리뷰 작성 이벤트", notes = "")
    public Response<String> events(@RequestParam Action action,
                                   @RequestBody @Valid EventReqDto eventReqDto){

        eventReqDto.updateAction(eventReqDto, action);
        if(eventReqDto.getAction().equals(Action.ADD)) {
            return Response.OK("", eventService.addEvent(eventReqDto));
        }
        //수정 이벤트
        else if (eventReqDto.getAction().equals(Action.MOD)){
            return Response.OK("", eventService.modEvent(eventReqDto));
        }
        //삭제 이벤트
        else if (eventReqDto.getAction().equals(Action.DELETE)){
            return Response.OK("", eventService.deleteEvent(eventReqDto));
        }

        return Response.ERROR_WITH(ErrorCode.NOT_VALID_ACTION, "액션 타입이 올바르지 않습니다.");

    }


    @GetMapping("/point")
    @ApiOperation(value = "포인트 조회", notes = "" +
            "\n - userId가 null일 경우 전체 사용자에 대해서 조회")
    public Response<List<PointResDto>> point(String userId){
        return Response.OK("", eventService.point(userId));
    }
}
