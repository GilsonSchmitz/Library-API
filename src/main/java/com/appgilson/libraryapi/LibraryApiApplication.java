package com.appgilson.libraryapi;

import com.appgilson.libraryapi.service.EmailService;
import jakarta.validation.constraints.Email;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;


@SpringBootApplication
@EnableScheduling
public class LibraryApiApplication {

    @Autowired
    private EmailService emailService;

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner runner() {
        return args -> {
            List<String> emails = Arrays.asList("29000a8f9e-64c8d3@inbox.mailtrap.io");
            emailService.sendMails("Testando servico de emails", emails);
            System.out.println("Enviado com sucesso!");
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}


