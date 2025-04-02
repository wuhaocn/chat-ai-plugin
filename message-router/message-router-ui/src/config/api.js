export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '';

export const API_ENDPOINTS = {
  chatbots: `${API_BASE_URL}/api/chatbots`,
  messages: {
    history: `${API_BASE_URL}/api/messages/history`,
    send: `${API_BASE_URL}/api/messages/send`
  }
}; 