package edu.sjsu.cmpe272.simpleblog.server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class MessageDetails {
    LocalDateTime date;
    String author;
    String message;
    String attachment;
    String signature;
}
