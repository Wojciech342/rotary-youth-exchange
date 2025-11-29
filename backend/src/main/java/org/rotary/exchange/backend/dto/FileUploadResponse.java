package org.rotary.exchange.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response object for file upload operations")
public class FileUploadResponse {
    @Schema(description = "URL to access the uploaded file", example = "/api/files/images/abc123.jpg")
    private String url;
    
    @Schema(description = "Status message", example = "Image uploaded successfully")
    private String message;
}
