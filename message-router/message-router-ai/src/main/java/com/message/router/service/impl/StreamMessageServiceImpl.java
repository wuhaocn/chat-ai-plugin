package com.message.router.service.impl;

import com.message.router.config.RongCloudConfig;
import com.message.router.service.StreamMessageService;
import com.message.router.service.ChatbotService;
import com.message.router.model.ChatbotInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 流式消息服务实现类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@Service
public class StreamMessageServiceImpl implements StreamMessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(StreamMessageServiceImpl.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String APPKEY = "App-Key";
    private static final String NONCE = "Nonce";
    private static final String TIMESTAMP = "Timestamp";
    private static final String SIGNATURE = "Signature";
    private static final String USERAGENT = "User-Agent";
    private static final String STREAM_MESSAGE_PATH = "/v3/message/private/publish_stream.json";
    
    private final OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper;
    private final RongCloudConfig rongCloudConfig;
    private final Map<String, String> messageUIDMap = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> messageSeqMap = new ConcurrentHashMap<>();
    
    @Autowired
    private ChatbotService chatbotService;
    
    public StreamMessageServiceImpl(ObjectMapper objectMapper, RongCloudConfig rongCloudConfig) {
        this.okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.objectMapper = objectMapper;
        this.rongCloudConfig = rongCloudConfig;
    }
    
    private String generateSignature(String appSecret, String nonce, String timestamp) {
        if (appSecret == null || nonce == null || timestamp == null) {
            logger.error("Invalid parameters for signature generation: appSecret={}, nonce={}, timestamp={}", 
                appSecret, nonce, timestamp);
            throw new IllegalArgumentException("Parameters for signature generation cannot be null");
        }
        StringBuilder toSign = new StringBuilder(appSecret).append(nonce).append(timestamp);
        return DigestUtils.sha1Hex(toSign.toString());
    }
    
    private int getNextSeq(String conversationId) {
        return messageSeqMap.computeIfAbsent(conversationId, k -> new AtomicInteger(0)).incrementAndGet();
    }
    
    private String getChatbotName(String chatbotId) {
        try {
            ChatbotInfo chatbot = chatbotService.getChatbot(chatbotId);
            return chatbot != null ? chatbot.getChatbotName() : "AI Assistant";
        } catch (Exception e) {
            logger.error("Failed to get chatbot name for id: {}", chatbotId, e);
            return "AI Assistant";
        }
    }
    
    @Override
    public void sendStreamMessage(String fromUserId, String content, String conversationId, String toUserId) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fromUserId", fromUserId);
            requestBody.put("toUserId", toUserId);
            requestBody.put("objectName", "RC:StreamMsg");
            
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("content", content != null ? content : "");
            contentMap.put("complete", false);
            contentMap.put("seq", getNextSeq(conversationId));
            
            // 如果不是首包，使用已保存的messageUID
            String messageUID = messageUIDMap.get(conversationId);
            if (messageUID != null) {
                contentMap.put("messageUID", messageUID);
            }
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", fromUserId);
            userMap.put("name", getChatbotName(fromUserId));
            userMap.put("portrait", "");
            userMap.put("extra", new HashMap<>());
            
            contentMap.put("user", userMap);
            contentMap.put("extra", new HashMap<>());
            
            requestBody.put("content", contentMap);
            requestBody.put("isPersisted", 1);
            requestBody.put("disableUpdateLastMsg", true);
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(jsonBody, JSON);
            
            String nonce = String.valueOf(Math.random() * 1000000);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String signature = generateSignature(rongCloudConfig.getAppSecret(), nonce, timestamp);
            
            Request request = new Request.Builder()
                .url(rongCloudConfig.getStreamMessage().getApiUrl() + STREAM_MESSAGE_PATH)
                .post(body)
                .header(APPKEY, rongCloudConfig.getAppKey())
                .header(NONCE, nonce)
                .header(TIMESTAMP, timestamp)
                .header(SIGNATURE, signature)
                .header(USERAGENT, "rc-java-sdk/1.0.0")
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Connection", "keep-alive")
                .header("X-Request-ID", UUID.randomUUID().toString().replaceAll("-", ""))
                .build();
            
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to send stream message. Status code: {}, Body: {}", 
                        response.code(),
                        response.body() != null ? response.body().string() : "No body");
                } else {
                    // 如果是首包，保存返回的messageUID
                    if (messageUID == null && response.body() != null) {
                        String responseBody = response.body().string();
                        Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);
                        if (responseMap.containsKey("messageUID")) {
                            messageUIDMap.put(conversationId, (String) responseMap.get("messageUID"));
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error sending stream message: {}", e.getMessage(), e);
        }
    }
    
    @Override
    public void sendCompleteMessage(String fromUserId, String conversationId, String toUserId) {
        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("fromUserId", fromUserId);
            requestBody.put("toUserId", toUserId);
            requestBody.put("objectName", "RC:StreamMsg");
            
            Map<String, Object> contentMap = new HashMap<>();
            contentMap.put("content", "");
            contentMap.put("complete", true);
            contentMap.put("seq", getNextSeq(conversationId));
            
            // 使用已保存的messageUID
            String messageUID = messageUIDMap.get(conversationId);
            if (messageUID != null) {
                contentMap.put("messageUID", messageUID);
            }
            
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("id", fromUserId);
            userMap.put("name", getChatbotName(fromUserId));
            userMap.put("portrait", "");
            userMap.put("extra", new HashMap<>());
            
            contentMap.put("user", userMap);
            contentMap.put("extra", new HashMap<>());
            
            requestBody.put("content", contentMap);
            requestBody.put("isPersisted", 1);
            requestBody.put("disableUpdateLastMsg", true);
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            RequestBody body = RequestBody.create(jsonBody, JSON);
            
            String nonce = String.valueOf(Math.random() * 1000000);
            String timestamp = String.valueOf(System.currentTimeMillis() / 1000);
            String signature = generateSignature(rongCloudConfig.getAppSecret(), nonce, timestamp);
            
            Request request = new Request.Builder()
                .url(rongCloudConfig.getStreamMessage().getApiUrl() + STREAM_MESSAGE_PATH)
                .post(body)
                .header(APPKEY, rongCloudConfig.getAppKey())
                .header(NONCE, nonce)
                .header(TIMESTAMP, timestamp)
                .header(SIGNATURE, signature)
                .header(USERAGENT, "rc-java-sdk/1.0.0")
                .header("Content-Type", "application/json;charset=UTF-8")
                .header("Connection", "keep-alive")
                .header("X-Request-ID", UUID.randomUUID().toString().replaceAll("-", ""))
                .build();
            
            try (Response response = okHttpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    logger.error("Failed to send complete message. Status code: {}, Body: {}", 
                        response.code(),
                        response.body() != null ? response.body().string() : "No body");
                }
            }
            
            // 清理消息ID映射和序列号映射
            messageUIDMap.remove(conversationId);
            messageSeqMap.remove(conversationId);
        } catch (Exception e) {
            logger.error("Error sending complete message: {}", e.getMessage(), e);
        }
    }
} 