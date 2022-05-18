package org.triple.ClubMiles.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.triple.ClubMiles.domain.User;

public interface UserRepository extends JpaRepository<User, String> {
}
