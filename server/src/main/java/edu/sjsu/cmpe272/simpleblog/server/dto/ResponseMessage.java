package edu.sjsu.cmpe272.simpleblog.server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class ResponseMessage {
    Integer messageId;
    LocalDateTime date;
    String author;
    String message;
    String attachment;
    String signature;
}
