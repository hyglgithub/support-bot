package com.wok.supportbot.app;

import com.wok.supportbot.advisor.MyLoggerAdvisor;
import com.wok.supportbot.advisor.ReReadingAdvisor;
import com.wok.supportbot.chatmemory.DatabaseChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @Classname AssistantApp
 * @Description
 * @Version 1.0.0
 * @Date 2025/06/27 14:11
 * @Author lyx
 */
@Component
@Slf4j
public class AssistantApp {

    @Resource
    private VectorStore vectorStore;

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一名电商平台的智能客服助手，负责解答用户关于商品、订单、支付、物流和售后等问题。" +
            "请主动引导用户提供关键信息（如订单号、商品名），并尽量在不转人工的情况下解决问题。保持专业、耐心、礼貌。";

    /**
     * 初始化 ChatClient
     *
     * @param dashscopeChatModel
     */
    public AssistantApp(ChatModel dashscopeChatModel, DatabaseChatMemory chatMemory) {
        // 初始化基于文件的对话记忆
        //String fileDir = System.getProperty("user.dir") + "/tmp/chat-memory";
        //ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        // 初始化基于内存的对话记忆
        // ChatMemory chatMemory = new InMemoryChatMemory();

        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 自定义日志 Advisor，可按需开启
                        new MyLoggerAdvisor()
                        // 自定义推理增强 Advisor，可按需开启
                        //,new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * AI 基础对话（支持多轮对话记忆）
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChat(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }


    /**
     * 和 RAG 知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 应用 RAG 知识库问答
                .advisors(QuestionAnswerAdvisor.builder(vectorStore)
                        // 相似度阈值为 0.0，并返回最相关的前 4 个结果
                        .searchRequest(SearchRequest.builder().similarityThreshold(0.0).topK(4).build())
                        .build())
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }


    @Autowired
    private List<QueryTransformer> queryTransformers;
    @Autowired
    private MultiQueryExpander multiQueryExpander;

    /**
     * 和 RAG 知识库进行对话
     *
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRagEnhance(String message, String chatId) {
        Advisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                //.queryTransformers(queryTransformers)
                //.queryExpander(multiQueryExpander)
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .vectorStore(vectorStore)
                        .similarityThreshold(0.5)
                        .topK(4)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(false) // 不允许模型在没有找到相关文档的情况下也生成回答
                        .build())
                .build();

        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 应用 RAG 知识库问答
                .advisors(retrievalAugmentationAdvisor)
                .call()
                .chatResponse();
        return chatResponse.getResult().getOutput().getText();
    }
}
