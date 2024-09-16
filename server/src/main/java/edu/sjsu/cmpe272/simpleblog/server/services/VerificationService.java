package edu.sjsu.cmpe272.simpleblog.server.services;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.sjsu.cmpe272.simpleblog.server.dto.MessageDetails;
import edu.sjsu.cmpe272.simpleblog.server.dto.SamMessage;
import edu.sjsu.cmpe272.simpleblog.server.respository.MessageDAO;
import edu.sjsu.cmpe272.simpleblog.server.respository.UserDAO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Objects;

@Service
@Slf4j
public class VerificationService {
    @Autowired
    UserDAO userDAO;
    @Autowired
    MessageDAO messageDAO;
    @Autowired
    ObjectMapper objectMapper;
    public boolean verifySignature(String sign,String message,String publicStringKey) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] messageDigestBytes = messageDigest.digest(message.getBytes());
            byte[] decodedSignature = Base64.getDecoder().decode(sign);
            Signature signature = Signature.getInstance("SHA256withRSA");
            PublicKey key = getpublicKeyFromString(publicStringKey);
            signature.initVerify(key);
            signature.update(messageDigestBytes);
            return signature.verify(decodedSignature);
        }catch (NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException | SignatureException exception){
            log.error(String.format("SMAS-There was an error while verifying signature,%s",exception.getMessage()));
        }
        return true;
    }
    public PublicKey getpublicKeyFromString(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
    public String getMessageJson(MessageDetails messageDetails){
        SamMessage message = new SamMessage();
        message.setMessage(messageDetails.getMessage());
        message.setDate(messageDetails.getDate());
        message.setAuthor(messageDetails.getAuthor());
        if(Objects.nonNull(messageDetails.getAttachment()))
        message.setAttachment(messageDetails.getAttachment());
        try {
            String messageJson = objectMapper.writeValueAsString(message);
            return messageJson.replaceAll("\\s","");
        }
        catch (JsonProcessingException exception){
            log.error(String.format("Server-0020 Error while parsing messageDetails object, %s",exception.getMessage()));
        }
        return null;
    }
}
