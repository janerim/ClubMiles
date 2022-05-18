package org.triple.ClubMiles.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointResDto {

    private String userId;
    private String userName;
    private List<PlacePoint> placePointList;
    private int currentPoint;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class PlacePoint {

        private String placeId;
        private String placeName;
        private int point;
    }
}
