package org.rotary.exchange.backend.repository;

import org.rotary.exchange.backend.model.CampInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CampInstanceRepository extends JpaRepository<CampInstance, Integer>, JpaSpecificationExecutor<CampInstance> {

}