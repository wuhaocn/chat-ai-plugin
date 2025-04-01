package com.message.router.model;

import java.util.Map;

/**
 * 消息模型类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class Message {
    private String channelType;      // 频道类型
    private String fromUserId;       // 发送者ID
    private String toUserId;         // 接收者ID
    private String msgTimestamp;     // 消息时间戳
    private String timestamp;        // 时间戳
    private String objectName;       // 消息类型
    private String content;          // 消息内容
    private String sensitiveType;    // 敏感词类型
    private String source;           // 消息来源
    private String msgUID;           // 消息唯一ID
    private String clientIp;         // 客户端IP
    private String signTimestamp;    // 签名时间戳
    private String nonce;            // 随机数
    private String signature;        // 签名
    private String appKey;           // 应用Key

    // Getters and Setters
    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(String msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSensitiveType() {
        return sensitiveType;
    }

    public void setSensitiveType(String sensitiveType) {
        this.sensitiveType = sensitiveType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getMsgUID() {
        return msgUID;
    }

    public void setMsgUID(String msgUID) {
        this.msgUID = msgUID;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getSignTimestamp() {
        return signTimestamp;
    }

    public void setSignTimestamp(String signTimestamp) {
        this.signTimestamp = signTimestamp;
    }

    public String getNonce() {
        return nonce;
    }

    public void setNonce(String nonce) {
        this.nonce = nonce;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
} 