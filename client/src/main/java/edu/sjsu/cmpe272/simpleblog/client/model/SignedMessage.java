package edu.sjsu.cmpe272.simpleblog.client.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class SignedMessage {
    LocalDateTime date;
    String author;
    String message;
    String attachment;
    String signature;
}
