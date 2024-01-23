package com.appgilson.libraryapi.service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface EmailService {
    void sendMails(String message, List<String> mailsList);
}
