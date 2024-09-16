package edu.sjsu.cmpe272.simpleblog.client.model;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreation {
    private String user;
    private String publicKey;
}
