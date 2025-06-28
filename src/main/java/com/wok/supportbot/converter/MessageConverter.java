package com.wok.supportbot.converter;

import com.wok.supportbot.entity.ChatMessage;
import org.springframework.ai.chat.messages.*;

import java.util.List;
import java.util.Map;

/**
 * @Classname MessageConverter
 * @Description
 * @Version 1.0.0
 * @Date 2025/06/28 13:30
 * @Author lyx
 */
public class MessageConverter {

    /**
     * 将 Message 转换为 ChatMessage
     */
    public static ChatMessage toChatMessage(Message message, String conversationId) {
        return ChatMessage.builder()
                .conversationId(conversationId)
                .messageType(message.getMessageType())
                .content(message.getText())
                .metadata(message.getMetadata())
                .build();
    }

    /**
     * 将 ChatMessage 转换为 Message
     */
    public static Message toMessage(ChatMessage chatMessage) {
        MessageType messageType = chatMessage.getMessageType();
        String text = chatMessage.getContent();
        Map<String, Object> metadata = chatMessage.getMetadata();
        return switch (messageType) {
            case USER -> new UserMessage(text);
            case ASSISTANT -> new AssistantMessage(text, metadata);
            case SYSTEM -> new SystemMessage(text);
            case TOOL -> new ToolResponseMessage(List.of(), metadata);
        };
    }
}
