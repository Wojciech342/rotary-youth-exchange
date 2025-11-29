package org.rotary.exchange.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.rotary.exchange.backend.repository.CampTemplateRepository;
import org.rotary.exchange.backend.repository.CoordinatorRepository;
import org.springframework.stereotype.Service;

/**
 * Service responsible for cleaning up unused files.
 * Before deleting any file, it checks if the file URL is still being used
 * by any entity in the database to prevent accidental data loss.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FileCleanupService {

    private final FileStorageService fileStorageService;
    private final CampTemplateRepository campTemplateRepository;
    private final CoordinatorRepository coordinatorRepository;

    /**
     * Attempts to delete an image file if it's not used anywhere else.
     * Checks CampTemplate.imageUrl and Coordinator.profilePictureUrl
     *
     * @param imageUrl The URL of the image to potentially delete
     * @return true if the file was deleted, false if it's still in use or doesn't exist
     */
    public boolean cleanupImageIfUnused(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return false;
        }

        // Check if this image is used by any CampTemplate
        if (campTemplateRepository.existsByImageUrl(imageUrl)) {
            log.debug("Image {} is still used by a CampTemplate, skipping cleanup", imageUrl);
            return false;
        }

        // Check if this image is used by any Coordinator as profile picture
        if (coordinatorRepository.existsByProfilePictureUrl(imageUrl)) {
            log.debug("Image {} is still used by a Coordinator, skipping cleanup", imageUrl);
            return false;
        }

        // Safe to delete
        boolean deleted = fileStorageService.deleteFile(imageUrl);
        if (deleted) {
            log.info("Cleaned up unused image: {}", imageUrl);
        }
        return deleted;
    }

    /**
     * Attempts to delete a PDF flyer file if it's not used anywhere else.
     * Checks CampTemplate.flyerPdfUrl
     *
     * @param flyerUrl The URL of the flyer to potentially delete
     * @return true if the file was deleted, false if it's still in use or doesn't exist
     */
    public boolean cleanupFlyerIfUnused(String flyerUrl) {
        if (flyerUrl == null || flyerUrl.isBlank()) {
            return false;
        }

        // Check if this flyer is used by any CampTemplate
        if (campTemplateRepository.existsByFlyerPdfUrl(flyerUrl)) {
            log.debug("Flyer {} is still used by a CampTemplate, skipping cleanup", flyerUrl);
            return false;
        }

        // Safe to delete
        boolean deleted = fileStorageService.deleteFile(flyerUrl);
        if (deleted) {
            log.info("Cleaned up unused flyer: {}", flyerUrl);
        }
        return deleted;
    }

    /**
     * Schedule cleanup for an old file URL after it has been replaced.
     * This should be called AFTER the database has been updated with the new URL.
     *
     * @param oldUrl The old URL that was replaced
     * @param fileType The type of file ("image" or "flyer")
     */
    public void scheduleCleanup(String oldUrl, FileType fileType) {
        if (oldUrl == null || oldUrl.isBlank()) {
            return;
        }

        // Run cleanup (in a real production app, this could be async or queued)
        switch (fileType) {
            case IMAGE -> cleanupImageIfUnused(oldUrl);
            case FLYER -> cleanupFlyerIfUnused(oldUrl);
        }
    }

    public enum FileType {
        IMAGE,
        FLYER
    }
}
