package com.message.router.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import java.util.List;

/**
 * Dify消息类
 * 
 * @author rcloud
 * @since 2025-03-31
 */
public class DifyMessage {
    private Map<String, Object> inputs;
    private String query;
    
    @JsonProperty("response_mode")
    private String responseMode;
    
    @JsonProperty("conversation_id")
    private String conversationId;
    
    private String user;
    private List<FileInfo> files;

    public static class FileInfo {
        private String type;
        private String transferMethod;
        private String url;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTransferMethod() {
            return transferMethod;
        }

        public void setTransferMethod(String transferMethod) {
            this.transferMethod = transferMethod;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public Map<String, Object> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Object> inputs) {
        this.inputs = inputs;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getResponseMode() {
        return responseMode;
    }

    public void setResponseMode(String responseMode) {
        this.responseMode = responseMode;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public List<FileInfo> getFiles() {
        return files;
    }

    public void setFiles(List<FileInfo> files) {
        this.files = files;
    }
} 