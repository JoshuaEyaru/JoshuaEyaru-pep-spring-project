package com.example.service;

import javax.naming.AuthenticationException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.entity.Account;
import com.example.entity.Message;
import com.example.exception.ClientErrorException;
import com.example.exception.DuplicateAccountException;
import com.example.exception.ResourceNotFoundException;
import com.example.repository.AccountRepository;

import java.util.List;
import java.util.Optional;;

@Service
@Transactional
public class AccountService {

    private AccountRepository accountRepository;

    @Autowired
    MessageService messageService;

    @Autowired
    public AccountService(AccountRepository accountRepository){
        this.accountRepository = accountRepository;
    }

    public Account register(Account newAccount) {
        // Find the account by username and check if it exists
        Account existingAccount = accountRepository.findByUsername(newAccount.getUsername().trim());
        String username = newAccount.getUsername().trim();
        
        if (existingAccount != null && existingAccount.getUsername().equals(username)) {
            // Username already taken, throw an exception
            throw new DuplicateAccountException("Duplicate account: " + existingAccount.getUsername());
        }
    
        // If username is valid and password length is sufficient
        if (username.length() > 0 && newAccount.getPassword().length() >= 4) {
            return accountRepository.save(newAccount);
        }
    
        // If there was a problem with the username or password
        throw new ClientErrorException("Invalid username or password");
    }
    
    public Account login(String username, String password) throws AuthenticationException {
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new AuthenticationException("Username and password cannot be empty");
        }
    
        Account existingAccount = accountRepository.findByUsername(username.trim());
        if (existingAccount != null && existingAccount.getUsername().equals(username.trim())) {
            
            if (existingAccount.getPassword().equals(password)) {
                return existingAccount;
            }
        }
    
        throw new AuthenticationException("Incorrect username or password");
    }

    public Optional<Account> getExistingUser(int accountId){
        return accountRepository.findById(accountId);
    }
    
    // public Optional<List<Message>> getAllUserMessages(int accountId){
    //     Optional<List<Message>> messages = messageService.findA
    // }
}
