package org.rotary.exchange.backend.message.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Request object for sending a message")
public class MessageRequest {
    @Schema(description = "Message content/body", example = "Hello, I have a question about the summer camp...")
    private String content;
    
    @Schema(description = "Message subject line", example = "Question about Summer Camp 2024")
    private String subject;
}
