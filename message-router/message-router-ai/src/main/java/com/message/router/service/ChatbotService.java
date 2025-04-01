package com.message.router.service;

import com.message.router.model.ChatbotInfo;
import java.util.List;
import java.util.Map;

/**
 * 聊天机器人服务接口
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public interface ChatbotService {
    /**
     * 获取聊天机器人
     * 
     * @param chatbotId 聊天机器人ID
     * @return 聊天机器人信息
     */
    ChatbotInfo getChatbot(String chatbotId);

    /**
     * 获取所有聊天机器人
     * 
     * @return 聊天机器人列表
     */
    List<ChatbotInfo> getAllChatbots();

    /**
     * 创建聊天机器人
     * 
     * @param chatbotInfo 聊天机器人信息
     * @return 创建的聊天机器人信息
     */
    ChatbotInfo createChatbot(ChatbotInfo chatbotInfo);

    /**
     * 更新聊天机器人
     * 
     * @param chatbotId 聊天机器人ID
     * @param chatbotInfo 聊天机器人信息
     * @return 更新后的聊天机器人信息
     */
    ChatbotInfo updateChatbot(String chatbotId, ChatbotInfo chatbotInfo);

    /**
     * 删除聊天机器人
     * 
     * @param chatbotId 聊天机器人ID
     */
    void deleteChatbot(String chatbotId);

    /**
     * 更新聊天机器人状态
     * 
     * @param chatbotId 聊天机器人ID
     * @param status 状态
     */
    void updateChatbotStatus(String chatbotId, String status);

    /**
     * 获取聊天机器人统计信息
     * 
     * @return 统计信息
     */
    Map<String, Object> getChatbotStats();
} 