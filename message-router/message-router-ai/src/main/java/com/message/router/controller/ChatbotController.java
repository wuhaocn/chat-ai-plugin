package com.message.router.controller;

import com.message.router.model.ChatbotInfo;
import com.message.router.service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 聊天机器人控制器
 * 
 * @author rcloud
 * @since 2025-03-31
 */
@RestController
@RequestMapping("/api/chatbots")
public class ChatbotController {
    
    private final ChatbotService chatbotService;
    
    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }
    
    @GetMapping("/{id}")
    public ChatbotInfo getChatbot(@PathVariable String id) {
        return chatbotService.getChatbot(id);
    }
    
    @GetMapping
    public List<ChatbotInfo> getAllChatbots() {
        return chatbotService.getAllChatbots();
    }
    
    @PostMapping
    public ChatbotInfo createChatbot(@RequestBody ChatbotInfo chatbotInfo) {
        return chatbotService.createChatbot(chatbotInfo);
    }
    
    @PutMapping("/{id}")
    public ChatbotInfo updateChatbot(@PathVariable String id, @RequestBody ChatbotInfo chatbotInfo) {
        return chatbotService.updateChatbot(id, chatbotInfo);
    }
    
    @DeleteMapping("/{id}")
    public void deleteChatbot(@PathVariable String id) {
        chatbotService.deleteChatbot(id);
    }
} 