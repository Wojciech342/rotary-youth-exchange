package org.rotary.exchange.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${file.max-size:10485760}")  // 10MB default
    private long maxFileSize;

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    private static final Set<String> ALLOWED_PDF_TYPES = Set.of(
            "application/pdf"
    );

    private Path uploadPath;

    @PostConstruct
    public void init() {
        uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(uploadPath);
            Files.createDirectories(uploadPath.resolve("images"));
            Files.createDirectories(uploadPath.resolve("flyers"));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directories", e);
        }
    }

    /**
     * Upload an image file (JPEG, PNG, GIF, WebP)
     * @param file The uploaded file
     * @return The relative URL path to access the file
     */
    public String uploadImage(MultipartFile file) {
        validateFile(file, ALLOWED_IMAGE_TYPES);
        return storeFile(file, "images");
    }

    /**
     * Upload a PDF flyer
     * @param file The uploaded PDF file
     * @return The relative URL path to access the file
     */
    public String uploadFlyer(MultipartFile file) {
        validateFile(file, ALLOWED_PDF_TYPES);
        return storeFile(file, "flyers");
    }

    /**
     * Delete a file by its URL path
     * @param fileUrl The relative URL path of the file
     * @return true if file was deleted, false if it didn't exist
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) {
            return false;
        }

        try {
            // Remove leading /uploads/ or /files/ if present
            String relativePath = fileUrl;
            if (relativePath.startsWith("/uploads/")) {
                relativePath = relativePath.substring(9);
            } else if (relativePath.startsWith("/files/")) {
                relativePath = relativePath.substring(7);
            }

            Path filePath = uploadPath.resolve(relativePath).normalize();
            
            // Security check: ensure the path is still within upload directory
            if (!filePath.startsWith(uploadPath)) {
                throw new IllegalArgumentException("Invalid file path");
            }

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file: " + fileUrl, e);
        }
    }

    /**
     * Get the full path to a stored file
     * @param fileUrl The relative URL path
     * @return The full file path
     */
    public Path getFilePath(String fileUrl) {
        String relativePath = fileUrl;
        if (relativePath.startsWith("/uploads/")) {
            relativePath = relativePath.substring(9);
        } else if (relativePath.startsWith("/files/")) {
            relativePath = relativePath.substring(7);
        } else if (relativePath.startsWith("/api/files/")) {
            relativePath = relativePath.substring(11);
        }
        
        Path filePath = uploadPath.resolve(relativePath).normalize();
        
        // Security check: ensure the path is still within upload directory
        if (!filePath.startsWith(uploadPath)) {
            throw new IllegalArgumentException("Invalid file path - directory traversal detected");
        }
        
        return filePath;
    }

    private void validateFile(MultipartFile file, Set<String> allowedTypes) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty or null");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size of " + 
                    (maxFileSize / 1024 / 1024) + "MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || !allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("File type '" + contentType + 
                    "' is not allowed. Allowed types: " + allowedTypes);
        }
    }

    private String storeFile(MultipartFile file, String subDirectory) {
        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        
        // Security check: prevent directory traversal
        if (originalFilename.contains("..")) {
            throw new IllegalArgumentException("Filename contains invalid path sequence");
        }

        // Generate unique filename to prevent overwrites
        String extension = getFileExtension(originalFilename);
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        try {
            Path targetDir = uploadPath.resolve(subDirectory);
            Path targetPath = targetDir.resolve(uniqueFilename);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            // Return the relative URL path
            return "/uploads/" + subDirectory + "/" + uniqueFilename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file: " + originalFilename, e);
        }
    }

    private String getFileExtension(String filename) {
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filename.length() - 1) {
            return filename.substring(dotIndex);
        }
        return "";
    }
}
