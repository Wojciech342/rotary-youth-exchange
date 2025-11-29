package org.rotary.exchange.backend.repository;

import org.rotary.exchange.backend.model.CampDistrictStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CampDistrictStatusRepository extends JpaRepository<CampDistrictStatus, Integer> {
    // Find specific status logic
    Optional<CampDistrictStatus> findByCampInstanceIdAndDistrictId(Integer campInstanceId, Integer districtId);

    // Find all statuses for a specific camp (for global cascading)
    List<CampDistrictStatus> findByCampInstanceId(Integer campInstanceId);
}