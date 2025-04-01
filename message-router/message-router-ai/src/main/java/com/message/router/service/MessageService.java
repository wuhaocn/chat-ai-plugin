package com.message.router.service;

import com.message.router.model.Message;
import com.message.router.model.MessageResponse;

/**
 * 消息服务接口
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public interface MessageService {
    MessageResponse route(Message message);
} 