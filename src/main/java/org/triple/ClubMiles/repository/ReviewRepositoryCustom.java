package org.triple.ClubMiles.repository;

import org.triple.ClubMiles.domain.Place;
import org.triple.ClubMiles.domain.Review;

public interface ReviewRepositoryCustom {

    boolean findExistByPlaceAndIsDelete(Place place, boolean b);
}
