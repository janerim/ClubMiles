package org.triple.ClubMiles.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.triple.ClubMiles.domain.common.Action;
import org.triple.ClubMiles.domain.common.ModType;
import org.triple.ClubMiles.domain.common.TimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity(name = "point_history")
public class PointHistory extends TimeEntity {

    @Id
    @Size(max = 36, min = 36)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @NotNull
    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    private Action action;

    @Column(name = "mod_type")
    @Enumerated(EnumType.STRING)
    private ModType modType;

    @NotNull
    @Column(name = "point")
    private int point;

    @Column(name = "is_bonus_point")
    private boolean isBonusPoint;

    public void updatePoint(ModType modeType) {
        this.modType =  modeType;
        if(modeType.equals(ModType.ADD_PHOTO)){
            this.point = 1;
        }
        else if(modeType.equals(ModType.DELETE_PHOTO)){
            this.point = -1;
        }
    }
}
