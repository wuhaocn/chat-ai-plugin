package com.message.router.service.impl;

import com.message.router.entity.ChatbotEntity;
import com.message.router.model.ChatbotInfo;
import com.message.router.repository.ChatbotRepository;
import com.message.router.service.ChatbotService;
import com.message.router.exception.ChatbotException;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 聊天机器人服务实现类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Service
public class ChatbotServiceImpl implements ChatbotService {
    private static final Logger logger = LoggerFactory.getLogger(ChatbotServiceImpl.class);
    @Autowired
    private ChatbotRepository chatbotRepository;

    private final Cache<String, ChatbotInfo> chatbotCache;

    public ChatbotServiceImpl(ChatbotRepository chatbotRepository) {
        this.chatbotRepository = chatbotRepository;
        this.chatbotCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(5, TimeUnit.SECONDS)
            .build();
    }

    @Override
    @Transactional
    public ChatbotInfo createChatbot(ChatbotInfo chatbotInfo) {
        // 检查ID是否已存在
        if (chatbotInfo.getChatbotId() != null && chatbotRepository.existsByChatbotId(chatbotInfo.getChatbotId())) {
            throw new IllegalArgumentException("Chatbot ID already exists");
        }

        // 创建新的聊天机器人
        ChatbotEntity chatbot = new ChatbotEntity();
        chatbot.setChatbotId(chatbotInfo.getChatbotId() != null ? chatbotInfo.getChatbotId() : UUID.randomUUID().toString());
        chatbot.setChatbotName(chatbotInfo.getChatbotName());
        chatbot.setChatbotType(chatbotInfo.getChatbotType());
        chatbot.setChatbotHookUrl(chatbotInfo.getChatbotHookUrl());
        chatbot.setChatbotHookHeaders(chatbotInfo.getChatbotHookHeaders());
        chatbot.setChatbotModel(chatbotInfo.getChatbotModel());
        chatbot.setChatbotTemperature(chatbotInfo.getChatbotTemperature());
        chatbot.setChatbotMaxTokens(chatbotInfo.getChatbotMaxTokens());
        chatbot.setChatbotSystemPrompt(chatbotInfo.getChatbotSystemPrompt());
        chatbot.setChatbotStatus(chatbotInfo.getChatbotStatus());
        chatbot.setCreatedAt(LocalDateTime.now());
        chatbot.setUpdatedAt(LocalDateTime.now());

        // 保存并返回
        ChatbotEntity savedChatbot = chatbotRepository.save(chatbot);
        return convertToChatbotInfo(savedChatbot);
    }

    @Override
    @Transactional(readOnly = true)
    public ChatbotInfo getChatbot(String chatbotId) {
        if (!StringUtils.hasText(chatbotId)) {
            throw new ChatbotException("聊天机器人ID不能为空");
        }
        
        // 先从缓存中获取
        return chatbotCache.get(chatbotId, key -> {
            logger.info("缓存未命中，从数据库获取聊天机器人，ID: {}", key);
            return chatbotRepository.findByChatbotId(key)
                .map(this::convertToChatbotInfo)
                .orElseThrow(() -> new ChatbotException("聊天机器人不存在: " + key));
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatbotInfo> getAllChatbots() {
        return chatbotRepository.findAll().stream()
                .map(this::convertToChatbotInfo)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ChatbotInfo updateChatbot(String chatbotId, ChatbotInfo chatbotInfo) {
        ChatbotEntity chatbot = chatbotRepository.findByChatbotId(chatbotId)
                .orElseThrow(() -> new IllegalArgumentException("Chatbot not found"));

        // 更新字段
        chatbot.setChatbotName(chatbotInfo.getChatbotName());
        chatbot.setChatbotType(chatbotInfo.getChatbotType());
        chatbot.setChatbotHookUrl(chatbotInfo.getChatbotHookUrl());
        chatbot.setChatbotHookHeaders(chatbotInfo.getChatbotHookHeaders());
        chatbot.setChatbotModel(chatbotInfo.getChatbotModel());
        chatbot.setChatbotTemperature(chatbotInfo.getChatbotTemperature());
        chatbot.setChatbotMaxTokens(chatbotInfo.getChatbotMaxTokens());
        chatbot.setChatbotSystemPrompt(chatbotInfo.getChatbotSystemPrompt());
        chatbot.setChatbotStatus(chatbotInfo.getChatbotStatus());
        chatbot.setUpdatedAt(LocalDateTime.now());

        // 保存并返回
        ChatbotEntity updatedChatbot = chatbotRepository.save(chatbot);
        return convertToChatbotInfo(updatedChatbot);
    }

    @Override
    @Transactional
    public void deleteChatbot(String chatbotId) {
        if (!chatbotRepository.existsByChatbotId(chatbotId)) {
            throw new IllegalArgumentException("Chatbot not found");
        }
        chatbotRepository.deleteByChatbotId(chatbotId);
    }

    @Override
    @Transactional
    public void updateChatbotStatus(String chatbotId, String status) {
        if (!StringUtils.hasText(chatbotId)) {
            throw new ChatbotException("聊天机器人ID不能为空");
        }
        if (!StringUtils.hasText(status)) {
            throw new ChatbotException("状态不能为空");
        }
        if (!isValidStatus(status)) {
            throw new ChatbotException("无效的状态值: " + status);
        }
        logger.info("更新聊天机器人状态，ID: {}, 状态: {}", chatbotId, status);
        var chatbot = chatbotRepository.findByChatbotId(chatbotId)
            .orElseThrow(() -> new ChatbotException("聊天机器人不存在: " + chatbotId));
        chatbot.setChatbotStatus(status);
        chatbotRepository.save(chatbot);
    }

    @Override
    public Map<String, Object> getChatbotStats() {
        logger.info("获取聊天机器人统计信息");
        var stats = new HashMap<String, Object>();
        var allChatbots = chatbotRepository.findAll();
        stats.put("total", allChatbots.size());
        stats.put("active", allChatbots.stream()
            .filter(c -> "active".equals(c.getChatbotStatus()))
            .count());
        stats.put("inactive", allChatbots.stream()
            .filter(c -> "inactive".equals(c.getChatbotStatus()))
            .count());
        return stats;
    }

    private boolean isValidStatus(String status) {
        return "active".equals(status) || "inactive".equals(status);
    }

    /**
     * 将实体转换为信息对象
     */
    private ChatbotInfo convertToChatbotInfo(ChatbotEntity entity) {
        ChatbotInfo info = new ChatbotInfo();
        info.setChatbotId(entity.getChatbotId());
        info.setChatbotName(entity.getChatbotName());
        info.setChatbotType(entity.getChatbotType());
        info.setChatbotHookUrl(entity.getChatbotHookUrl());
        info.setChatbotHookHeaders(entity.getChatbotHookHeaders());
        info.setChatbotModel(entity.getChatbotModel());
        info.setChatbotTemperature(entity.getChatbotTemperature());
        info.setChatbotMaxTokens(entity.getChatbotMaxTokens());
        info.setChatbotSystemPrompt(entity.getChatbotSystemPrompt());
        info.setChatbotStatus(entity.getChatbotStatus());
        return info;
    }
} 