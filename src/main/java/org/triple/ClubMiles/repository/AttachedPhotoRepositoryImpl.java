package org.triple.ClubMiles.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.triple.ClubMiles.domain.AttachedPhoto;

import java.util.List;

import static org.triple.ClubMiles.domain.QAttachedPhoto.attachedPhoto;

;

@Slf4j
@RequiredArgsConstructor
public class AttachedPhotoRepositoryImpl implements AttachedPhotoRepositoryCustom {

    private final JPAQueryFactory factory;

    @Override
    public List<AttachedPhoto> findByIds(List<String> attachedPhotoIds) {

        List<AttachedPhoto> attachedPhotoList = factory
                .select(attachedPhoto)
                .from(attachedPhoto)
                .where(attachedPhoto.id.in(attachedPhotoIds))
                .fetch();

        return attachedPhotoList;
    }

    @Override
    public List<AttachedPhoto> findListByReview_idAndIsApprovalAndIsDelete(String reviewId) {

        /**
         * 리스트 2가지
         * 1. is_delete = false, is_approval = true
         * 2. is_delete = true, is_approval = false
         * 2. is_delete = false, is_approval = false
         * */

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(attachedPhoto.isDelete.eq(false).and(attachedPhoto.isApproval.eq(true)));
        builder.or(attachedPhoto.isDelete.eq(true).and(attachedPhoto.isApproval.eq(false)));
        builder.or(attachedPhoto.isDelete.eq(false).and(attachedPhoto.isApproval.eq(false)));

        List<AttachedPhoto> attachedPhotoList = factory
                .select(attachedPhoto)
                .from(attachedPhoto)
                .where(builder)
                .fetch();

        return attachedPhotoList;
    }
}
