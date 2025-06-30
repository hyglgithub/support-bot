package com.wok.supportbot.controller;

import cn.hutool.json.JSONUtil;
import com.wok.supportbot.app.AssistantApp;
import com.wok.supportbot.app.ProductInfoApp;
import com.wok.supportbot.entity.ProductInfo;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import java.io.IOException;



public class AiController {

    @Resource
    private AssistantApp assistantApp;
    @Resource
    private ProductInfoApp productInfoApp;


    /**
     * 同步调用 AI 提取结构化商品信息
     *
     * @param message
     * @return
     */
    @GetMapping("/product_info_app/chat/sync")
    public String doChatWithProductInfoAppSync(String message) {
        ProductInfo productInfo = productInfoApp.extractProductInfo(message);
        return JSONUtil.toJsonStr(productInfo);
    }

    /**
     * 同步调用 AI 智能客服应用
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping("/assistant_app/chat/sync")
    public String doChatWithAssistantAppSync(String message, String chatId) {
        return assistantApp.doChat(message, chatId);
    }

    /**
     * SSE 流式调用 AI 智能客服应用
     * 返回Flux 响应式؜对象，并且添加 SSE 对应的 MediaType
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/assistant_app/chat/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> doChatWithLoveAppSSE(String message, String chatId) {
        return assistantApp.doChatByStream(message, chatId);
    }

    /**
     * SSE 流式调用 AI 智能客服应用
     * 返回 Flux 对象，并且؜设置泛型为 ServerSentEvent。使用这种方式可以省略 MediaType
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/assistant_app/chat/server_sent_event")
    public Flux<ServerSentEvent<String>> doChatWithAssistantAppServerSentEvent(String message, String chatId) {
        return assistantApp.doChatByStream(message, chatId)
                .map(chunk -> ServerSentEvent.<String>builder()
                        .data(chunk)
                        .build());
    }

    /**
     * SSE 流式调用 AI 智能客服应用
     * 使用 SSEEmiter，؜通过 send 方法持续向 SseEmitter 发送消息
     *
     * @param message
     * @param chatId
     * @return
     */
    @GetMapping(value = "/assistant_app/chat/sse_emitter")
    public SseEmitter doChatWithAssistantAppServerSseEmitter(String message, String chatId) {
        // 创建一个超时时间较长的 SseEmitter
        SseEmitter sseEmitter = new SseEmitter(180000L); // 3 分钟超时
        // 获取 Flux 响应式数据流并且直接通过订阅推送给 SseEmitter
        assistantApp.doChatByStream(message, chatId)
                .subscribe(chunk -> {
                    try {
                        sseEmitter.send(chunk);
                    } catch (IOException e) {
                        sseEmitter.completeWithError(e);
                    }
                }, sseEmitter::completeWithError, sseEmitter::complete);
        // 返回
        return sseEmitter;
    }
}
