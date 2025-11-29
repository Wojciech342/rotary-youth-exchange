package org.rotary.exchange.backend.repository;

import org.rotary.exchange.backend.model.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CoordinatorRepository extends JpaRepository<Coordinator, Integer> {
    Optional<Coordinator> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByProfilePictureUrl(String profilePictureUrl);
    List<Coordinator> findByDistrictId(Integer districtId);
}
