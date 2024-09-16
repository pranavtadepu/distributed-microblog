package edu.sjsu.cmpe272.simpleblog.server.services;


import edu.sjsu.cmpe272.simpleblog.server.dto.MessageDetails;
import edu.sjsu.cmpe272.simpleblog.server.dto.UserCreation;
import edu.sjsu.cmpe272.simpleblog.server.model.Message;
import edu.sjsu.cmpe272.simpleblog.server.model.Userdata;
import edu.sjsu.cmpe272.simpleblog.server.respository.MessageDAO;
import edu.sjsu.cmpe272.simpleblog.server.respository.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserService {

    @Autowired
    UserDAO userDAO;
    @Autowired
    MessageDAO messageDAO;
    public String createUser(UserCreation userCreation){
        Userdata userdata = new Userdata();
        userdata.setName(userCreation.getUser());
        userdata.setPublic_key(userCreation.getPublicKey());
        userDAO.save(userdata);
        return "welcome";
    }

    public String getPublicKey(String username){
        Userdata userdata = userDAO.findByName(username);
        if(Objects.nonNull(userdata)){
             return userdata.getPublic_key();
         }
        else {
            return null;
        }
    }
    public Integer saveMessageAndGetMessageID(MessageDetails messageDetails){
        Message message = new Message();
        message.setMessage(messageDetails.getMessage());
        message.setDate(messageDetails.getDate());
        message.setAttachment(messageDetails.getAttachment());
        message.setSignature(messageDetails.getSignature());
        message.setAuthor(messageDetails.getAuthor());
        Message savedMessage = messageDAO.save(message);
        return savedMessage.getId();
    }
}
