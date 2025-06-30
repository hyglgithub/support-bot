package com.wok.supportbot.rag.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryExpanderConfig {

    @Bean
    public MultiQueryExpander multiQueryExpander(ChatModel dashscopeChatModel) {
        return MultiQueryExpander.builder()
                .chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .numberOfQueries(3)
                .includeOriginal(true)
                .build();
    }
}
