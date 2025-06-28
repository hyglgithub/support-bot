package com.wok.supportbot.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.DocumentRetriever;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class QueryTransformerConfig {

    @Bean
    public QueryTransformer rewriteQueryTransformer(ChatModel dashscopeChatModel) {
        return RewriteQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .build();
    }

    @Bean
    public QueryTransformer translationQueryTransformer(ChatModel dashscopeChatModel) {
        return TranslationQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .targetLanguage("chinese")
                .build();
    }

    @Bean
    public QueryTransformer compressionQueryTransformer(ChatModel dashscopeChatModel) {
        return CompressionQueryTransformer.builder()
                .chatClientBuilder(ChatClient.builder(dashscopeChatModel))
                .build();
    }
}
