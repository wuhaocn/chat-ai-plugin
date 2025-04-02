package com.message.router.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 融云配置类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Configuration
@ConfigurationProperties(prefix = "rongcloud")
public class RongCloudConfig {
    private String appKey;
    private String appSecret;
    private StreamMessageConfig streamMessage = new StreamMessageConfig();

    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public StreamMessageConfig getStreamMessage() {
        return streamMessage;
    }

    public void setStreamMessage(StreamMessageConfig streamMessage) {
        this.streamMessage = streamMessage;
    }

    public static class StreamMessageConfig {
        private String apiUrl;

        public String getApiUrl() {
            return apiUrl;
        }

        public void setApiUrl(String apiUrl) {
            this.apiUrl = apiUrl;
        }
    }
} 