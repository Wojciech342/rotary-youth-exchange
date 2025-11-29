package org.rotary.exchange.backend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.rotary.exchange.backend.dto.FileUploadResponse;
import org.rotary.exchange.backend.exception.ErrorResponse;
import org.rotary.exchange.backend.service.FileStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.MalformedURLException;
import java.nio.file.Path;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Tag(name = "Files", description = "File upload and download operations for images and PDF flyers")
public class FileController {

    private final FileStorageService fileStorageService;

    @Operation(
            summary = "Upload an image",
            description = "Uploads an image file for use in camp templates or coordinator profiles. Accepts JPEG, PNG, GIF, and WebP formats. Max size: 5MB.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires COORDINATOR or ADMIN role")
    })
    @PostMapping("/images")
    @PreAuthorize("hasRole('COORDINATOR') or hasRole('ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadImage(
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.uploadImage(file);
        return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Image uploaded successfully"));
    }

    @Operation(
            summary = "Upload a PDF flyer",
            description = "Uploads a PDF flyer document for camp information. Max size: 10MB.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Flyer uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file type or size",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires COORDINATOR or ADMIN role")
    })
    @PostMapping("/flyers")
    @PreAuthorize("hasRole('COORDINATOR') or hasRole('ADMIN')")
    public ResponseEntity<FileUploadResponse> uploadFlyer(
            @Parameter(description = "PDF file to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        String fileUrl = fileStorageService.uploadFlyer(file);
        return ResponseEntity.ok(new FileUploadResponse(fileUrl, "Flyer uploaded successfully"));
    }

    @Operation(
            summary = "Delete a file",
            description = "Permanently deletes a file from the server. Only admins can delete files.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "File deleted successfully"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized - requires ADMIN role"),
            @ApiResponse(responseCode = "404", description = "File not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteFile(
            @Parameter(description = "URL of the file to delete", required = true, example = "/api/files/images/abc123.jpg")
            @RequestParam("url") String fileUrl) {
        fileStorageService.deleteFile(fileUrl);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Serve a file",
            description = "Retrieves and serves a file (image or PDF) by its type and filename. Public endpoint for viewing camp images and flyers."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "File served successfully",
                    content = @Content(mediaType = "application/octet-stream")),
            @ApiResponse(responseCode = "400", description = "Invalid request"),
            @ApiResponse(responseCode = "404", description = "File not found")
    })
    @GetMapping("/{type}/{filename:.+}")
    public ResponseEntity<Resource> serveFile(
            @Parameter(description = "File type (images or flyers)", required = true, example = "images")
            @PathVariable String type,
            @Parameter(description = "Filename with extension", required = true, example = "abc123.jpg")
            @PathVariable String filename) {
        try {
            Path filePath = fileStorageService.getFilePath(type + "/" + filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = determineContentType(filename);
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, 
                            "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private String determineContentType(String filename) {
        String lowerFilename = filename.toLowerCase();
        if (lowerFilename.endsWith(".pdf")) {
            return "application/pdf";
        } else if (lowerFilename.endsWith(".png")) {
            return "image/png";
        } else if (lowerFilename.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerFilename.endsWith(".webp")) {
            return "image/webp";
        }
        return "image/jpeg";  // Default to JPEG
    }
}
