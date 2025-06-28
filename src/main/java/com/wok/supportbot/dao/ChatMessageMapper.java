package com.wok.supportbot.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wok.supportbot.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
