package com.message.router.service;

public interface StreamMessageService {
    /**
     * 发送流式消息
     * @param toUserId 接收者用户ID
     * @param content 消息内容
     * @param conversationId 会话ID
     * @param fromUserId 发送者用户ID
     */
    void sendStreamMessage(String fromUserId, String content, String conversationId, String toUserId);
    
    /**
     * 发送完成消息
     * @param toUserId 接收者用户ID
     * @param conversationId 会话ID
     * @param fromUserId 发送者用户ID
     */
    void sendCompleteMessage(String fromUserId, String conversationId, String toUserId);
} 