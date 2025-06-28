package com.wok.supportbot.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.wok.supportbot.handler.PostgresJsonTypeHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ai.chat.messages.MessageType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "chat_message", autoResultMap = true)
public class ChatMessage implements Serializable {

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * 会话ID
     */
    @TableField("conversation_id")
    private String conversationId;
    
    /**
     * 消息类型
     */
    @TableField("message_type")
    private MessageType messageType;

    /**
     * 消息内容
     */
    @TableField("content")
    private String content;

    /**
     * 元数据
     */
    @TableField(value = "metadata", typeHandler = PostgresJsonTypeHandler.class)
    private Map<String, Object> metadata;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 更新时间
     */
    @Version
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    /**
     * 是否删除 false-未删除 true-已删除
     */
    @TableField("is_delete")
    @TableLogic
    private boolean isDelete;
}