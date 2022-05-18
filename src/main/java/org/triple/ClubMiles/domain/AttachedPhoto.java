package org.triple.ClubMiles.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.triple.ClubMiles.domain.common.TimeEntity;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Entity(name = "attached_photo")
public class AttachedPhoto extends TimeEntity {

    @Id
    @Size(max = 36, min = 36)
    private String id;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Column(name = "file_extension")
    private String fileExtension;

    @Column(name = "file_path")
    private String filePath;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "stored_file")
    private String storedFile;

    @Column(name = "is_delete")
    private boolean isDelete;

    @Column(name = "is_approval")
    private boolean isApproval;

    public void updateApproval() {
        this.isApproval = true;
    }

    public void updateDelete() {
        this.isDelete = true;
        this.isApproval = false;
    }

    public void updateReviewId(Review review) {
        this.review = review;
    }
}
