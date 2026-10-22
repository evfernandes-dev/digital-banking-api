package com.digitalbanking.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Classe principal da API.
@SpringBootApplication
public class DigitalBankingApiApplication {

    // Sobe o contexto Spring e o servidor web.
    public static void main(String[] args) {
        SpringApplication.run(DigitalBankingApiApplication.class, args);
    }

}
