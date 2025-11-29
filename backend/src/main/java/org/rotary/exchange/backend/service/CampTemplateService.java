package org.rotary.exchange.backend.service;

import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.CampTemplateDTO;
import org.rotary.exchange.backend.dto.CampTemplateRequest;
import org.rotary.exchange.backend.exception.AccessDeniedException;
import org.rotary.exchange.backend.exception.ResourceNotFoundException;
import org.rotary.exchange.backend.model.CampTemplate;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.repository.CampTemplateRepository;
import org.rotary.exchange.backend.repository.CoordinatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CampTemplateService {

    private final CampTemplateRepository templateRepository;
    private final CoordinatorRepository coordinatorRepository;
    private final FileCleanupService fileCleanupService;

    /**
     * Get templates for a coordinator (only their own templates).
     */
    public List<CampTemplateDTO> getTemplatesForCoordinator(Integer coordinatorId) {
        return templateRepository.findByOwnerId(coordinatorId).stream()
                .map(CampTemplateDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get all templates (admin only).
     */
    public List<CampTemplateDTO> getAllTemplates() {
        return templateRepository.findAll().stream()
                .map(CampTemplateDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Get template by ID with ownership check.
     */
    public CampTemplateDTO getTemplateById(Integer id, Integer coordinatorId, boolean isAdmin) {
        CampTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", id));
        
        // Check ownership: admin can see all, coordinator can only see their own
        if (!isAdmin && (template.getOwner() == null || !template.getOwner().getId().equals(coordinatorId))) {
            throw new AccessDeniedException("You don't have access to this template");
        }
        
        return new CampTemplateDTO(template);
    }

    /**
     * Get template by ID (for internal use, no ownership check).
     */
    public CampTemplateDTO getTemplateById(Integer id) {
        CampTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", id));
        return new CampTemplateDTO(template);
    }

    @Transactional
    public CampTemplateDTO createTemplate(CampTemplateRequest request, Integer coordinatorId) {
        Coordinator owner = coordinatorRepository.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "id", coordinatorId));
        
        CampTemplate template = new CampTemplate();
        template.setOwner(owner);
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setAgeMin(request.getAgeMin());
        template.setAgeMax(request.getAgeMax());
        template.setImageUrl(request.getImageUrl());
        template.setFlyerPdfUrl(request.getFlyerPdfUrl());

        CampTemplate saved = templateRepository.save(template);
        return new CampTemplateDTO(saved);
    }

    /**
     * Legacy method for admin template creation (no owner).
     */
    @Transactional
    public CampTemplateDTO createTemplate(CampTemplateRequest request) {
        CampTemplate template = new CampTemplate();
        template.setOwner(null); // Admin template - no owner
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setAgeMin(request.getAgeMin());
        template.setAgeMax(request.getAgeMax());
        template.setImageUrl(request.getImageUrl());
        template.setFlyerPdfUrl(request.getFlyerPdfUrl());

        CampTemplate saved = templateRepository.save(template);
        return new CampTemplateDTO(saved);
    }

    @Transactional
    public CampTemplateDTO updateTemplate(Integer id, CampTemplateRequest request, Integer coordinatorId, boolean isAdmin) {
        CampTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", id));

        // Check ownership: admin can update all, coordinator can only update their own
        if (!isAdmin && (template.getOwner() == null || !template.getOwner().getId().equals(coordinatorId))) {
            throw new AccessDeniedException("You don't have permission to update this template");
        }

        // Track old URLs for cleanup
        String oldImageUrl = template.getImageUrl();
        String oldFlyerUrl = template.getFlyerPdfUrl();

        if (request.getName() != null) template.setName(request.getName());
        if (request.getDescription() != null) template.setDescription(request.getDescription());
        if (request.getAgeMin() != null) template.setAgeMin(request.getAgeMin());
        if (request.getAgeMax() != null) template.setAgeMax(request.getAgeMax());
        if (request.getImageUrl() != null) template.setImageUrl(request.getImageUrl());
        if (request.getFlyerPdfUrl() != null) template.setFlyerPdfUrl(request.getFlyerPdfUrl());

        CampTemplate saved = templateRepository.save(template);

        // Cleanup old files if they were replaced
        if (request.getImageUrl() != null && !request.getImageUrl().equals(oldImageUrl)) {
            fileCleanupService.scheduleCleanup(oldImageUrl, FileCleanupService.FileType.IMAGE);
        }
        if (request.getFlyerPdfUrl() != null && !request.getFlyerPdfUrl().equals(oldFlyerUrl)) {
            fileCleanupService.scheduleCleanup(oldFlyerUrl, FileCleanupService.FileType.FLYER);
        }

        return new CampTemplateDTO(saved);
    }

    @Transactional
    public CampTemplateDTO updateTemplate(Integer id, CampTemplateRequest request) {
        CampTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", id));

        // Track old URLs for cleanup
        String oldImageUrl = template.getImageUrl();
        String oldFlyerUrl = template.getFlyerPdfUrl();

        if (request.getName() != null) template.setName(request.getName());
        if (request.getDescription() != null) template.setDescription(request.getDescription());
        if (request.getAgeMin() != null) template.setAgeMin(request.getAgeMin());
        if (request.getAgeMax() != null) template.setAgeMax(request.getAgeMax());
        if (request.getImageUrl() != null) template.setImageUrl(request.getImageUrl());
        if (request.getFlyerPdfUrl() != null) template.setFlyerPdfUrl(request.getFlyerPdfUrl());

        CampTemplate saved = templateRepository.save(template);

        // Cleanup old files if they were replaced
        if (request.getImageUrl() != null && !request.getImageUrl().equals(oldImageUrl)) {
            fileCleanupService.scheduleCleanup(oldImageUrl, FileCleanupService.FileType.IMAGE);
        }
        if (request.getFlyerPdfUrl() != null && !request.getFlyerPdfUrl().equals(oldFlyerUrl)) {
            fileCleanupService.scheduleCleanup(oldFlyerUrl, FileCleanupService.FileType.FLYER);
        }

        return new CampTemplateDTO(saved);
    }

    @Transactional
    public void deleteTemplate(Integer id, Integer coordinatorId, boolean isAdmin) {
        CampTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", id));

        // Check ownership: admin can delete all, coordinator can only delete their own
        if (!isAdmin && (template.getOwner() == null || !template.getOwner().getId().equals(coordinatorId))) {
            throw new AccessDeniedException("You don't have permission to delete this template");
        }

        // Store URLs before deletion
        String imageUrl = template.getImageUrl();
        String flyerUrl = template.getFlyerPdfUrl();

        templateRepository.delete(template);

        // Cleanup files after deletion
        fileCleanupService.scheduleCleanup(imageUrl, FileCleanupService.FileType.IMAGE);
        fileCleanupService.scheduleCleanup(flyerUrl, FileCleanupService.FileType.FLYER);
    }

    @Transactional
    public void deleteTemplate(Integer id) {
        CampTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", id));

        // Store URLs before deletion
        String imageUrl = template.getImageUrl();
        String flyerUrl = template.getFlyerPdfUrl();

        templateRepository.delete(template);

        // Cleanup files after deletion
        fileCleanupService.scheduleCleanup(imageUrl, FileCleanupService.FileType.IMAGE);
        fileCleanupService.scheduleCleanup(flyerUrl, FileCleanupService.FileType.FLYER);
    }

    /**
     * Verify that a coordinator owns a template (used by CampManagementService).
     */
    public CampTemplate getTemplateIfOwned(Integer templateId, Integer coordinatorId) {
        CampTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResourceNotFoundException("CampTemplate", "id", templateId));
        
        if (template.getOwner() == null || !template.getOwner().getId().equals(coordinatorId)) {
            throw new AccessDeniedException("You don't have access to this template");
        }
        
        return template;
    }
}
