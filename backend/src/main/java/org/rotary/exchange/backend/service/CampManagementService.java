package org.rotary.exchange.backend.service;

import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.*;
import org.rotary.exchange.backend.exception.AccessDeniedException;
import org.rotary.exchange.backend.exception.ResourceNotFoundException;
import org.rotary.exchange.backend.model.*;
import org.rotary.exchange.backend.repository.*;
import org.rotary.exchange.backend.repository.spec.CampSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CampManagementService {

    private final CampInstanceRepository instanceRepo;
    private final CampTemplateRepository templateRepo;
    private final CoordinatorRepository coordinatorRepo;
    private final CampDistrictStatusRepository districtStatusRepo;
    private final DistrictStatusService districtStatusService;

    public Page<CampResponseDTO> getPublicCamps(CampSearchCriteria filters, Pageable pageable) {
        Specification<CampInstance> spec = CampSpecifications.withCriteria(filters, true);
        return instanceRepo.findAll(spec, pageable)
                .map(CampResponseDTO::new);
    }

    /**
     * Get camps for coordinator's own camps (excludes ARCHIVED).
     */
    public Page<CampResponseDTO> getCoordinatorCamps(CampSearchCriteria filters, Pageable pageable) {
        Specification<CampInstance> spec = CampSpecifications.withCriteria(filters, false)
                .and((root, query, cb) -> cb.notEqual(root.get("globalStatus"), CampStatus.ARCHIVED));
        return instanceRepo.findAll(spec, pageable)
                .map(CampResponseDTO::new);
    }

    /**
     * Get all camps available for a specific district with their local status.
     * Excludes ARCHIVED camps.
     */
    public Page<CampWithDistrictStatusDTO> getCampsForDistrict(Integer districtId, String districtCode, Pageable pageable) {
        // Get camps that have a status entry for this district (excluding ARCHIVED)
        Specification<CampInstance> spec = (root, query, cb) -> {
            // Exclude archived camps
            return cb.notEqual(root.get("globalStatus"), CampStatus.ARCHIVED);
        };
        
        return instanceRepo.findAll(spec, pageable)
                .map(camp -> {
                    // Find the local status for this district
                    CampDistrictStatus status = districtStatusRepo
                            .findByCampInstanceIdAndDistrictId(camp.getId(), districtId)
                            .orElse(null);
                    
                    CampStatus localStatus = status != null ? status.getLocalStatus() : camp.getGlobalStatus();
                    return new CampWithDistrictStatusDTO(camp, districtId, districtCode, localStatus);
                });
    }

    public CampResponseDTO getCampById(Integer id) {
        CampInstance camp = instanceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camp", "id", id));
        return new CampResponseDTO(camp);
    }

    /**
     * Get coordinator with their assigned district.
     */
    public Coordinator getCoordinatorWithDistrict(Integer coordinatorId) {
        return coordinatorRepo.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "id", coordinatorId));
    }

    /**
     * Get camp by ID with district access verification.
     * Ensures the camp is available for the given district before returning details.
     */
    public CampResponseDTO getCampById(Integer id, Integer districtId) {
        CampInstance camp = instanceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camp", "id", id));
        
        // Check if the camp is available for this district
        boolean hasAccess = camp.getDistrictStatuses().stream()
                .anyMatch(status -> 
                        status.getDistrict().getId().equals(districtId) &&
                        (status.getLocalStatus() == CampStatus.OPEN ||
                         status.getLocalStatus() == CampStatus.ONLY_MALE ||
                         status.getLocalStatus() == CampStatus.ONLY_FEMALE));
        
        if (!hasAccess) {
            throw new ResourceNotFoundException("Camp", "id", id);
        }
        
        return new CampResponseDTO(camp);
    }

    // --- WRITE OPERATIONS ---

    @Transactional
    public CampResponseDTO createCamp(CampCreationRequest request) {
        // Get the coordinator first (we need it for template ownership)
        Coordinator owner = coordinatorRepo.findById(request.getCoordinatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "id", request.getCoordinatorId()));

        CampTemplate template;

        if (request.getExistingTemplateId() != null) {
            // Reuse existing template - must be owned by this coordinator
            template = templateRepo.findById(request.getExistingTemplateId())
                    .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", request.getExistingTemplateId()));
            
            // Verify ownership: coordinator can only use their own templates
            if (template.getOwner() == null || !template.getOwner().getId().equals(request.getCoordinatorId())) {
                throw new AccessDeniedException("You don't have access to this template");
            }
        } else {
            // Create new template owned by this coordinator
            template = new CampTemplate();
            template.setOwner(owner);
            template.setName(request.getName());
            template.setDescription(request.getDescription());
            template.setAgeMin(request.getAgeMin());
            template.setAgeMax(request.getAgeMax());
            template.setImageUrl(request.getImageUrl());
            template = templateRepo.save(template);
        }

        CampInstance instance = new CampInstance();
        instance.setCampTemplate(template);
        instance.setCoordinator(owner);
        instance.setDateStart(request.getDateStart());
        instance.setDateEnd(request.getDateEnd());
        instance.setPrice(request.getPrice());
        instance.setEdition(request.getEdition());
        instance.setGlobalStatus(CampStatus.OPEN);
        instance.setLimitTotal(request.getLimitTotal());
        instance.setLimitMale(request.getLimitMale());
        instance.setLimitFemale(request.getLimitFemale());

        CampInstance saved = instanceRepo.save(instance);
        return new CampResponseDTO(saved);
    }

    @Transactional
    public CampResponseDTO updateCampDetails(Integer id, Integer coordinatorId, boolean isAdmin, CampCreationRequest request) {
        CampInstance camp = instanceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camp", "id", id));

        // Ownership check: only owner or admin can update
        verifyOwnershipOrAdmin(camp, coordinatorId, isAdmin);

        if (request.getDateStart() != null) camp.setDateStart(request.getDateStart());
        if (request.getDateEnd() != null) camp.setDateEnd(request.getDateEnd());
        if (request.getPrice() != null) camp.setPrice(request.getPrice());
        if (request.getLimitTotal() != null) camp.setLimitTotal(request.getLimitTotal());
        if (request.getLimitMale() != null) camp.setLimitMale(request.getLimitMale());
        if (request.getLimitFemale() != null) camp.setLimitFemale(request.getLimitFemale());

        CampInstance saved = instanceRepo.save(camp);
        return new CampResponseDTO(saved);
    }

    @Transactional
    public CampResponseDTO updateGlobalStatus(Integer id, Integer coordinatorId, boolean isAdmin, CampStatus newStatus) {
        CampInstance camp = instanceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camp", "id", id));

        // Ownership check: only owner or admin can update status
        verifyOwnershipOrAdmin(camp, coordinatorId, isAdmin);

        camp.setGlobalStatus(newStatus);
        CampInstance saved = instanceRepo.save(camp);

        districtStatusService.recalculateAllLocalStatuses(id, newStatus);

        return new CampResponseDTO(saved);
    }

    @Transactional
    public void archiveCamp(Integer id, Integer coordinatorId, boolean isAdmin) {
        CampInstance camp = instanceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Camp", "id", id));

        // Ownership check: only owner or admin can archive
        verifyOwnershipOrAdmin(camp, coordinatorId, isAdmin);

        camp.setGlobalStatus(CampStatus.ARCHIVED);
        instanceRepo.save(camp);

        districtStatusService.recalculateAllLocalStatuses(id, CampStatus.ARCHIVED);
    }

    // --- HELPER METHODS ---

    private void verifyOwnershipOrAdmin(CampInstance camp, Integer coordinatorId, boolean isAdmin) {
        if (isAdmin) {
            return; // Admins can do anything
        }
        
        if (camp.getCoordinator() == null || !camp.getCoordinator().getId().equals(coordinatorId)) {
            throw new AccessDeniedException("Camp", camp.getId());
        }
    }
}