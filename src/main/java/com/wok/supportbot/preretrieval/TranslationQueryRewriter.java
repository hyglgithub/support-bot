package com.wok.supportbot.preretrieval;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.stereotype.Component;

/**
 * 查询翻译器 - TranslationQueryTransformer
 */
@Component
public class TranslationQueryRewriter {

    private final QueryTransformer queryTransformer;

    public TranslationQueryRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        queryTransformer = TranslationQueryTransformer.builder()
                .chatClientBuilder(builder)
                .targetLanguage("chinese")
                .build();
    }

    /**
     * 执行查询翻译
     *
     * @param prompt 原始查询文本
     * @return 翻译后的查询文本
     */
    public String doQueryRewrite(String prompt) {
        Query query = new Query(prompt);
        Query transformedQuery = queryTransformer.transform(query);
        return transformedQuery.text();
    }
}
