package com.message.router.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.message.router.model.ChatbotInfo;
import com.message.router.model.Message;
import com.message.router.model.MessageResponse;
import com.message.router.service.MessageService;
import com.message.router.service.ChatbotService;
import com.message.router.service.StreamMessageService;
import com.message.router.exception.ChatbotException;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;

/**
 * Dify 消息服务实现类
 * 
 * @author dify
 * @since 2024-03-27
 */
@Service
public class MessageServiceDifyImpl implements MessageService {
    private static final Logger logger = LoggerFactory.getLogger(MessageServiceDifyImpl.class);
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final ChatbotService chatbotService;
    private final StreamMessageService streamMessageService;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;
    private final EventSource.Factory eventSources;

    public MessageServiceDifyImpl(ChatbotService chatbotService,
                                StreamMessageService streamMessageService,
                                ObjectMapper objectMapper) {
        this.chatbotService = chatbotService;
        this.streamMessageService = streamMessageService;
        this.objectMapper = objectMapper;
        
        this.okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
            
        this.eventSources = EventSources.createFactory(okHttpClient);
    }

    @Override
    public MessageResponse route(Message message) {
        try {

            // 校验消息类型
            if (message.getObjectName() == null || 
                (!"RC:TxtMsg".equals(message.getObjectName()) && !"RC:ReferenceMsg".equals(message.getObjectName()))) {
                // 返回200状态码，但在错误描述中说明原因
                return new MessageResponse(message.getMsgUID(),"error", "不支持的消息类型: " + message.getObjectName());
            }
            
            ChatbotInfo chatbot = null;
            try {
                chatbot = chatbotService.getChatbot(message.getToUserId());
            } catch (Exception e){
                //ignore
            }
            if (chatbot == null) {
                // 返回200状态码，但在错误描述中说明原因
                return new MessageResponse(message.getMsgUID(),"error", "聊天机器人不存在: " + message.getToUserId());
            }

            // 解析消息内容
            String query = parseMessageContent(message.getContent());
            if (query == null || query.trim().isEmpty()) {
                return new MessageResponse(message.getMsgUID(), "error", "消息内容不能为空");
            }

            // 构建请求体
            Map<String, Object> requestBodyMap = new HashMap<>();
            requestBodyMap.put("query", query);
            requestBodyMap.put("user", message.getFromUserId());
            requestBodyMap.put("response_mode", "streaming");
            requestBodyMap.put("inputs", new HashMap<>());

            logger.info("发送消息到dify: {}", query);
            try {
                // 将请求体转换为JSON字符串
                String jsonBody = objectMapper.writeValueAsString(requestBodyMap);
                RequestBody body = RequestBody.create(jsonBody, JSON);

                // 构建请求
                var requestBuilder = new Request.Builder()
                    .url(chatbot.getChatbotHookUrl())
                    .post(body)
                    .header("Content-Type", "application/json")
                    .header("Accept", "text/event-stream");

                // 添加所有 chatbot hook headers
                chatbot.getChatbotHookHeaders().forEach(requestBuilder::header);

                var request = requestBuilder.build();

                // 创建 EventSource 监听器
                ChatbotInfo finalChatbot = chatbot;
                var eventSourceListener = new EventSourceListener() {
                    @Override
                    public void onOpen(EventSource eventSource, Response response) {
                        logger.info("Dify SSE 连接已打开");
                    }

                    @Override
                    public void onEvent(EventSource eventSource, String id, String event, String data) {
                        try {
                            if (data != null) {
                                logger.debug("收到 SSE 数据: {}", data);
                                processDifyEvent(data, message, finalChatbot.getChatbotId());
                            }
                        } catch (Exception e) {
                            logger.error("处理事件失败: {}", e.getMessage(), e);
                        }
                    }

                    @Override
                    public void onClosed(EventSource eventSource) {
                        logger.info("Dify SSE 连接已关闭");
                    }

                    @Override
                    public void onFailure(EventSource eventSource, Throwable t, Response response) {
                        logger.error("Dify SSE 连接失败: {}", t.getMessage(), t);
                    }
                };

                // 创建并启动 EventSource
                eventSources.newEventSource(request, eventSourceListener);
                
                // 返回成功响应
                return new MessageResponse(message.getMsgUID(), "success", null);
                
            } catch (Exception e) {
                logger.error("消息处理失败", e);
                return new MessageResponse(message.getMsgUID(), "error", "消息处理失败: " + e.getMessage());
            }
        } catch (Exception e) {
            logger.error("消息处理失败", e);
            return new MessageResponse(message.getMsgUID(),"error", "消息处理失败: " + e.getMessage());
        }
    }

    /**
     * 解析消息内容
     * @param content 原始消息内容
     * @return 解析后的查询内容
     */
    private String parseMessageContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return null;
        }

        try {
            // 尝试解析JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(content);
            
            // 检查是否是RC:TxtMsg格式
            if (jsonNode.has("content")) {
                return jsonNode.get("content").asText();
            }
            
            // 如果不是RC:TxtMsg格式，直接返回原始内容
            return content;
        } catch (Exception e) {
            logger.warn("消息内容不是有效的JSON格式: {}", content);
            // 如果不是JSON格式，直接返回原始内容
            return null;
        }
    }

    private void processDifyEvent(String data, Message message, String chatbotId) throws IOException {
        var event = objectMapper.readTree(data);
        var eventType = event.get("event").asText();
        
        switch (eventType) {
            case "message":
                // 处理消息事件，发送流式消息
                var answer = event.get("answer").asText();
                // 使用Dify返回的conversation_id
                String conversationId = event.get("conversation_id").asText();
                streamMessageService.sendStreamMessage(
                    message.getToUserId(),  // 使用chatbotId作为发送者
                    answer,
                    conversationId,  // 使用Dify返回的conversation_id
                    message.getFromUserId()  // 用户ID作为接收者
                );
                break;
                
            case "message_end":
                // 处理消息结束事件
                var metadata = event.get("metadata");
                if (metadata != null) {
                    var usage = metadata.get("usage");
                    if (usage != null) {
                        logger.info("消息处理完成，使用情况: {}", usage);
                    }
                }
                // 发送完成消息，使用Dify返回的conversation_id
                String endConversationId = event.get("conversation_id").asText();
                streamMessageService.sendCompleteMessage(
                    message.getToUserId(),  // 使用chatbotId作为发送者
                    endConversationId,  // 使用Dify返回的conversation_id
                    message.getFromUserId()  // 用户ID作为接收者
                );
                break;
                
            case "workflow_started":
            case "workflow_finished":
            case "node_started":
            case "node_finished":
                // 处理工作流相关事件
                logger.debug("工作流事件: {} - {}", eventType, event.get("data"));
                break;
                
            default:
                logger.debug("未处理的事件类型: {}", eventType);
        }
    }
} 