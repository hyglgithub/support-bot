package com.wok.supportbot.app;

import com.wok.supportbot.advisor.MyLoggerAdvisor;
import com.wok.supportbot.record.ProductInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.stereotype.Component;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @Classname ProductInfoApp
 * @Description 电商商品信息抽取助手App
 * @Version 1.0.0
 * @Date 2025/06/27
 * @Author lyx
 */
@Component
@Slf4j
public class ProductInfoApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一名电商商品信息抽取助手，" +
            "请从用户提供的商品网页内容中提取标题(title)、描述(description)、价格(price)、评分(rating)、评论数(reviewCount)、品牌(brand)、分类(category)等字段。" +
            "请严格按照JSON格式返回，不要带任何解释和多余内容。";

    public ProductInfoApp(ChatModel dashscopeChatModel) {
        ChatMemory chatMemory = new InMemoryChatMemory();
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        // 可以按需添加日志或其它advisor
                        new MyLoggerAdvisor()
                )
                .build();
    }

    /**
     * 商品信息结构化抽取
     * @param rawContent 爬取的商品网页内容
     * @param chatId 对话ID
     * @return 结构化的商品信息对象
     */
    public ProductInfo extractProductInfo(String rawContent, String chatId) {
        ProductInfo productInfo = chatClient
                .prompt()
                .system(SYSTEM_PROMPT)
                .user(rawContent)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(ProductInfo.class);
        log.info("Extracted product info: {}", productInfo);
        return productInfo;
    }
}
