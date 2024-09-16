package edu.sjsu.cmpe272.simpleblog.client.feign;


import edu.sjsu.cmpe272.simpleblog.client.model.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(name = "${service.name}", url = "${service.url}")
public interface BlogFeignClient {
    @PostMapping("/messages/create")
    String postMessage(SignedMessage signedMessage);

    @PostMapping("/user/create")
    String createUser(UserCreation userCreation);

    @GetMapping("/user/{username}/public-key")
    ResponseGetPublicKey getPublicKey(@PathVariable String username);

    @PostMapping("/messages/list")
    List<Message> listmessage(ListMessage listMessage);
    @PostMapping("/user/attachment")
    String getAttachment(String userid);
}
