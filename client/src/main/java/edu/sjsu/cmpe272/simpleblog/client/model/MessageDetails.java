package edu.sjsu.cmpe272.simpleblog.client.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@Data
public class MessageDetails {
    LocalDateTime date;
    String author;
    String message;
    String attachment;
}
