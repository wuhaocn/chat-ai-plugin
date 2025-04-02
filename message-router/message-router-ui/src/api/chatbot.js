import { defineStore } from 'pinia'
import { API_ENDPOINTS } from '../config/api'

export const useChatbotStore = defineStore('chatbot', {
  state: () => ({
    chatbots: [],
    loading: false,
    error: null
  }),

  actions: {
    async fetchChatbots() {
      this.loading = true
      try {
        const response = await fetch(API_ENDPOINTS.chatbots)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data = await response.json()
        this.chatbots = data
        return data
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async createChatbot(chatbotData) {
      this.loading = true
      try {
        const response = await fetch(API_ENDPOINTS.chatbots, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(chatbotData)
        })
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data = await response.json()
        this.chatbots.push(data)
        return data
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async updateChatbot(id, chatbotData) {
      this.loading = true
      try {
        const response = await fetch(`${API_ENDPOINTS.chatbots}/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(chatbotData)
        })
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        const data = await response.json()
        const index = this.chatbots.findIndex(c => c.chatbotId === id)
        if (index !== -1) {
          this.chatbots[index] = data
        }
        return data
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    },

    async deleteChatbot(id) {
      this.loading = true
      try {
        const response = await fetch(`${API_ENDPOINTS.chatbots}/${id}`, {
          method: 'DELETE'
        })
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`)
        }
        this.chatbots = this.chatbots.filter(c => c.chatbotId !== id)
      } catch (error) {
        this.error = error.message
        throw error
      } finally {
        this.loading = false
      }
    }
  }
}) 