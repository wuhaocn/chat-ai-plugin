package com.message.router.model;

/**
 * 消息响应类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class MessageResponse {
    private String messageId;
    private String status;
    private String errorMessage;

    public MessageResponse() {
    }

    public MessageResponse(String messageId, String status, String errorMessage) {
        this.messageId = messageId;
        this.status = status;
        this.errorMessage = errorMessage;
    }

    public static MessageResponse error(String code, String message) {
        return new MessageResponse(null, code, message);
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "MessageResponse{" +
                "messageId='" + messageId + '\'' +
                ", status='" + status + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
} 