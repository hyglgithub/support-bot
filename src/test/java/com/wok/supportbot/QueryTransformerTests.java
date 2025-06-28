package com.wok.supportbot;

import com.wok.supportbot.preretrieval.CompressionQueryRewriter;
import com.wok.supportbot.preretrieval.MultiQueryExpanderRewriter;
import com.wok.supportbot.preretrieval.RewriteQueryRewriter;
import com.wok.supportbot.preretrieval.TranslationQueryRewriter;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.rag.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class QueryTransformerTests {

    @Autowired
    private TranslationQueryRewriter translationQueryRewriter;

    @Autowired
    private CompressionQueryRewriter compressionQueryRewriter;

    @Autowired
    private MultiQueryExpanderRewriter multiQueryExpanderRewriter;

    @Autowired
    private RewriteQueryRewriter rewriteQueryRewriter;

    @Test
    void testRewriteQueryRewriter() {
        // 构造输入
        String originalQuery = "我想买一部拍照效果好的手机";
        // 执行
        String rewritten = rewriteQueryRewriter.doQueryRewrite(originalQuery);
        // 输出结果
        System.out.println("重写结果：" + rewritten);
    }

    @Test
    public void testTranslationQueryRewriter() {
        String prompt = "I want to buy a lightweight laptop suitable for students.";
        String result = translationQueryRewriter.doQueryRewrite(prompt);
        System.out.println("Translation result: " + result);
    }

    @Test
    public void testCompressionQueryRewriter() {
        // 当前追问，用户说得很模糊
        String prompt = "那这款的电池续航如何？";

        // 多轮上下文，用户逐渐缩小目标
        List<Message> history = List.of(
                new UserMessage("我想买一台适合出差用的轻薄笔记本"),
                new AssistantMessage("你可以看看戴尔 XPS 13，性能不错而且轻便"),
                new UserMessage("能不能推荐一款支持长续航的？"),
                new AssistantMessage("荣耀 MagicBook X16 电池续航表现优秀，适合长时间外出使用")
        );
        // 执行压缩
        String result = compressionQueryRewriter.doQueryRewrite(prompt, history);
        // 输出压缩后的独立查询
        System.out.println("Compression result: " + result);
    }


    @Test
    public void testMultiQueryExpanderRewriter() {
        String prompt = "推荐一些适合夏天穿的男士T恤";
        List<String> expandedQueries = multiQueryExpanderRewriter.doQueryRewrite(prompt);
        System.out.println("Expanded queries:");
        expandedQueries.forEach(System.out::println);
    }
}
