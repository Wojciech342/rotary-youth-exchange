package org.rotary.exchange.backend.repository;

import io.swagger.v3.oas.annotations.media.Schema;
import org.rotary.exchange.backend.model.CampTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CampTemplateRepository extends JpaRepository<CampTemplate, Integer> {
    
    boolean existsByImageUrl(String imageUrl);
    
    boolean existsByFlyerPdfUrl(String flyerPdfUrl);

    @Schema(description = "Find all templates owned by a specific coordinator.")
    List<CampTemplate> findByOwnerId(Integer ownerId);
}
