package org.triple.ClubMiles.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.triple.ClubMiles.domain.AttachedPhoto;
import org.triple.ClubMiles.domain.Place;
import org.triple.ClubMiles.domain.Review;

import java.util.List;

import static org.triple.ClubMiles.domain.QReview.review;

@Slf4j
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom{

    private final JPAQueryFactory factory;

    @Override
    public boolean findExistByPlaceAndIsDelete(Place place, boolean isDelete) {

        List<Review> exist = factory
                .select(review)
                .from(review)
                .where(review.place.eq(place).and(review.isDelete.eq(isDelete)))
                .limit(2)
                .fetch();

        return exist.size() > 1;
    }
}
