package org.triple.ClubMiles.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PhotoResDto {

    @ApiModelProperty(value = "첨부사진 ID")
    private String attachedPhotoId;

    @ApiModelProperty(value = "삭제 여부")
    private boolean isDelete;

    @ApiModelProperty(value = "승인 여부")
    private boolean isApproval;

    @ApiModelProperty(value = "review ID")
    private String reviewId;
}