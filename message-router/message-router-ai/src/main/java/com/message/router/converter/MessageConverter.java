package com.message.router.converter;

import com.message.router.model.Message;
import com.message.router.model.DifyMessage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 消息转换器
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Component
public class MessageConverter {
    
    public DifyMessage toDifyMessage(Message message) {
        DifyMessage difyMessage = new DifyMessage();
        
        // 设置输入参数（必需）
        Map<String, Object> inputs = new HashMap<>();
        difyMessage.setInputs(inputs);
        
        // 设置查询内容（必需）
        String query = message.getContent();
        if (query == null || query.trim().isEmpty()) {
            query = "Empty message";  // 提供默认值
        }
        difyMessage.setQuery(query);
        
        // 设置响应模式（必需）
        difyMessage.setResponseMode("streaming");
        
        difyMessage.setConversationId(UUID.randomUUID().toString());
        
        // 设置用户ID（必需）
        String userId = message.getFromUserId();
        if (userId == null || userId.trim().isEmpty()) {
            userId = "default-user";  // 提供默认值
        }
        difyMessage.setUser(userId);
        
        return difyMessage;
    }
} 