package org.triple.ClubMiles.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.triple.ClubMiles.domain.common.Action;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventReqDto {

    @ApiModelProperty(required = true, value = "이벤트 타입")
    @JsonProperty("type")
    private String type;

    @JsonIgnore
    private Action action;

    @NotNull
    @Size(max = 36, min = 36)
    @ApiModelProperty(required = false, value = "리뷰 ID")
    @JsonProperty("reviewId")
    private String reviewId;

    @ApiModelProperty(required = true, value = "계약회원 고유번호")
    @JsonProperty("content")
    private String content;

    @ApiModelProperty(required = true, value = "첨부 사진 ID 배열")
    @JsonProperty("attachedPhotoIds")
    private List<String> attachedPhotoIds;

    @Size(max = 36, min = 36)
    @ApiModelProperty(required = true, value = "사용자 ID")
    @JsonProperty("userId")
    private String userId;

    @Size(max = 36, min = 36)
    @ApiModelProperty(required = true, value = "장소 ID")
    @JsonProperty("placeId")
    private String placeId;

    public void updateReviewId(EventReqDto eventReqDto, String reviewId) {
        eventReqDto.reviewId = reviewId;
    }

    public void updateAction(EventReqDto eventReqDto, Action action) {
        eventReqDto.action = action;
    }

}
