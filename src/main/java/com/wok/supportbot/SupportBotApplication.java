package com.wok.supportbot;

import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = PgVectorStoreAutoConfiguration.class)
public class
SupportBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(SupportBotApplication.class, args);
    }

}
