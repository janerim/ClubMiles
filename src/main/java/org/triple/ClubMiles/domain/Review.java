package org.triple.ClubMiles.domain;

import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.triple.ClubMiles.domain.common.TimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Getter
@Builder
@Entity(name = "review")
public class Review extends TimeEntity {

    @Id
    @Size(max = 36, min = 36)
    private String id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @NotNull
    @Column(name = "content")
    private String content;

    @Column(name = "is_delete")
    private boolean isDelete;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy="review", cascade = CascadeType.ALL)
    private List<AttachedPhoto> attachedPhotoList = new ArrayList<>();
    public void updateDelete() {
        this.isDelete = true;
    }

    public void updateContent(String newContent) {
        this.content = newContent;
    }
}
