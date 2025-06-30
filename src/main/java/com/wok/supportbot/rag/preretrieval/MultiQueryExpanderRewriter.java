package com.wok.supportbot.rag.preretrieval;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 多查询扩展器 - MultiQueryExpander
 */
@Component
public class MultiQueryExpanderRewriter {

    private final MultiQueryExpander queryExpander;

    public MultiQueryExpanderRewriter(ChatModel dashscopeChatModel) {
        ChatClient.Builder builder = ChatClient.builder(dashscopeChatModel);
        queryExpander = MultiQueryExpander.builder()
                .chatClientBuilder(builder)
                .numberOfQueries(3)
                .includeOriginal(true) //在扩展查询列表中包含原始查询
                .build();
    }

    /**
     * 执行查询扩展，返回多个查询文本
     *
     * @param prompt 原始查询
     * @return 多个语义不同的查询文本列表
     */
    public List<String> doQueryRewrite(String prompt) {
        List<Query> queries = queryExpander.expand(new Query(prompt));
        return queries.stream()
                .map(Query::text)
                .collect(Collectors.toList());
    }
}
