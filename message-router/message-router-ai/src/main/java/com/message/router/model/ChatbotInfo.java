package com.message.router.model;

import java.util.Map;

/**
 * 聊天机器人信息类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class ChatbotInfo {
    private String chatbotId;
    private String chatbotName;
    private String chatbotType;
    private String chatbotHookUrl;
    private Map<String, String> chatbotHookHeaders;
    private String chatbotModel;
    private Double chatbotTemperature;
    private Integer chatbotMaxTokens;
    private String chatbotSystemPrompt;
    private String chatbotStatus;

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        if (chatbotId == null || chatbotId.trim().isEmpty()) {
            throw new IllegalArgumentException("聊天机器人ID不能为空");
        }
        this.chatbotId = chatbotId.trim();
    }

    public String getChatbotName() {
        return chatbotName;
    }

    public void setChatbotName(String chatbotName) {
        this.chatbotName = chatbotName;
    }

    public String getChatbotType() {
        return chatbotType;
    }

    public void setChatbotType(String chatbotType) {
        this.chatbotType = chatbotType;
    }

    public String getChatbotHookUrl() {
        return chatbotHookUrl;
    }

    public void setChatbotHookUrl(String chatbotHookUrl) {
        this.chatbotHookUrl = chatbotHookUrl;
    }

    public Map<String, String> getChatbotHookHeaders() {
        return chatbotHookHeaders;
    }

    public void setChatbotHookHeaders(Map<String, String> chatbotHookHeaders) {
        this.chatbotHookHeaders = chatbotHookHeaders;
    }

    public String getChatbotModel() {
        return chatbotModel;
    }

    public void setChatbotModel(String chatbotModel) {
        this.chatbotModel = chatbotModel;
    }

    public Double getChatbotTemperature() {
        return chatbotTemperature;
    }

    public void setChatbotTemperature(Double chatbotTemperature) {
        this.chatbotTemperature = chatbotTemperature;
    }

    public Integer getChatbotMaxTokens() {
        return chatbotMaxTokens;
    }

    public void setChatbotMaxTokens(Integer chatbotMaxTokens) {
        this.chatbotMaxTokens = chatbotMaxTokens;
    }

    public String getChatbotSystemPrompt() {
        return chatbotSystemPrompt;
    }

    public void setChatbotSystemPrompt(String chatbotSystemPrompt) {
        this.chatbotSystemPrompt = chatbotSystemPrompt;
    }

    public String getChatbotStatus() {
        return chatbotStatus;
    }

    public void setChatbotStatus(String chatbotStatus) {
        this.chatbotStatus = chatbotStatus;
    }
    
} 