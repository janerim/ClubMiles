package org.triple.ClubMiles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.triple.ClubMiles.domain.Place;

public interface PlaceRepository extends JpaRepository<Place, String> {
}
