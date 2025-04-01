package com.message.router.entity;

import com.message.router.converter.JsonConverter;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 聊天机器人实体类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Entity
@Table(name = "chatbots")
public class ChatbotEntity {
    @Id
    @Column(name = "chatbot_id", length = 36)
    private String chatbotId;

    @Column(name = "chatbot_name", length = 100, nullable = false)
    private String chatbotName;

    @Column(name = "chatbot_type", length = 20, nullable = false)
    private String chatbotType;

    @Column(name = "chatbot_hook_url", length = 255)
    private String chatbotHookUrl;

    @Column(name = "chatbot_hook_headers", columnDefinition = "TEXT")
    @Convert(converter = JsonConverter.class)
    private Map<String, String> chatbotHookHeaders;

    @Column(name = "chatbot_model", length = 50)
    private String chatbotModel;

    @Column(name = "chatbot_temperature")
    private Double chatbotTemperature;

    @Column(name = "chatbot_max_tokens")
    private Integer chatbotMaxTokens;

    @Column(name = "chatbot_system_prompt", columnDefinition = "TEXT")
    private String chatbotSystemPrompt;

    @Column(name = "chatbot_status", length = 20, nullable = false)
    private String chatbotStatus;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public String getChatbotId() {
        return chatbotId;
    }

    public void setChatbotId(String chatbotId) {
        this.chatbotId = chatbotId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
} 