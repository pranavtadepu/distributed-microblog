package edu.sjsu.cmpe272.simpleblog.server.controller;

import edu.sjsu.cmpe272.simpleblog.server.dto.*;
import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import edu.sjsu.cmpe272.simpleblog.server.respository.MessageDAO;
import edu.sjsu.cmpe272.simpleblog.server.services.UserService;
import edu.sjsu.cmpe272.simpleblog.server.services.VerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
public class Welcome {

    @Autowired
    UserService userService;
    @Autowired
    VerificationService verificationService;
    @Autowired
    MessageDAO messageDAO;
    @GetMapping("/")
    ResponseEntity<String> getWelcome() {
        return ResponseEntity.ok("Welcome!");
    }

    @PostMapping("/messages/create")
    Response postmessage(@RequestBody MessageDetails messageDetails) {
        Response response = new Response();
        String messageJson = verificationService.getMessageJson(messageDetails);
        String publicKey = userService.getPublicKey(messageDetails.getAuthor());
        boolean b = verificationService.verifySignature(messageDetails.getSignature(), messageJson, publicKey);
        if(b)
        {
            Integer messageID = userService.saveMessageAndGetMessageID(messageDetails);
            response.setMessageId(messageID);
        }
        else {
            response.setError("Signature didn't Match, Data is corrupted");
        }
        return response;
    }

    @PostMapping("/messages/list")
    List<ResponseMessage> listmessage(@RequestBody ListMessage listMessage){
        List<ResponseMessage> responseMessageList=null;
        if(listMessage.getLimit()<20) {
            List<Message> messageList;
            if (listMessage.getNext() == -1) {
                messageList = messageDAO.findTopNMessages(listMessage.getLimit());
            } else {
                messageList = messageDAO.findNextNMessages(listMessage.getNext(), listMessage.getLimit());
            }
            responseMessageList = new ArrayList<>();
            for (Message i : messageList) {
                ResponseMessage responseMessage = new ResponseMessage();
                responseMessage.setMessage(i.getMessage());
                responseMessage.setSignature(i.getSignature());
                if(Objects.nonNull(i.getAttachment()) && !Objects.equals(i.getAttachment(),"bnVsbA=="))
                responseMessage.setAttachment(i.getAttachment());
                responseMessage.setAuthor(i.getAuthor());
                responseMessage.setDate(i.getDate());
                responseMessage.setMessageId(i.getId());
                responseMessageList.add(responseMessage);
            }
        }
        else {
            System.out.println("Limit should not exceed 20");
            throw new ErrorResponseException(HttpStatusCode.valueOf(400));
        }
        return responseMessageList;
    }

    @PostMapping("/user/create")
    Response createuser(@RequestBody UserCreation userCreation){
        System.out.println(userCreation.getPublicKey());
        Response response = new Response();
        String msg = userService.createUser(userCreation);
        response.setWelcome_msg(msg);
        return response;
    }
    @GetMapping("/user/{username}/public-key")
    Response getPublicKey(@PathVariable String username){
        Response response = new Response();
        String publicKey = userService.getPublicKey(username);
        if(Objects.nonNull(publicKey)){
            response.setPublicKey(publicKey);
        }
        else
        {
            response.setPublicKey("Not a valid User");
        }
        return response;
    }

    @PostMapping("/user/attachment")
    Response getAttachment(@RequestBody String userid){
        Response response = new Response();
        Message byAuthor = messageDAO.findByAuthor(userid);
        response.setAttachment(byAuthor.getAttachment());
        return response;
    }

}
