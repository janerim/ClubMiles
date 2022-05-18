package org.triple.ClubMiles.repository;

import org.triple.ClubMiles.domain.AttachedPhoto;

import java.util.List;

public interface AttachedPhotoRepositoryCustom {
    List<AttachedPhoto> findByIds(List<String> attachedPhotoIds);

    List<AttachedPhoto> findListByReview_idAndIsApprovalAndIsDelete(String reviewId);
}
