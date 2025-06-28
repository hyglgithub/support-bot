package com.wok.supportbot.preretrieval;

import org.springframework.ai.chat.client.ChatClient;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询压缩器 - CompressionQueryTransformer
 */
@Component
public class CompressionQueryRewriter {

    private final QueryTransformer queryTransformer;

    public CompressionQueryRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        queryTransformer = CompressionQueryTransformer.builder()
                .chatClientBuilder(builder)
                .build();
    }

    /**
     * 执行查询压缩（带对话历史）
     *
     * @param prompt 当前查询文本
     * @return 压缩后的查询文本
     */
    public String doQueryRewrite(String prompt, List<Message> history) {
        Query query = Query.builder()
                .text(prompt)
                .history(history)
                .build();

        Query transformed = queryTransformer.transform(query);
        return transformed.text();
    }
}
