package edu.sjsu.cmpe272.simpleblog.server.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserCreation {
    String user;
    String publicKey;
}
