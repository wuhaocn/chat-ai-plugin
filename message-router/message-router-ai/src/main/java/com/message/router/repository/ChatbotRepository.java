package com.message.router.repository;

import com.message.router.entity.ChatbotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 聊天机器人仓库接口
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Repository
public interface ChatbotRepository extends JpaRepository<ChatbotEntity, String> {
    /**
     * 根据聊天机器人ID查找
     * 
     * @param chatbotId 聊天机器人ID
     * @return 聊天机器人实体
     */
    Optional<ChatbotEntity> findByChatbotId(String chatbotId);

    /**
     * 根据聊天机器人类型查找
     * 
     * @param chatbotType 聊天机器人类型
     * @return 聊天机器人列表
     */
    List<ChatbotEntity> findByChatbotType(String chatbotType);

    /**
     * 根据聊天机器人状态查找
     * 
     * @param chatbotStatus 聊天机器人状态
     * @return 聊天机器人列表
     */
    List<ChatbotEntity> findByChatbotStatus(String chatbotStatus);

    /**
     * 检查聊天机器人ID是否存在
     * 
     * @param chatbotId 聊天机器人ID
     * @return 是否存在
     */
    boolean existsByChatbotId(String chatbotId);

    /**
     * 根据聊天机器人ID删除
     * 
     * @param chatbotId 聊天机器人ID
     */
    void deleteByChatbotId(String chatbotId);
} 