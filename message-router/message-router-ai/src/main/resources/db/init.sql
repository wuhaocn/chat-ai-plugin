CREATE TABLE IF NOT EXISTS chatbots (
    chatbot_id VARCHAR(255) PRIMARY KEY,
    chatbot_name VARCHAR(255) NOT NULL,
    chatbot_type VARCHAR(50) NOT NULL,
    chatbot_hook_url VARCHAR(255) NOT NULL,
    chatbot_hook_headers TEXT,
    chatbot_model VARCHAR(50),
    chatbot_temperature DOUBLE,
    chatbot_max_tokens INTEGER,
    chatbot_system_prompt TEXT,
    chatbot_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
); 