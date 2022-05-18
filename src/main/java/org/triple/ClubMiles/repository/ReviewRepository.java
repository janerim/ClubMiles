package org.triple.ClubMiles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.triple.ClubMiles.domain.Place;
import org.triple.ClubMiles.domain.Review;

import java.util.Optional;

public interface ReviewRepository extends
        JpaRepository<Review, String>, ReviewRepositoryCustom {
    Optional<Review> findByIdAndIsDelete(String reviewId, boolean b);
}
