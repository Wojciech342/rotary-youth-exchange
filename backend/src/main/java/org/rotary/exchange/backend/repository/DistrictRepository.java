package org.rotary.exchange.backend.repository;

import org.rotary.exchange.backend.model.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DistrictRepository extends JpaRepository<District, Integer> {
    List<District> findByCountryId(Integer countryId);
    
    Optional<District> findByAccessCode(String accessCode);
    
    Optional<District> findByCode(String code);
}
