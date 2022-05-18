package org.triple.ClubMiles.dto;

import lombok.*;
import org.triple.ClubMiles.domain.common.Action;
import org.triple.ClubMiles.domain.common.ModType;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointHistoryResDto {

    private String userId;
    private String userName;

    private String placeId;
    private String placeName;

    private String reviewId;

    private Action action;
    private ModType modType;
    private int point;
    private boolean isBonusPoint;


}
