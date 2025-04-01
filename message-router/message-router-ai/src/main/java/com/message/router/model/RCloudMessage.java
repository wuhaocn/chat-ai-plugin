package com.message.router.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 融云消息类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class RCloudMessage {
    private String fromUserId;
    private String toUserId;
    private String objectName;
    private String content;
    private String channelType;
    private String msgTimestamp;
    private String msgUID;
    private String originalMsgUID;
    private Integer sensitiveType;
    private String source;
    private String busChannel;
    private String[] groupUserIds;

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

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public String getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(String msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public String getMsgUID() {
        return msgUID;
    }

    public void setMsgUID(String msgUID) {
        this.msgUID = msgUID;
    }

    public String getOriginalMsgUID() {
        return originalMsgUID;
    }

    public void setOriginalMsgUID(String originalMsgUID) {
        this.originalMsgUID = originalMsgUID;
    }

    public Integer getSensitiveType() {
        return sensitiveType;
    }

    public void setSensitiveType(Integer sensitiveType) {
        this.sensitiveType = sensitiveType;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getBusChannel() {
        return busChannel;
    }

    public void setBusChannel(String busChannel) {
        this.busChannel = busChannel;
    }

    public String[] getGroupUserIds() {
        return groupUserIds;
    }

    public void setGroupUserIds(String[] groupUserIds) {
        this.groupUserIds = groupUserIds;
    }
} 