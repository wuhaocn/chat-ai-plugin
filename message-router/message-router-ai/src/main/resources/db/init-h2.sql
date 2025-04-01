-- 创建消息表
CREATE TABLE IF NOT EXISTS message (
    message_id VARCHAR(36) PRIMARY KEY,
    conversation_id VARCHAR(36) NOT NULL,
    from_user_id VARCHAR(36) NOT NULL,
    to_user_id VARCHAR(36) NOT NULL,
    content TEXT NOT NULL,
    message_type VARCHAR(20) NOT NULL,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 创建聊天机器人表
CREATE TABLE IF NOT EXISTS chatbots (
    chatbot_id VARCHAR(36) PRIMARY KEY,
    chatbot_name VARCHAR(100) NOT NULL,
    chatbot_type VARCHAR(20) NOT NULL,
    chatbot_hook_url VARCHAR(255),
    chatbot_hook_headers TEXT,
    chatbot_model VARCHAR(50),
    chatbot_temperature DOUBLE,
    chatbot_max_tokens INTEGER,
    chatbot_system_prompt TEXT,
    chatbot_status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_message_conversation_id ON message(conversation_id);
CREATE INDEX IF NOT EXISTS idx_message_from_user_id ON message(from_user_id);
CREATE INDEX IF NOT EXISTS idx_message_to_user_id ON message(to_user_id); 