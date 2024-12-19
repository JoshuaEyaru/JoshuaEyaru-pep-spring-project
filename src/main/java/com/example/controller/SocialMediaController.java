package com.example.controller;

import javax.naming.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azul.crs.client.Response;
import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.ClientErrorException;
import com.example.exception.DuplicateAccountException;
import com.example.service.AccountService;
import com.example.service.MessageService;
import java.util.List;
import java.util.Optional;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller using Spring. The endpoints you will need can be
 * found in readme.md as well as the test cases. You be required to use the @GET/POST/PUT/DELETE/etc Mapping annotations
 * where applicable as well as the @ResponseBody and @PathVariable annotations. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */

 @RestController
public class SocialMediaController {

    @Autowired
    AccountService accountService;

    @Autowired
    MessageService messageService;

    // Method for registering new users
    @PostMapping("/register")
    public ResponseEntity<Account> register(@RequestBody Account newAccount){
        try{
            Account saveAccount = accountService.register(newAccount);
            return ResponseEntity.status(HttpStatus.OK).body(saveAccount);
        }catch(DuplicateAccountException ex){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }catch(ClientErrorException ex){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }catch(Exception ex){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody Account account){
        try{
            Account userlogged = accountService.login(account.getUsername(), account.getPassword());
            return ResponseEntity.status(HttpStatus.OK).body(userlogged);
        } catch(AuthenticationException ex){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    
    @PostMapping("/messages")
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        try {
            // Call the service to create the message
            Message createdMessage = messageService.createMessage(message.getPostedBy(), message.getMessageText(), message.getTimePostedEpoch());
            return ResponseEntity.status(HttpStatus.OK).body(createdMessage);  // Return 200 OK on success
        } catch (ClientErrorException ex) {
            // Handle validation errors (message length, user not found, etc.)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);  // Return 400 Bad Request for client errors
        } catch (Exception ex) {
            // Catch any unexpected error and return 500 Internal Server Error
            ex.printStackTrace();  // Log the error for debugging (use a logger in production)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);  // Return 500 for unexpected errors
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<List<Message>>getAllMessages(){
        List<Message> messages = messageService.getAllMessages();
        return ResponseEntity.status(HttpStatus.OK).body(messages);
    }

    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Optional<Message>> getMessageById(@PathVariable Integer messageId){
        Optional<Message> message = messageService.getMessageById(messageId);
        if (message.isEmpty()){
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(message);
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Integer> deleteMessageById(@PathVariable int messageId){
        int result = messageService.deleteMessageById(messageId);
        if(result == 1){
            return ResponseEntity.status(HttpStatus.OK).body(1);
        }else{
            return ResponseEntity.status(HttpStatus.OK).body(null);
        }
    }

    @PatchMapping("/messages/{messageId}")
    public ResponseEntity<Integer> updateMessage(@RequestBody Message message, @PathVariable int messageId){
        Integer updated = messageService.updateMessage(messageId, message.getMessageText());
        if (updated == 1){
            return ResponseEntity.status(HttpStatus.OK).body(1);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
    @GetMapping("/accounts/{accountId}/messages")
    public ResponseEntity<List<Message>> findMessagesBySameUser(@PathVariable int accountId){
        List<Message> userMessages = messageService.getAllUserMessages(accountId);
        if(userMessages.size() > 0){
            return ResponseEntity.status(HttpStatus.OK).body(userMessages);
        }
        return ResponseEntity.status(HttpStatus.OK).body(userMessages);
    }
}
