package org.triple.ClubMiles.controller;

import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.triple.ClubMiles.controller.common.Response;
import org.triple.ClubMiles.domain.common.Action;
import org.triple.ClubMiles.dto.EventReqDto;
import org.triple.ClubMiles.dto.PointResDto;
import org.triple.ClubMiles.service.EventService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
@AutoConfigureMockMvc //MockMvc를 Builder 없이 주입받을 수 있음
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    EventService eventService;

    @Test
    @DisplayName("이벤트 controller 테스트 - 정상동작")
    void events() throws Exception {
        
        //given : Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        Action action = Action.ADD;
        List<String> attachedPhotoIdList = new ArrayList<>();
        attachedPhotoIdList.add("11111111-1234-1234-1234-123456789012");
        attachedPhotoIdList.add("22222222-1234-1234-1234-123456789012");
        EventReqDto eventReqDto = EventReqDto.builder()
                .type("REVIEW")
                .userId("12345678-1234-1234-1234-123456789012")
                .placeId("12345678-1111-1234-1234-123456789012")
                .reviewId("12345678-2222-1234-1234-123456789012")
                .attachedPhotoIds(attachedPhotoIdList)
                .content("좋아요")
                .build();

        Gson gson = new Gson();
        String content = gson.toJson(eventReqDto);

        //andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/events")
                .param("action", action.name())
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andDo(print());

    }

    @Test
    @DisplayName("이벤트 controller 테스트 - 서버에러")
    void events_error_reviewId_null() throws Exception {
        
        //given : Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        Action action = Action.MOD;
        List<String> attachedPhotoIdList = new ArrayList<>();
        attachedPhotoIdList.add("11111111-1234-1234-1234-123456789012");
        attachedPhotoIdList.add("22222222-1234-1234-1234-123456789012");
        EventReqDto eventReqDto = EventReqDto.builder()
                .type("REVIEW")
                .userId("12345678-1234-1234-1234-123456789012")
                .placeId("12345678-1111-1234-1234-123456789012")
//                .reviewId("12345678-2222-1234-1234-123456789012")
                .attachedPhotoIds(attachedPhotoIdList)
                .content("좋아요")
                .build();

        Gson gson = new Gson();
        String content = gson.toJson(eventReqDto);

        //andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/events")
                .param("action", action.name())
                .content(content)
                .contentType(MediaType.APPLICATION_JSON);
        mockMvc.perform(builder)
                .andExpect(status().is5xxServerError())
                .andDo(print());
    }

    @Test
    @DisplayName("포인트 리스트 가져오기 테스트")
    void point() throws Exception {

        //given : Mock 객체가 특정 상황에서 해야하는 행위를 정의하는 메소드
        List<PointResDto> pointResDtoList = new ArrayList<>();

        List<PointResDto.PlacePoint> placePointList = new ArrayList<>();
        PointResDto.PlacePoint placePoint = PointResDto.PlacePoint.builder()
                .placeId("12345678-1111-1234-1234-123456789012")
                .placeName("테스트장소01")
                .point(3)
                .build();
        placePointList.add(placePoint);
        placePoint = PointResDto.PlacePoint.builder()
                .placeId("12345678-2222-1234-1234-123456789012")
                .placeName("테스트장소02")
                .point(2)
                .build();
        placePointList.add(placePoint);

        PointResDto pointResDto = PointResDto.builder()
                .userId("12345678-1234-1234-1234-123456789012")
                .userName("테스트유저01")
                .currentPoint(4)
                .placePointList(placePointList)
                .build();
        pointResDtoList.add(pointResDto);
        Response response = new Response();
        response.setData(pointResDtoList);

        given(eventService.point(null)).willReturn(
                pointResDtoList
        );

        //andExpect : 기대하는 값이 나왔는지 체크해볼 수 있는 메소드
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get("/api/point");
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("data[0].userId").exists())
                .andExpect(jsonPath("data[0].userName").exists())
                .andExpect(jsonPath("data[0].currentPoint").exists())
                .andDo(print());

    }
}