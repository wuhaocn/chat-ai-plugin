package com.message.router.constant;

/**
 * 聊天机器人常量类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class ChatbotConstants {
    public static final String CHATBOT_TYPE_DIFY = "dify";
    public static final String CHATBOT_TYPE_RCLOUD = "rcloud";
    
    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_IMAGE = "image";
    public static final String MESSAGE_TYPE_VOICE = "voice";
    
    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_ERROR = "error";
    
    public static final String ERROR_CODE_NOT_FOUND = "CHATBOT_NOT_FOUND";
    public static final String ERROR_CODE_INVALID_CONFIG = "INVALID_CONFIG";
    public static final String ERROR_CODE_API_ERROR = "API_ERROR";
    
    private ChatbotConstants() {
        // 私有构造函数，防止实例化
    }
} 