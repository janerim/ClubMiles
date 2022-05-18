package org.triple.ClubMiles.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.triple.ClubMiles.domain.Place;
import org.triple.ClubMiles.domain.Review;
import org.triple.ClubMiles.domain.User;
import org.triple.ClubMiles.dto.EventReqDto;
import org.triple.ClubMiles.repository.PlaceRepository;
import org.triple.ClubMiles.repository.ReviewRepository;
import org.triple.ClubMiles.repository.UserRepository;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //테스트 클래스가 Mockito를 사용함을 의미
class EventServiceTest {

    //실제 구현된 객체 대신에 Mock 객체를 사용하게 될 클래스를 의미
    //테스트 런타임 시 해당 객체 대신 Mock 객체가 주입되어 Unit Test가 처리됨
    @Mock
    ReviewRepository reviewRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    PlaceRepository placeRepository;

    //Mock 객체가 주입된 클래스를 사용하게 될 클래스를 의미
    //테스트 런타임 시 클래스 내부에 선언된 멤버 변수들 중에서
    //@Mock으로 등록된 클래스의 변수에 실제 객체 대신 Mock 객체가 주입되어 Unit Test가 처리
    @InjectMocks
    EventService eventService;

    @BeforeAll
    static void init(){

    }

    @Test
    @DisplayName("이벤트 ADD 테스트")
    void addEvent() {

        //given
        User user = User.builder()
                .id("12345678-1111-1234-1234-123456789012")
                .userName("사용자01")
                .build();
        Place place = Place.builder()
                .id("12345678-1234-1234-1111-123456789012")
                .placeName("장소01")
                .build();
        Review review = Review.builder()
                .id("11111111-1234-1234-1234-123456789012")
                .user(user)
                .place(place)
                .content("좋아요")
                .isDelete(false)
                .build();

        //Mock 객체 주입
        when(userRepository.save(any())).thenReturn("12345678-1111-1234-1234-123456789012");
        when(placeRepository.save(any())).thenReturn("12345678-1234-1234-1111-123456789012");
        when(reviewRepository.save(any())).thenReturn("11111111-1234-1234-1234-123456789012");

        //when
        EventReqDto eventReqDto = new EventReqDto();
        String reviewId = eventService.addEvent(eventReqDto);

        //then
        verify(reviewRepository, times(1)).save(any());
        assertThat(reviewId, equalTo(review.getId()));
    }

    @Test
    @DisplayName("이벤트 MOD 테스트")
    void modEvent() {
    }

    @Test
    @DisplayName("이벤트 DELETE 테스트")
    void deleteEvent() {
    }

    @Test
    @DisplayName("포인트 리스트 가려오기 테스트")
    void point() {
    }
}