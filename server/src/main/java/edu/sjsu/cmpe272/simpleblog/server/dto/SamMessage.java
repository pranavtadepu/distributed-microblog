package edu.sjsu.cmpe272.simpleblog.server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Data
public class SamMessage {
    LocalDateTime date;
    String author;
    String message;
    String attachment;
}
