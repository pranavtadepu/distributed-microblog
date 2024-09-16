package edu.sjsu.cmpe272.simpleblog.client.keygeneration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.*;

@Service
@Slf4j
public class KeyPairService {

//Reference: Used https://docs.oracle.com/javase/tutorial/security/apisign/step2.html
   public KeyPair keypairhenerator(){
       KeyPairGenerator keyPairGenerator;
       try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
           SecureRandom random = SecureRandom.getInstanceStrong();
           keyPairGenerator.initialize(2056, random);
           return keyPairGenerator.generateKeyPair();
       }catch (NoSuchAlgorithmException exception){
           log.error(String.format("SMB-001 There was an error at KeyPairService,%s",exception.getMessage()));
       }
       return null;
   }
}
