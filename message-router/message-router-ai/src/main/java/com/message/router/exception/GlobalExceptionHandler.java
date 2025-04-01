package com.message.router.exception;

import com.message.router.model.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 全局异常处理类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ChatbotException.class)
    public MessageResponse handleChatbotException(ChatbotException ex) {
        logger.error("聊天机器人异常: {}", ex.getMessage(), ex);
        return MessageResponse.error("CHATBOT_ERROR", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public MessageResponse handleException(Exception ex) {
        logger.error("系统异常: {}", ex.getMessage(), ex);
        return MessageResponse.error("SYSTEM_ERROR", "系统内部错误");
    }
} 