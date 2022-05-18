package org.triple.ClubMiles.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.triple.ClubMiles.domain.common.TimeEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity(name = "place")
public class Place extends TimeEntity {

    @Id
    @Size(max = 36, min = 36)
    private String id;

    @NotNull
    @Column(name = "place_name")
    private String placeName;

}
