package com.message.router.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

/**
 * Dify响应类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class DifyResponse {
    private String event;
    private String taskId;
    private String workflowRunId;
    private String messageId;
    private String conversationId;
    private String answer;
    private Map<String, Object> data;
    private Map<String, Object> metadata;

    @JsonProperty("task_id")
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    @JsonProperty("workflow_run_id")
    public String getWorkflowRunId() {
        return workflowRunId;
    }

    public void setWorkflowRunId(String workflowRunId) {
        this.workflowRunId = workflowRunId;
    }

    @JsonProperty("message_id")
    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    @JsonProperty("conversation_id")
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }
} 