package org.triple.ClubMiles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.triple.ClubMiles.domain.AttachedPhoto;

import java.util.List;

public interface AttachedPhotoRepository extends
        JpaRepository<AttachedPhoto, String>, AttachedPhotoRepositoryCustom {
    List<AttachedPhoto> findByReview_id(String reviewId);

    List<AttachedPhoto> findByReview_idAndIsApprovalAndIsDelete(String reviewId, boolean isApproval, boolean isDelete);
}
