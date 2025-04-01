package com.message.router.controller;

import com.message.router.model.Message;
import com.message.router.model.MessageResponse;
import com.message.router.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 消息控制器
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(MessageController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final MessageService messageRouter;

    public MessageController(MessageService messageRouter) {
        this.messageRouter = messageRouter;
    }

    @PostMapping("/send")
    public MessageResponse sendMessage(@RequestBody Message message) {
        return this.messageRouter.route(message);
    }

    @PostMapping(value = "/send", consumes = "application/x-www-form-urlencoded")
    public MessageResponse sendMessageForm(
            @ModelAttribute Message message,
            @RequestParam(required = false) String signTimestamp,
            @RequestParam(required = false) String nonce,
            @RequestParam(required = false) String signature,
            @RequestParam(required = false) String appKey) {
        
        // 设置签名相关参数
        message.setSignTimestamp(signTimestamp);
        message.setNonce(nonce);
        message.setSignature(signature);
        message.setAppKey(appKey);
        
        return this.messageRouter.route(message);
    }
} 