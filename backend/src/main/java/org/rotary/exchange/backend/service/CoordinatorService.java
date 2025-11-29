package org.rotary.exchange.backend.service;

import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.CoordinatorDTO;
import org.rotary.exchange.backend.dto.CoordinatorUpdateRequest;
import org.rotary.exchange.backend.exception.ResourceNotFoundException;
import org.rotary.exchange.backend.model.Coordinator;
import org.rotary.exchange.backend.model.District;
import org.rotary.exchange.backend.repository.CoordinatorRepository;
import org.rotary.exchange.backend.repository.DistrictRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CoordinatorService {

    private final CoordinatorRepository coordinatorRepository;
    private final DistrictRepository districtRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileCleanupService fileCleanupService;

    public CoordinatorDTO getCoordinatorById(Integer id) {
        Coordinator coordinator = coordinatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "id", id));
        return new CoordinatorDTO(coordinator);
    }

    public List<CoordinatorDTO> getAllCoordinators() {
        return coordinatorRepository.findAll().stream()
                .map(CoordinatorDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CoordinatorDTO updateProfile(Authentication authentication, CoordinatorUpdateRequest request) {
        String email = authentication.getName();
        Coordinator coordinator = coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "email", email));

        String oldProfilePictureUrl = coordinator.getProfilePictureUrl();
        updateCoordinatorFields(coordinator, request);
        Coordinator saved = coordinatorRepository.save(coordinator);

        // Cleanup old profile picture if it was replaced
        if (request.getProfilePictureUrl() != null && 
            !request.getProfilePictureUrl().equals(oldProfilePictureUrl)) {
            fileCleanupService.scheduleCleanup(oldProfilePictureUrl, FileCleanupService.FileType.IMAGE);
        }

        return new CoordinatorDTO(saved);
    }

    @Transactional
    public CoordinatorDTO updateCoordinatorById(Integer id, CoordinatorUpdateRequest request) {
        Coordinator coordinator = coordinatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "id", id));

        String oldProfilePictureUrl = coordinator.getProfilePictureUrl();
        updateCoordinatorFields(coordinator, request);
        Coordinator saved = coordinatorRepository.save(coordinator);

        // Cleanup old profile picture if it was replaced
        if (request.getProfilePictureUrl() != null && 
            !request.getProfilePictureUrl().equals(oldProfilePictureUrl)) {
            fileCleanupService.scheduleCleanup(oldProfilePictureUrl, FileCleanupService.FileType.IMAGE);
        }

        return new CoordinatorDTO(saved);
    }

    @Transactional
    public void changePassword(Authentication authentication, String oldPassword, String newPassword) {
        String email = authentication.getName();
        Coordinator coordinator = coordinatorRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "email", email));

        if (!passwordEncoder.matches(oldPassword, coordinator.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        coordinator.setPasswordHash(passwordEncoder.encode(newPassword));
        coordinatorRepository.save(coordinator);
    }

    @Transactional
    public CoordinatorDTO assignDistrict(Integer coordinatorId, Integer districtId) {
        Coordinator coordinator = coordinatorRepository.findById(coordinatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "id", coordinatorId));

        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException("District", "id", districtId));

        coordinator.setDistrict(district);
        Coordinator saved = coordinatorRepository.save(coordinator);
        return new CoordinatorDTO(saved);
    }

    @Transactional
    public void deleteCoordinator(Integer id) {
        Coordinator coordinator = coordinatorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coordinator", "id", id));

        String profilePictureUrl = coordinator.getProfilePictureUrl();
        coordinatorRepository.delete(coordinator);

        // Cleanup profile picture after deletion
        fileCleanupService.scheduleCleanup(profilePictureUrl, FileCleanupService.FileType.IMAGE);
    }

    private void updateCoordinatorFields(Coordinator coordinator, CoordinatorUpdateRequest request) {
        if (request.getFirstName() != null) coordinator.setFirstName(request.getFirstName());
        if (request.getLastName() != null) coordinator.setLastName(request.getLastName());
        if (request.getPhone() != null) coordinator.setPhone(request.getPhone());
        if (request.getProfilePictureUrl() != null) coordinator.setProfilePictureUrl(request.getProfilePictureUrl());
        if (request.getDescription() != null) coordinator.setDescription(request.getDescription());
    }
}
