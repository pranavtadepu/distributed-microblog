package edu.sjsu.cmpe272.simpleblog.client;

import edu.sjsu.cmpe272.simpleblog.client.feign.BlogFeignClient;
import edu.sjsu.cmpe272.simpleblog.client.keygeneration.IniWriter;
import edu.sjsu.cmpe272.simpleblog.client.keygeneration.KeyPairService;
import edu.sjsu.cmpe272.simpleblog.client.model.*;
import edu.sjsu.cmpe272.simpleblog.client.signing.MessageSigningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

import static edu.sjsu.cmpe272.simpleblog.client.model.constants.NOT_VALID_USER;

@SpringBootApplication
@EnableFeignClients
@Command
@Slf4j
public class ClientApplication implements CommandLineRunner, ExitCodeGenerator {

    @Autowired
    CommandLine.IFactory iFactory;

    @Autowired
    private ConfigurableApplicationContext context;
    @Autowired
    KeyPairService keyPairService;

    @Autowired
    IniWriter iniWriter;

    @Autowired
    MessageSigningService messageSigningService;

    @Autowired
    BlogFeignClient blogFeignClient;


    @Command
    public int post(@Parameters String message, @Parameters(defaultValue = "null") String attachment) {
            try {
                String messageBase64Encoded = null;
                MessageDetails messageDetails = new MessageDetails();
                if (Objects.nonNull(attachment)) {
                    messageBase64Encoded = iniWriter.getMessageBase64Encoded(attachment);
                    messageDetails.setAttachment(messageBase64Encoded);
                }
                String userid = iniWriter.getUserid();
                messageDetails.setMessage(message);
                messageDetails.setAuthor(userid);
                messageDetails.setDate(LocalDateTime.now(ZoneOffset.UTC));
                String messageJson = messageSigningService.getMessageJson(messageDetails);
                String finalJson = messageJson.replaceAll("\\s", "");
                String signature = messageSigningService.getmessagesigned(userid, finalJson);
                SignedMessage signedMessage = new SignedMessage();
                signedMessage.setDate(messageDetails.getDate());
                if (Objects.nonNull(messageDetails.getAttachment()))
                    signedMessage.setAttachment(messageDetails.getAttachment());
                signedMessage.setAuthor(messageDetails.getAuthor());
                signedMessage.setMessage(messageDetails.getMessage());
                signedMessage.setSignature(signature);
                String messageId = blogFeignClient.postMessage(signedMessage);
            }
            catch (Exception exception){
                log.error(exception.getMessage());
            }
        return 2;
    }

    @Command
    int create(@Parameters String id) {

            if (iniWriter.verifyString(id)) {
                ResponseGetPublicKey responseGetPublicKey = blogFeignClient.getPublicKey(id);
                if (!Objects.equals(responseGetPublicKey.getPublicKey(), NOT_VALID_USER)) {
                    System.out.println("UserName Already taken");
                    return 2;
                }
                KeyPair keyPair = keyPairService.keypairhenerator();
                PrivateKey privateKey = keyPair.getPrivate();
                iniWriter.useridPrivatekeyWriter(id, privateKey);
                UserCreation userCreation = new UserCreation();
                userCreation.setUser(id);
                userCreation.setPublicKey(iniWriter.convertpublicKeytoString(keyPair.getPublic()));
                blogFeignClient.createUser(userCreation);
            } else {
                System.out.println("userid must consist of only lowercase letters and numbers");
            }

        return 2;
    }
    @Command
    int list(@Option(names="--starting",defaultValue = "-1")int startid,@Option(names = "--count",defaultValue = "10")int count,@Option(names = "--save-attachment",defaultValue = "false")Boolean saveAttachment){
        try {
            List<Message> messages = iniWriter.getAllMessages(startid, count);
            if (saveAttachment == true) {
                for (Message i : messages) {
                    if (Objects.nonNull(i.getAttachment()) && !Objects.equals(i.getAttachment(), "bnVsbA==")) {
                        iniWriter.saveAttachment(i.getAttachment(), i.getMessageId());
                    }
                }
            }
            for (Message j : messages) {
                if (Objects.nonNull(j.getAttachment()) && !Objects.equals(j.getAttachment(), "bnVsbA==")) {
                    System.out.println(j.getMessageId() + ":" + j.getAuthor() + " says " + "\"" + j.getMessage() + "\"" + " 📎");
                } else {
                    System.out.println(j.getMessageId() + ":" + j.getAuthor() + " says " + "\"" + j.getMessage() + "\"");
                }
            }
        }
        catch (Exception exception){
            log.error(exception.getMessage());
        }
        return 2;
    }
    public static void main(String[] args) {
                SpringApplication.run(ClientApplication.class, args);
    }

    int exitCode;

    @Override
    public void run(String... args) throws Exception {
        exitCode = new CommandLine(this, iFactory).execute(args);
    }

    @Override
    public int getExitCode() {
        return exitCode;
    }
}
