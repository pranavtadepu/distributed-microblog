package edu.sjsu.cmpe272.simpleblog.client.keygeneration;

import edu.sjsu.cmpe272.simpleblog.client.feign.BlogFeignClient;
import edu.sjsu.cmpe272.simpleblog.client.model.ListMessage;
import edu.sjsu.cmpe272.simpleblog.client.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

@Service
@Slf4j
public class IniWriter {

    @Value("${ini.file.path}")
    String path;
    String filename = "mb.ini";
    @Autowired
    BlogFeignClient blogFeignClient;

    public void useridPrivatekeyWriter(String userId, PrivateKey privateKey) {
        try {
            PrintWriter writer = new PrintWriter(filename);
            byte[] encode = Base64.getEncoder().encode(privateKey.getEncoded());
            writer.println("UserId=" + userId);
            writer.println("PrivateKey=" + new String(encode));
            writer.close();
        }
        catch (FileNotFoundException exception){
                        log.error(String.format("SMB-002 There was an error while writing to ini file, %s",exception.getMessage()));

        }

    }

    public PrivateKey getPrivateKey() {
        try {
            Properties properties = loadPropertiesFromFile(filename);
            String pkey = properties.getProperty("PrivateKey");
            if(pkey!=null) {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                byte[] decoded = Base64.getDecoder().decode(pkey);
                PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(decoded);
                return keyFactory.generatePrivate(pkcs8EncodedKeySpec);
            }
        }
        catch(IOException | InvalidKeySpecException | NoSuchAlgorithmException exception){
            log.error(String.format("SMB-002 There was an error in getting private key, %s",exception.getMessage()));
        }
        return null;
    }

    public String getMessageBase64Encoded(String attachment){
        return Base64.getEncoder().encodeToString(attachment.getBytes());
    }

    private static Properties loadPropertiesFromFile(String fileName) throws IOException {
        FileInputStream fis = new FileInputStream(fileName);
        Properties properties = new Properties();
        properties.load(fis);
        fis.close();
        return properties;
    }
    public String getUserid(){
        try {
            Properties properties = loadPropertiesFromFile(filename);
            return properties.getProperty("UserId");
        }
        catch (IOException exception){
            log.error(String.format("SMB-004 There was an error while reading userid from ini file, %s",exception.getMessage()));
        }
        return null;
    }

    public String convertpublicKeytoString(PublicKey publicKey){
        byte[] bytes = publicKey.getEncoded();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public void saveAttachment(String attachment, Integer id){
        try{
        String fileName = id+".out";
        byte[] decoded = Base64.getDecoder().decode(attachment.getBytes());
        String attachmentString = new String(decoded);

         FileOutputStream outputStream = new FileOutputStream(fileName);
            outputStream.write(attachmentString.getBytes());
        } catch(IOException e) {
            log.error(String.format("Error at saveAttachment, %s",e.getMessage()));
        }
        catch (Exception e){
            log.error(String.format("Exception error at save attachment %s",e.getMessage()));
        }
    }
    public  boolean verifyString(String string) {  //reference https://www.geeksforgeeks.org/java-string-tochararray-example/
        if (string == null || string.isEmpty()) {
            return false;
        }
        for (char c : string.toCharArray()) {
            if (!(c >= 'a' && c <= 'z') && !(c >= '0' && c <= '9')) {
                return false;
            }
        }
        return true;
    }

    public List<Message> getAllMessages(Integer startId, int Messagecount){
        int remainCount = Messagecount;
        int currStartID = startId;
        List<Message> MessagesFromDb = new ArrayList<>();
        while(remainCount>0){
            int requestLimit = Math.min(20,remainCount);
            List<Message> messageList = getMessageList(currStartID, requestLimit);
            if(messageList.isEmpty())
                break;
            MessagesFromDb.addAll(messageList);
            remainCount = remainCount - messageList.size();
            int n=messageList.size();
            Message message = messageList.get(n - 1);
            currStartID = message.getMessageId() + 1;
        }
        return MessagesFromDb;
    }

    private List<Message> getMessageList(Integer startId, Integer limit) {
        ListMessage listMessage = new ListMessage();
        listMessage.setNext(startId);
        listMessage.setLimit(limit);
        return blogFeignClient.listmessage(listMessage);
    }
}
