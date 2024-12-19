package com.example.service;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.entity.Account;
import com.example.service.AccountService;

import java.util.List;
import ch.qos.logback.classic.Logger;

import com.example.entity.Message;
import com.example.exception.ClientErrorException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.MessageRepository;
import java.util.Optional;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    AccountService accountService;


    public Message createMessage(int postedBy, String messageText, long timePostedEpoch) {
        // Validate message length
        if (messageText == null || messageText.isEmpty() || messageText.length() > 255) {
            throw new ClientErrorException("Missing or invalid message length");
        }
        
        // Check if the user exists
        Optional<Account> existing = accountService.getExistingUser(postedBy);
        if (!existing.isPresent()) { // Corrected condition: if the user is not found
            throw new ClientErrorException("User not found");
        }
    
        messageText = messageText.trim();
        
        // Create a new message if the user exists
        Message newMessage = new Message();
        newMessage.setPostedBy(postedBy);
        newMessage.setMessageText(messageText);
        newMessage.setTimePostedEpoch(timePostedEpoch);
        messageRepository.save(newMessage); // Save message to database
        
        return newMessage;
    }
    
    public List<Message> getAllMessages(){
        return (List<Message>)messageRepository.findAll();
    }
    
    public Optional<Message> getMessageById(int messageId){
        return messageRepository.findById(messageId);
    }

    public Integer deleteMessageById(int messageId){
        Optional<Message>message = messageRepository.findById(messageId);
        if(message.isPresent()){
            messageRepository.deleteById(messageId);
            return 1;
        }
        return 0;
    }
    public Integer updateMessage(int messageId, String messageText){
        if (messageText == null || messageText.isEmpty() || messageText.length() > 255) {
            throw new ClientErrorException("Missing or invalid message length");
        }
        Optional<Message> message = messageRepository.findById(messageId);
        if(message.isPresent()){
            Message existingMessage = message.get();
            existingMessage.setMessageText(messageText);
            return 1;
        }
        throw new ClientErrorException("Message with messageId " + messageId + " doesn't exist");
    }

    public List<Message> getAllUserMessages(int accountId){
        // to get messages by same user, we need to 
        List<Message> messages = messageRepository.findAllByPostedBy(accountId);
        return messages;
    }
}
