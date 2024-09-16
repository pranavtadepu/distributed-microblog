package edu.sjsu.cmpe272.simpleblog.server.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
public class Response {
    LocalDateTime date;
    String author;
    String message;
    String attachment;
    String signature;
    String error;
    String publicKey;
    Integer messageId;
    String welcome_msg;
}
