package com.message.router.exception;

/**
 * 聊天机器人异常类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class ChatbotException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * 构造函数
     * 
     * @param message 错误信息
     */
    public ChatbotException(String message) {
        super(message);
    }

    /**
     * 构造函数
     * 
     * @param message 错误信息
     * @param cause 原始异常
     */
    public ChatbotException(String message, Throwable cause) {
        super(message, cause);
    }
} 