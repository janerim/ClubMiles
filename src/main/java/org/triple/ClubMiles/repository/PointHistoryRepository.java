package org.triple.ClubMiles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.triple.ClubMiles.domain.Place;
import org.triple.ClubMiles.domain.PointHistory;
import org.triple.ClubMiles.domain.User;

import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, String> {
    List<PointHistory> findByUserAndPlace(User user, Place place);
    List<PointHistory> findByUser_Id(String userId);
}
