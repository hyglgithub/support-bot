package com.wok.supportbot;

import com.wok.supportbot.app.AssistantApp;
import com.wok.supportbot.app.ProductInfoApp;
import com.wok.supportbot.entity.ProductInfo;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

@SpringBootTest
class SupportBotApplicationTests {
    @Resource
    private AssistantApp assistantApp;

    @Autowired
    private ProductInfoApp productInfoApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        // 第一轮：商品咨询
        String message = "你好，我想买一台适合学生用的笔记本电脑，有推荐吗？";
        String answer = assistantApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第二轮：物流问题
        message = "我上周买的那台电脑现在还没到，能查一下物流吗？";
        answer = assistantApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);

        // 第三轮：售后问题
        message = "电脑到了，但有点问题。你刚刚说的售后流程能再说一遍吗？";
        answer = assistantApp.doChat(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    public void testExtractProductInfo() {
        // 模拟爬取的网页内容，建议写简洁但包含关键信息
        String rawContent = "这是商品标题：智能手表Pro 2025，" +
                "描述：这款智能手表支持心率监测和GPS，" +
                "价格：299美元，评分：4.7星，评论数：1567，品牌：TechBrand，分类：电子产品。";

        // 生成随机聊天ID，模拟独立会话
        String chatId = UUID.randomUUID().toString();

        // 调用方法
        ProductInfo productInfo = productInfoApp.extractProductInfo(rawContent, chatId);

        // 断言结果不为空
        Assertions.assertNotNull(productInfo);

        // 断言关键字段合理（你也可以根据实际字段调整）
        Assertions.assertNotNull(productInfo.getTitle());
        Assertions.assertTrue(productInfo.getTitle().contains("智能手表"));

        Assertions.assertNotNull(productInfo.getPrice());
        Assertions.assertTrue(productInfo.getPrice().contains("299"));

        Assertions.assertNotNull(productInfo.getBrand());
        Assertions.assertEquals("TechBrand", productInfo.getBrand());

        // 你可以打印结果，方便调试
        System.out.println("提取的商品信息: " + productInfo);
    }

    @Test
    void doChatWithRag() {
        String chatId = UUID.randomUUID().toString();
        String message = "T恤怎么搭配？";
        String answer =  assistantApp.doChatWithRag(message, chatId);
        Assertions.assertNotNull(answer);
    }


}
