package com.wok.supportbot.repository;

import com.baomidou.mybatisplus.extension.repository.CrudRepository;
import com.wok.supportbot.dao.ChatMessageMapper;
import com.wok.supportbot.entity.ChatMessage;
import org.springframework.stereotype.Component;

@Component
public class ChatMessageRepository extends CrudRepository<ChatMessageMapper, ChatMessage> {
    
}
