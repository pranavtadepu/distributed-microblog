package edu.sjsu.cmpe272.simpleblog.client.signing;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sjsu.cmpe272.simpleblog.client.keygeneration.IniWriter;
import edu.sjsu.cmpe272.simpleblog.client.model.MessageDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.util.Base64;

@Service
@Slf4j
public class MessageSigningService {
    @Autowired
    ObjectMapper objectMapper;

   @Autowired
   IniWriter iniWriter;

   public String getMessageJson(MessageDetails messageDetails){
       try {
           return  objectMapper.writeValueAsString(messageDetails);
       }
       catch (JsonProcessingException exception){
           log.error(String.format("SMBC-0020 Error while parsing messageDetails object, %s",exception.getMessage()));
       }
       return null;
       }
           public String getmessagesigned(String userid, String message) {
               try {
                   MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                   byte[] digestBytes = messageDigest.digest(message.getBytes());
                   Signature signature = Signature.getInstance("SHA256withRSA");
                   signature.initSign(iniWriter.getPrivateKey());
                   signature.update(digestBytes);
                   byte[] signed = signature.sign();
                   return Base64.getEncoder().encodeToString(signed);
               }
               catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException exception){
                   log.error(String.format("SMBC-0021 Error while signing the message, %s",exception.getMessage()));
               }
              return null;
           }


}
